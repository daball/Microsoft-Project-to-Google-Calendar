/*
 * Copyright (c) 1996, 2007, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package sun.awt.windows;

import java.awt.*;
import java.awt.peer.*;
import java.awt.image.VolatileImage;
import sun.awt.RepaintArea;
import sun.awt.CausedFocusEvent;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.ToolkitImage;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import java.awt.image.ColorModel;
import java.awt.event.PaintEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import sun.awt.Win32GraphicsConfig;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.opengl.OGLSurfaceData;
import sun.awt.DisplayChangedListener;
import sun.awt.PaintEventDispatcher;
import sun.awt.event.IgnorePaintEvent;

import java.awt.dnd.DropTarget;
import java.awt.dnd.peer.DropTargetPeer;
import sun.awt.ComponentAccessor;

import java.util.logging.*;

import java.util.logging.*;


public abstract class WComponentPeer extends WObjectPeer
    implements ComponentPeer, DropTargetPeer, DisplayChangedListener
{
    /**
     * Handle to native window
     */
    protected volatile long hwnd;

    private static final Logger log = Logger.getLogger("sun.awt.windows.WComponentPeer");
    private static final Logger shapeLog = Logger.getLogger("sun.awt.windows.shape.WComponentPeer");

    static {
        wheelInit();
    }

    // Only actually does stuff if running on 95
    native static void wheelInit();

    // ComponentPeer implementation
    SurfaceData surfaceData;

    private RepaintArea paintArea;

    protected Win32GraphicsConfig winGraphicsConfig;

    boolean isLayouting = false;
    boolean paintPending = false;
    int     oldWidth = -1;
    int     oldHeight = -1;
    private int numBackBuffers = 0;
    private VolatileImage backBuffer = null;

    // foreground, background and color are cached to avoid calling back
    // into the Component.
    private Color foreground;
    private Color background;
    private Font font;

    public native boolean isObscured();
    public boolean canDetermineObscurity() { return true; }

    // DropTarget support

    int nDropTargets;
    long nativeDropTargetContext; // native pointer

    public synchronized native void pShow();
    public synchronized native void hide();
    public synchronized native void enable();
    public synchronized native void disable();

    public long getHWnd() {
        return hwnd;
    }

    /* New 1.1 API */
    public native Point getLocationOnScreen();

    /* New 1.1 API */
    public void setVisible(boolean b) {
        if (b) {
            show();
        } else {
            hide();
        }
    }

    public void show() {
        Dimension s = ((Component)target).getSize();
        oldHeight = s.height;
        oldWidth = s.width;
        pShow();
    }

    /* New 1.1 API */
    public void setEnabled(boolean b) {
        if (b) {
            enable();
        } else {
            disable();
        }
    }

    public int serialNum = 0;

    private native void reshapeNoCheck(int x, int y, int width, int height);

    /* New 1.1 API */
    public void setBounds(int x, int y, int width, int height, int op) {
        // Should set paintPending before reahape to prevent
        // thread race between paint events
        // Native components do redraw after resize
        paintPending = (width != oldWidth) || (height != oldHeight);

        if ( (op & NO_EMBEDDED_CHECK) != 0 ) {
            reshapeNoCheck(x, y, width, height);
        } else {
            reshape(x, y, width, height);
        }
        if ((width != oldWidth) || (height != oldHeight)) {
            // Only recreate surfaceData if this setBounds is called
            // for a resize; a simple move should not trigger a recreation
            try {
                replaceSurfaceData();
            } catch (InvalidPipeException e) {
                // REMIND : what do we do if our surface creation failed?
            }
            oldWidth = width;
            oldHeight = height;
        }

        serialNum++;
    }

    /*
     * Called from native code (on Toolkit thread) in order to
     * dynamically layout the Container during resizing
     */
    void dynamicallyLayoutContainer() {
        // If we got the WM_SIZING, this must be a Container, right?
        // In fact, it must be the top-level Container.
        if (log.isLoggable(Level.FINE)) {
            Container parent = WToolkit.getNativeContainer((Component)target);
            if (parent != null) {
                log.log(Level.FINE, "Assertion (parent == null) failed");
            }
        }
        final Container cont = (Container)target;

        WToolkit.executeOnEventHandlerThread(cont, new Runnable() {
            public void run() {
                // Discarding old paint events doesn't seem to be necessary.
                cont.invalidate();
                cont.validate();

                if (surfaceData instanceof OGLSurfaceData) {
                    // 6290245: When OGL is enabled, it is necessary to
                    // replace the SurfaceData for each dynamic layout
                    // request so that the OGL viewport stays in sync
                    // with the window bounds.
                    try {
                        replaceSurfaceData();
                    } catch (InvalidPipeException e) {
                        // REMIND: this is unlikely to occur for OGL, but
                        // what do we do if surface creation fails?
                    }
                }

                // Forcing a paint here doesn't seem to be necessary.
                // paintDamagedAreaImmediately();
            }
        });
    }

    /*
     * Paints any portion of the component that needs updating
     * before the call returns (similar to the Win32 API UpdateWindow)
     */
    void paintDamagedAreaImmediately() {
        // force Windows to send any pending WM_PAINT events so
        // the damage area is updated on the Java side
        updateWindow();
        // make sure paint events are transferred to main event queue
        // for coalescing
        WToolkit.getWToolkit().flushPendingEvents();
        // paint the damaged area
        paintArea.paint(target, shouldClearRectBeforePaint());
    }

    native synchronized void updateWindow();

    public void paint(Graphics g) {
        ((Component)target).paint(g);
    }

    public void repaint(long tm, int x, int y, int width, int height) {
    }

    private static final double BANDING_DIVISOR = 4.0;
    private native int[] createPrintedPixels(int srcX, int srcY,
                                             int srcW, int srcH);
    public void print(Graphics g) {

        Component comp = (Component)target;

        // To conserve memory usage, we will band the image.

        int totalW = comp.getWidth();
        int totalH = comp.getHeight();

        int hInc = (int)(totalH / BANDING_DIVISOR);
        if (hInc == 0) {
            hInc = totalH;
        }

        for (int startY = 0; startY < totalH; startY += hInc) {
            int endY = startY + hInc - 1;
            if (endY >= totalH) {
                endY = totalH - 1;
            }
            int h = endY - startY + 1;

            int[] pix = createPrintedPixels(0, startY, totalW, h);
            if (pix != null) {
                BufferedImage bim = new BufferedImage(totalW, h,
                                              BufferedImage.TYPE_INT_RGB);
                bim.setRGB(0, 0, totalW, h, pix, 0, totalW);
                g.drawImage(bim, 0, startY, null);
                bim.flush();
            }
        }

        comp.print(g);
    }

    public void coalescePaintEvent(PaintEvent e) {
        Rectangle r = e.getUpdateRect();
        if (!(e instanceof IgnorePaintEvent)) {
            paintArea.add(r, e.getID());
        }

        if (log.isLoggable(Level.FINEST)) {
            switch(e.getID()) {
            case PaintEvent.UPDATE:
                log.log(Level.FINEST, "coalescePaintEvent: UPDATE: add: x = " +
                    r.x + ", y = " + r.y + ", width = " + r.width + ", height = " + r.height);
                return;
            case PaintEvent.PAINT:
                log.log(Level.FINEST, "coalescePaintEvent: PAINT: add: x = " +
                    r.x + ", y = " + r.y + ", width = " + r.width + ", height = " + r.height);
                return;
            }
        }
    }

    public synchronized native void reshape(int x, int y, int width, int height);

    // returns true if the event has been handled and shouldn't be propagated
    // though handleEvent method chain - e.g. WTextFieldPeer returns true
    // on handling '\n' to prevent it from being passed to native code
    public boolean handleJavaKeyEvent(KeyEvent e) { return false; }

    native void nativeHandleEvent(AWTEvent e);

    public void handleEvent(AWTEvent e) {
        int id = e.getID();

        if (((Component)target).isEnabled() && (e instanceof KeyEvent) && !((KeyEvent)e).isConsumed())  {
            if (handleJavaKeyEvent((KeyEvent)e)) {
                return;
            }
        }

        switch(id) {
            case PaintEvent.PAINT:
                // Got native painting
                paintPending = false;
                // Fallthrough to next statement
            case PaintEvent.UPDATE:
                // Skip all painting while layouting and all UPDATEs
                // while waiting for native paint
                if (!isLayouting && ! paintPending) {
                    paintArea.paint(target,shouldClearRectBeforePaint());
                }
                return;
            default:
            break;
        }

        // Call the native code
        nativeHandleEvent(e);
    }

    public Dimension getMinimumSize() {
        return ((Component)target).getSize();
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    // Do nothing for heavyweight implementation
    public void layout() {}

    public Rectangle getBounds() {
        return ((Component)target).getBounds();
    }

    public boolean isFocusable() {
        return false;
    }

    /*
     * Return the GraphicsConfiguration associated with this peer, either
     * the locally stored winGraphicsConfig, or that of the target Component.
     */
    public GraphicsConfiguration getGraphicsConfiguration() {
        if (winGraphicsConfig != null) {
            return winGraphicsConfig;
        }
        else {
            // we don't need a treelock here, since
            // Component.getGraphicsConfiguration() gets it itself.
            return ((Component)target).getGraphicsConfiguration();
        }
    }

    public SurfaceData getSurfaceData() {
        return surfaceData;
    }

    /**
     * Creates new surfaceData object and invalidates the previous
     * surfaceData object.
     * Replacing the surface data should never lock on any resources which are
     * required by other threads which may have them and may require
     * the tree-lock.
     * This is a degenerate version of replaceSurfaceData(numBackBuffers), so
     * just call that version with our current numBackBuffers.
     */
    public void replaceSurfaceData() {
        replaceSurfaceData(this.numBackBuffers);
    }

    /**
     * Multi-buffer version of replaceSurfaceData.  This version is called
     * by createBuffers(), which needs to acquire the same locks in the same
     * order, but also needs to perform additional functions inside the
     * locks.
     */
    public void replaceSurfaceData(int newNumBackBuffers) {
        synchronized(((Component)target).getTreeLock()) {
            synchronized(this) {
                if (pData == 0) {
                    return;
                }
                numBackBuffers = newNumBackBuffers;
                SurfaceData oldData = surfaceData;
                Win32GraphicsConfig gc =
                    (Win32GraphicsConfig)getGraphicsConfiguration();
                surfaceData = gc.createSurfaceData(this, numBackBuffers);
                if (oldData != null) {
                    oldData.invalidate();
                    // null out the old data to make it collected faster
                    oldData = null;
                }

                if (backBuffer != null) {
                    // this will remove the back buffer from the
                    // display change notification list
                    backBuffer.flush();
                    backBuffer = null;
                }

                if (numBackBuffers > 0) {
                    backBuffer = gc.createBackBuffer(this);
                }
            }
        }
    }

    public void replaceSurfaceDataLater() {
        Runnable r = new Runnable() {
            public void run() {
                // Shouldn't do anything if object is disposed in meanwhile
                // No need for sync as disposeAction in Window is performed
                // on EDT
                if (!isDisposed()) {
                    try {
                        replaceSurfaceData();
                    } catch (InvalidPipeException e) {
                    // REMIND : what do we do if our surface creation failed?
                    }
                }
            }
        };
        // Fix 6255371.
        if (!PaintEventDispatcher.getPaintEventDispatcher().queueSurfaceDataReplacing((Component)target, r)) {
            postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), r));
        }
    }

    /**
     * From the DisplayChangedListener interface.
     *
     * Called after a change in the display mode.  This event
     * triggers replacing the surfaceData object (since that object
     * reflects the current display depth information, which has
     * just changed).
     */
    public void displayChanged() {
        try {
            replaceSurfaceData();
        } catch (InvalidPipeException e) {
            // REMIND : what do we do if our surface creation failed?
        }
    }

    /**
     * Part of the DisplayChangedListener interface: components
     * do not need to react to this event
     */
    public void paletteChanged() {
    }

    //This will return null for Components not yet added to a Container
    public ColorModel getColorModel() {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        if (gc != null) {
            return gc.getColorModel();
        }
        else {
            return null;
        }
    }

    //This will return null for Components not yet added to a Container
    public ColorModel getDeviceColorModel() {
        Win32GraphicsConfig gc =
            (Win32GraphicsConfig)getGraphicsConfiguration();
        if (gc != null) {
            return gc.getDeviceColorModel();
        }
        else {
            return null;
        }
    }

    //Returns null for Components not yet added to a Container
    public ColorModel getColorModel(int transparency) {
//      return WToolkit.config.getColorModel(transparency);
        GraphicsConfiguration gc = getGraphicsConfiguration();
        if (gc != null) {
            return gc.getColorModel(transparency);
        }
        else {
            return null;
        }
    }
    public java.awt.Toolkit getToolkit() {
        return Toolkit.getDefaultToolkit();
    }

    // fallback default font object
    final static Font defaultFont = new Font(Font.DIALOG, Font.PLAIN, 12);

    public Graphics getGraphics() {
        SurfaceData surfaceData = this.surfaceData;
        if (!isDisposed() && surfaceData != null) {
            /* Fix for bug 4746122. Color and Font shouldn't be null */
            Color bgColor = background;
            if (bgColor == null) {
                bgColor = SystemColor.window;
            }
            Color fgColor = foreground;
            if (fgColor == null) {
                fgColor = SystemColor.windowText;
            }
            Font font = this.font;
            if (font == null) {
                font = defaultFont;
            }
            return new SunGraphics2D(surfaceData, fgColor, bgColor, font);
        }
        return null;
    }
    public FontMetrics getFontMetrics(Font font) {
        return WFontMetrics.getFontMetrics(font);
    }

    private synchronized native void _dispose();
    protected void disposeImpl() {
        SurfaceData oldData = surfaceData;
        surfaceData = null;
        oldData.invalidate();
        // remove from updater before calling targetDisposedPeer
        WToolkit.targetDisposedPeer(target, this);
        _dispose();
    }

    public synchronized void setForeground(Color c) {
        foreground = c;
        _setForeground(c.getRGB());
    }

    public synchronized void setBackground(Color c) {
        background = c;
        _setBackground(c.getRGB());
    }

    public native void _setForeground(int rgb);
    public native void _setBackground(int rgb);

    public synchronized void setFont(Font f) {
        font = f;
        _setFont(f);
    }
    public synchronized native void _setFont(Font f);
    public final void updateCursorImmediately() {
        WGlobalCursorManager.getCursorManager().updateCursorImmediately();
    }

    native static boolean processSynchronousLightweightTransfer(Component heavyweight, Component descendant,
                                                                boolean temporary, boolean focusedWindowChangeAllowed,
                                                                long time);
    public boolean requestFocus
        (Component lightweightChild, boolean temporary,
         boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
        if (processSynchronousLightweightTransfer((Component)target, lightweightChild, temporary,
                                                                      focusedWindowChangeAllowed, time)) {
            return true;
        } else {
            return _requestFocus(lightweightChild, temporary, focusedWindowChangeAllowed, time, cause);
        }
    }
    public native boolean _requestFocus
        (Component lightweightChild, boolean temporary,
         boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause);

    public Image createImage(ImageProducer producer) {
        return new ToolkitImage(producer);
    }

    public Image createImage(int width, int height) {
        Win32GraphicsConfig gc =
            (Win32GraphicsConfig)getGraphicsConfiguration();
        return gc.createAcceleratedImage((Component)target, width, height);
    }

    public VolatileImage createVolatileImage(int width, int height) {
        return new SunVolatileImage((Component)target, width, height);
    }

    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
        return getToolkit().prepareImage(img, w, h, o);
    }

    public int checkImage(Image img, int w, int h, ImageObserver o) {
        return getToolkit().checkImage(img, w, h, o);
    }

    // Object overrides

    public String toString() {
        return getClass().getName() + "[" + target + "]";
    }

    // Toolkit & peer internals

    private int updateX1, updateY1, updateX2, updateY2;

    WComponentPeer(Component target) {
        this.target = target;
        this.paintArea = new RepaintArea();
        Container parent = WToolkit.getNativeContainer(target);
        WComponentPeer parentPeer = (WComponentPeer) WToolkit.targetToPeer(parent);
        create(parentPeer);
        // fix for 5088782: check if window object is created successfully
        checkCreation();
        this.winGraphicsConfig =
            (Win32GraphicsConfig)getGraphicsConfiguration();
        this.surfaceData =
            winGraphicsConfig.createSurfaceData(this, numBackBuffers);
        initialize();
        start();  // Initialize enable/disable state, turn on callbacks
    }
    abstract void create(WComponentPeer parent);

    protected void checkCreation()
    {
        if ((hwnd == 0) || (pData == 0))
        {
            if (createError != null)
            {
                throw createError;
            }
            else
            {
                throw new InternalError("couldn't create component peer");
            }
        }
    }

    synchronized native void start();

    void initialize() {
        if (((Component)target).isVisible()) {
            show();  // the wnd starts hidden
        }
        Color fg = ((Component)target).getForeground();
        if (fg != null) {
            setForeground(fg);
        }
        // Set background color in C++, to avoid inheriting a parent's color.
        Font  f = ((Component)target).getFont();
        if (f != null) {
            setFont(f);
        }
        if (! ((Component)target).isEnabled()) {
            disable();
        }
        Rectangle r = ((Component)target).getBounds();
        setBounds(r.x, r.y, r.width, r.height, SET_BOUNDS);
    }

    // Callbacks for window-system events to the frame

    // Invoke a update() method call on the target
    void handleRepaint(int x, int y, int w, int h) {
        // Repaints are posted from updateClient now...
    }

    // Invoke a paint() method call on the target, after clearing the
    // damaged area.
    void handleExpose(int x, int y, int w, int h) {
        // Bug ID 4081126 & 4129709 - can't do the clearRect() here,
        // since it interferes with the java thread working in the
        // same window on multi-processor NT machines.

        postPaintIfNecessary(x, y, w, h);
    }

    /* Invoke a paint() method call on the target, without clearing the
     * damaged area.  This is normally called by a native control after
     * it has painted itself.
     *
     * NOTE: This is called on the privileged toolkit thread. Do not
     *       call directly into user code using this thread!
     */
    void handlePaint(int x, int y, int w, int h) {
        postPaintIfNecessary(x, y, w, h);
    }

    private void postPaintIfNecessary(int x, int y, int w, int h) {
        if ( !ComponentAccessor.getIgnoreRepaint( (Component) target) ) {
            PaintEvent event = PaintEventDispatcher.getPaintEventDispatcher().
                createPaintEvent((Component)target, x, y, w, h);
            if (event != null) {
                postEvent(event);
            }
        }
    }

    /*
     * Post an event. Queue it for execution by the callback thread.
     */
    void postEvent(AWTEvent event) {
        WToolkit.postEvent(WToolkit.targetToAppContext(target), event);
    }

    // Routines to support deferred window positioning.
    public void beginLayout() {
        // Skip all painting till endLayout
        isLayouting = true;
    }

    public void endLayout() {
        if(!paintArea.isEmpty() && !paintPending &&
            !((Component)target).getIgnoreRepaint()) {
            // if not waiting for native painting repaint damaged area
            postEvent(new PaintEvent((Component)target, PaintEvent.PAINT,
                          new Rectangle()));
        }
        isLayouting = false;
    }

    public native void beginValidate();
    public native void endValidate();

    /**
     * DEPRECATED
     */
    public Dimension minimumSize() {
        return getMinimumSize();
    }

    /**
     * DEPRECATED
     */
    public Dimension preferredSize() {
        return getPreferredSize();
    }

    /**
     * register a DropTarget with this native peer
     */

    public synchronized void addDropTarget(DropTarget dt) {
        if (nDropTargets == 0) {
            nativeDropTargetContext = addNativeDropTarget();
        }
        nDropTargets++;
    }

    /**
     * unregister a DropTarget with this native peer
     */

    public synchronized void removeDropTarget(DropTarget dt) {
        nDropTargets--;
        if (nDropTargets == 0) {
            removeNativeDropTarget();
            nativeDropTargetContext = 0;
        }
    }

    /**
     * add the native peer's AwtDropTarget COM object
     * @return reference to AwtDropTarget object
     */

    native long addNativeDropTarget();

    /**
     * remove the native peer's AwtDropTarget COM object
     */

    native void removeNativeDropTarget();
    native boolean nativeHandlesWheelScrolling();

    public boolean handlesWheelScrolling() {
        // should this be cached?
        return nativeHandlesWheelScrolling();
    }

    // Returns true if we are inside begin/endLayout and
    // are waiting for native painting
    public boolean isPaintPending() {
        return paintPending && isLayouting;
    }

    /**
     * The following multibuffering-related methods delegate to our
     * associated GraphicsConfig (Win or WGL) to handle the appropriate
     * native windowing system specific actions.
     */

    public void createBuffers(int numBuffers, BufferCapabilities caps)
        throws AWTException
    {
        Win32GraphicsConfig gc =
            (Win32GraphicsConfig)getGraphicsConfiguration();
        gc.assertOperationSupported((Component)target, numBuffers, caps);

        // Re-create the primary surface with the new number of back buffers
        try {
            replaceSurfaceData(numBuffers - 1);
        } catch (InvalidPipeException e) {
            throw new AWTException(e.getMessage());
        }
    }

    public synchronized void destroyBuffers() {
        disposeBackBuffer();
        numBackBuffers = 0;
    }

    private synchronized void disposeBackBuffer() {
        if (backBuffer == null) {
            return;
        }
        backBuffer = null;
    }

    public synchronized void flip(BufferCapabilities.FlipContents flipAction) {
        if (backBuffer == null) {
            throw new IllegalStateException("Buffers have not been created");
        }
        Win32GraphicsConfig gc =
            (Win32GraphicsConfig)getGraphicsConfiguration();
        gc.flip(this, (Component)target, backBuffer, flipAction);
    }

    public synchronized Image getBackBuffer() {
        if (backBuffer == null) {
            throw new IllegalStateException("Buffers have not been created");
        }
        return backBuffer;
    }

    /* override and return false on components that DO NOT require
       a clearRect() before painting (i.e. native components) */
    public boolean shouldClearRectBeforePaint() {
        return true;
    }

    native void pSetParent(ComponentPeer newNativeParent);

    /**
     * @see java.awt.peer.ComponentPeer#reparent
     */
    public void reparent(ContainerPeer newNativeParent) {
        pSetParent(newNativeParent);
    }

    /**
     * @see java.awt.peer.ComponentPeer#isReparentSupported
     */
    public boolean isReparentSupported() {
        return true;
    }

    public void setBoundsOperation(int operation) {
    }


    native void setRectangularShape(int lox, int loy, int hix, int hiy,
                     sun.java2d.pipe.Region region);


    /**
     * Applies the shape to the native component window.
     * @since 1.7
     */
    public void applyShape(sun.java2d.pipe.Region shape) {
        if (shapeLog.isLoggable(Level.FINER)) {
            shapeLog.finer(
                    "*** INFO: Setting shape: PEER: " + this
                    + "; TARGET: " + target
                    + "; SHAPE: " + shape);
        }

        setRectangularShape(shape.getLoX(), shape.getLoY(), shape.getHiX(), shape.getHiY(),
                (shape.isRectangular() ? null : shape));
    }

}
