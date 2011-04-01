/*
 * Copyright (c) 1999, 2007, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.peer.DialogPeer;
import java.awt.dnd.DropTarget;
import java.util.Vector;
import sun.awt.AppContext;
import sun.awt.ComponentAccessor;

public class WPrintDialogPeer extends WWindowPeer implements DialogPeer {

    static {
        initIDs();
    }

    private WComponentPeer parent;

    private Vector<WWindowPeer> blockedWindows = new Vector<WWindowPeer>();

    WPrintDialogPeer(WPrintDialog target) {
        super(target);
    }

    void create(WComponentPeer parent) {
        this.parent = parent;
    }

    // fix for CR 6178323:
    // don't use checkCreation() from WComponentPeer to avoid hwnd check
    protected void checkCreation() {
    }

    protected void disposeImpl() {
        WToolkit.targetDisposedPeer(target, this);
    }

    private native boolean _show();

    public void show() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    ((WPrintDialog)target).setRetVal(_show());
                } catch (Exception e) {
                    // No exception should be thrown by native dialog code,
                    // but if it is we need to trap it so the thread does
                    // not hide is called and the thread doesn't hang.
                }
                ((WPrintDialog)target).hide();
            }
        }).start();
    }

    synchronized void setHWnd(long hwnd) {
        this.hwnd = hwnd;
        for (WWindowPeer window : blockedWindows) {
            if (hwnd != 0) {
                window.modalDisable((Dialog)target, hwnd);
            } else {
                window.modalEnable((Dialog)target);
            }
        }
    }

    synchronized void blockWindow(WWindowPeer window) {
        blockedWindows.add(window);
        if (hwnd != 0) {
            window.modalDisable((Dialog)target, hwnd);
        }
    }
    synchronized void unblockWindow(WWindowPeer window) {
        blockedWindows.remove(window);
        if (hwnd != 0) {
            window.modalEnable((Dialog)target);
        }
    }

    public void blockWindows(java.util.List<Window> toBlock) {
        for (Window w : toBlock) {
            WWindowPeer wp = (WWindowPeer)ComponentAccessor.getPeer(w);
            if (wp != null) {
                blockWindow(wp);
            }
        }
    }

    public native void toFront();
    public native void toBack();

    // unused methods.  Overridden to disable this functionality as
    // it requires HWND which is not available for FileDialog
    void initialize() {}
    public void setAlwaysOnTop(boolean b) {}
    public void setResizable(boolean resizable) {}
    public void hide() {}
    public void enable() {}
    public void disable() {}
    public void reshape(int x, int y, int width, int height) {}
    public boolean handleEvent(Event e) { return false; }
    public void setForeground(Color c) {}
    public void setBackground(Color c) {}
    public void setFont(Font f) {}
    public void updateMinimumSize() {}
    public void updateIconImages() {}
    public boolean requestFocus(boolean temporary, boolean focusedWindowChangeAllowed) {
        return false;
    }
    public void updateFocusableWindowState() {}
    void start() {}
    public void beginValidate() {}
    public void endValidate() {}
    void invalidate(int x, int y, int width, int height) {}
    public void addDropTarget(DropTarget dt) {}
    public void removeDropTarget(DropTarget dt) {}

    /**
     * Initialize JNI field and method ids
     */
    private static native void initIDs();

    /**
     * WPrintDialogPeer doesn't have native pData so we don't do restack on it
     * @see java.awt.peer.ContainerPeer#restack
     */
    public void restack() {
    }

    /**
     * @see java.awt.peer.ContainerPeer#isRestackSupported
     */
    public boolean isRestackSupported() {
        return false;
    }
}
