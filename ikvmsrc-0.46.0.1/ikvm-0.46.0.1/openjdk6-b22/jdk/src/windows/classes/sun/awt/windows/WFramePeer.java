/*
 * Copyright (c) 1996, 2006, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Vector;
import java.awt.*;
import java.awt.peer.*;
import java.awt.image.ImageObserver;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ToolkitImage;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.BufferedImage;
import sun.awt.im.*;
import sun.awt.Win32GraphicsDevice;
import java.awt.image.ColorModel;


class WFramePeer extends WWindowPeer implements FramePeer {

    // FramePeer implementation
    public native void setState(int state);
    public native int getState();

    // Convenience methods to save us from trouble of extracting
    // Rectangle fields in native code.
    private native void setMaximizedBounds(int x, int y, int w, int h);
    private native void clearMaximizedBounds();

    private static final boolean keepOnMinimize = "true".equals(
        (String)java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction(
                "sun.awt.keepWorkingSetOnMinimize")));

    public void setMaximizedBounds(Rectangle b) {
        if (b == null) {
            clearMaximizedBounds();
        } else {
            setMaximizedBounds(b.x, b.y, b.width, b.height);
        }
    }

    @Override
    boolean isTargetUndecorated() {
        return ((Frame)target).isUndecorated();
    }

    public void reshape(int x, int y, int width, int height) {
        Rectangle newBounds = constrainBounds(x, y, width, height);

        if (((Frame)target).isUndecorated()) {
            super.reshape(newBounds.x, newBounds.y, newBounds.width, newBounds.height);
        } else {
            reshapeFrame(newBounds.x, newBounds.y, newBounds.width, newBounds.height);
        }
    }

    public Dimension getMinimumSize() {
        Dimension d = new Dimension();
        if (!((Frame)target).isUndecorated()) {
            d.setSize(getSysMinWidth(), getSysMinHeight());
        }
        if (((Frame)target).getMenuBar() != null) {
            d.height += getSysMenuHeight();
        }
        return d;
    }

    // Note: Because this method calls resize(), which may be overridden
    // by client code, this method must not be executed on the toolkit
    // thread.
    public void setMenuBar(MenuBar mb) {
        WMenuBarPeer mbPeer = (WMenuBarPeer) WToolkit.targetToPeer(mb);
        setMenuBar0(mbPeer);
        updateInsets(insets_);
    }

    // Note: Because this method calls resize(), which may be overridden
    // by client code, this method must not be executed on the toolkit
    // thread.
    private native void setMenuBar0(WMenuBarPeer mbPeer);

    // Toolkit & peer internals

    WFramePeer(Frame target) {
        super(target);

        InputMethodManager imm = InputMethodManager.getInstance();
        String menuString = imm.getTriggerMenuString();
        if (menuString != null)
        {
          pSetIMMOption(menuString);
        }
    }

    native void createAwtFrame(WComponentPeer parent);
    void create(WComponentPeer parent) {
        createAwtFrame(parent);
    }

    void initialize() {
        super.initialize();

        Frame target = (Frame)this.target;

        if (target.getTitle() != null) {
            setTitle(target.getTitle());
        }
        setResizable(target.isResizable());
        setState(target.getExtendedState());
    }

    private native static int getSysMenuHeight();

    native void pSetIMMOption(String option);
    void notifyIMMOptionChange(){
      InputMethodManager.getInstance().notifyChangeRequest((Component)target);
    }

    public void setBoundsPrivate(int x, int y, int width, int height) {
        setBounds(x, y, width, height, SET_BOUNDS);
    }
    public Rectangle getBoundsPrivate() {
        return getBounds();
    }
}
