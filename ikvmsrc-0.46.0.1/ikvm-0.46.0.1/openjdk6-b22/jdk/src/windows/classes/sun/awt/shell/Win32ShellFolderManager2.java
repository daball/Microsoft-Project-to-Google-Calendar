/*
 * Copyright (c) 2003, 2006, Oracle and/or its affiliates. All rights reserved.
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

package sun.awt.shell;

import java.awt.Toolkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.util.*;
import sun.security.action.LoadLibraryAction;

import static sun.awt.shell.Win32ShellFolder2.*;
import sun.awt.OSInfo;

// NOTE: This class supersedes Win32ShellFolderManager, which was removed
//       from distribution after version 1.4.2.

/**
 * @author Michael Martak
 * @author Leif Samuelsson
 * @author Kenneth Russell
 * @since 1.4
 */

public class Win32ShellFolderManager2 extends ShellFolderManager {

    static {
        // Load library here
        AccessController.doPrivileged(new LoadLibraryAction("awt"));
    }

    public ShellFolder createShellFolder(File file) throws FileNotFoundException {
        return createShellFolder(getDesktop(), file);
    }

    static Win32ShellFolder2 createShellFolder(Win32ShellFolder2 parent, File file) throws FileNotFoundException {
        long pIDL;
        try {
            pIDL = parent.parseDisplayName(file.getCanonicalPath());
        } catch (IOException ex) {
            pIDL = 0;
        }
        if (pIDL == 0) {
            // Shouldn't happen but watch for it anyway
            throw new FileNotFoundException("File " + file.getAbsolutePath() + " not found");
        }
        Win32ShellFolder2 folder = createShellFolderFromRelativePIDL(parent, pIDL);
        Win32ShellFolder2.releasePIDL(pIDL);
        return folder;
    }

    static Win32ShellFolder2 createShellFolderFromRelativePIDL(Win32ShellFolder2 parent, long pIDL) {
        // Walk down this relative pIDL, creating new nodes for each of the entries
        while (pIDL != 0) {
            long curPIDL = Win32ShellFolder2.copyFirstPIDLEntry(pIDL);
            if (curPIDL != 0) {
                parent = new Win32ShellFolder2(parent, curPIDL);
                pIDL = Win32ShellFolder2.getNextPIDLEntry(pIDL);
            } else {
                // The list is empty if the parent is Desktop and pIDL is a shortcut to Desktop
                break;
            }
        }
        return parent;
    }

    // Special folders
    private static Win32ShellFolder2 desktop;
    private static Win32ShellFolder2 drives;
    private static Win32ShellFolder2 recent;
    private static Win32ShellFolder2 network;
    private static Win32ShellFolder2 personal;

    private static String osVersion = System.getProperty("os.version");
    private static final boolean useShell32Icons =
                        (osVersion != null && osVersion.compareTo("5.1") >= 0);

    static Win32ShellFolder2 getDesktop() {
        if (desktop == null) {
            try {
                desktop = new Win32ShellFolder2(DESKTOP);
            } catch (IOException e) {
                desktop = null;
            }
        }
        return desktop;
    }

    static Win32ShellFolder2 getDrives() {
        if (drives == null) {
            try {
                drives = new Win32ShellFolder2(DRIVES);
            } catch (IOException e) {
                drives = null;
            }
        }
        return drives;
    }

    static Win32ShellFolder2 getRecent() {
        if (recent == null) {
            try {
                String path = Win32ShellFolder2.getFileSystemPath(RECENT);
                if (path != null) {
                    recent = createShellFolder(getDesktop(), new File(path));
                }
            } catch (IOException e) {
                recent = null;
            }
        }
        return recent;
    }

    static Win32ShellFolder2 getNetwork() {
        if (network == null) {
            try {
                network = new Win32ShellFolder2(NETWORK);
            } catch (IOException e) {
                network = null;
            }
        }
        return network;
    }

    static Win32ShellFolder2 getPersonal() {
        if (personal == null) {
            try {
                String path = Win32ShellFolder2.getFileSystemPath(PERSONAL);
                if (path != null) {
                    Win32ShellFolder2 desktop = getDesktop();
                    personal = desktop.getChildByPath(path);
                    if (personal == null) {
                        personal = createShellFolder(getDesktop(), new File(path));
                    }
                    if (personal != null) {
                        personal.setIsPersonal();
                    }
                }
            } catch (IOException e) {
                personal = null;
            }
        }
        return personal;
    }


    private static File[] roots;

    /**
     * @param key a <code>String</code>
     *  "fileChooserDefaultFolder":
     *    Returns a <code>File</code> - the default shellfolder for a new filechooser
     *  "roots":
     *    Returns a <code>File[]</code> - containing the root(s) of the displayable hierarchy
     *  "fileChooserComboBoxFolders":
     *    Returns a <code>File[]</code> - an array of shellfolders representing the list to
     *    show by default in the file chooser's combobox
     *   "fileChooserShortcutPanelFolders":
     *    Returns a <code>File[]</code> - an array of shellfolders representing well-known
     *    folders, such as Desktop, Documents, History, Network, Home, etc.
     *    This is used in the shortcut panel of the filechooser on Windows 2000
     *    and Windows Me.
     *  "fileChooserIcon nn":
     *    Returns an <code>Image</code> - icon nn from resource 216 in shell32.dll,
     *      or if not found there from resource 124 in comctl32.dll (Windows only).
     *  "optionPaneIcon iconName":
     *    Returns an <code>Image</code> - icon from the system icon list
     *
     * @return An Object matching the key string.
     */
    public Object get(String key) {
        if (key.equals("fileChooserDefaultFolder")) {
            File file = getPersonal();
            if (file == null) {
                file = getDesktop();
            }
            return file;
        } else if (key.equals("roots")) {
            // Should be "History" and "Desktop" ?
            if (roots == null) {
                File desktop = getDesktop();
                if (desktop != null) {
                    roots = new File[] { desktop };
                } else {
                    roots = (File[])super.get(key);
                }
            }
            return roots;
        } else if (key.equals("fileChooserComboBoxFolders")) {
            Win32ShellFolder2 desktop = getDesktop();

            if (desktop != null) {
                ArrayList<File> folders = new ArrayList<File>();
                Win32ShellFolder2 drives = getDrives();

                Win32ShellFolder2 recentFolder = getRecent();
                if (recentFolder != null && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_2000) >= 0) {
                    folders.add(recentFolder);
                }

                folders.add(desktop);
                // Add all second level folders
                File[] secondLevelFolders = desktop.listFiles();
                Arrays.sort(secondLevelFolders);
                for (File secondLevelFolder : secondLevelFolders) {
                    Win32ShellFolder2 folder = (Win32ShellFolder2) secondLevelFolder;
                    if (!folder.isFileSystem() || folder.isDirectory()) {
                        folders.add(folder);
                        // Add third level for "My Computer"
                        if (folder.equals(drives)) {
                            File[] thirdLevelFolders = folder.listFiles();
                            if (thirdLevelFolders != null) {
                                Arrays.sort(thirdLevelFolders, driveComparator);
                                for (File thirdLevelFolder : thirdLevelFolders) {
                                    folders.add(thirdLevelFolder);
                                }
                            }
                        }
                    }
                }
                return folders.toArray(new File[folders.size()]);
            } else {
                return super.get(key);
            }
        } else if (key.equals("fileChooserShortcutPanelFolders")) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            ArrayList<File> folders = new ArrayList<File>();
            int i = 0;
            Object value;
            do {
                value = toolkit.getDesktopProperty("win.comdlg.placesBarPlace" + i++);
                try {
                    if (value instanceof Integer) {
                        // A CSIDL
                        folders.add(new Win32ShellFolder2((Integer)value));
                    } else if (value instanceof String) {
                        // A path
                        folders.add(createShellFolder(new File((String)value)));
                    }
                } catch (IOException e) {
                    // Skip this value
                }
            } while (value != null);

            if (folders.size() == 0) {
                // Use default list of places
                for (File f : new File[] {
                    getRecent(), getDesktop(), getPersonal(), getDrives(), getNetwork()
                }) {
                    if (f != null) {
                        folders.add(f);
                    }
                }
            }
            return folders.toArray(new File[folders.size()]);
        } else if (key.startsWith("fileChooserIcon ")) {
            int i = -1;
            String name = key.substring(key.indexOf(" ")+1);
            try {
                i = Integer.parseInt(name);
            } catch (NumberFormatException ex) {
                if (name.equals("ListView")) {
                    i = (useShell32Icons) ? 21 : 2;
                } else if (name.equals("DetailsView")) {
                    i = (useShell32Icons) ? 23 : 3;
                } else if (name.equals("UpFolder")) {
                    i = (useShell32Icons) ? 28 : 8;
                } else if (name.equals("NewFolder")) {
                    i = (useShell32Icons) ? 31 : 11;
                } else if (name.equals("ViewMenu")) {
                    i = (useShell32Icons) ? 21 : 2;
                }
            }
            if (i >= 0) {
                return Win32ShellFolder2.getFileChooserIcon(i);
            }
        } else if (key.startsWith("optionPaneIcon ")) {
            Win32ShellFolder2.SystemIcon iconType;
            if (key == "optionPaneIcon Error") {
                iconType = Win32ShellFolder2.SystemIcon.IDI_ERROR;
            } else if (key == "optionPaneIcon Information") {
                iconType = Win32ShellFolder2.SystemIcon.IDI_INFORMATION;
            } else if (key == "optionPaneIcon Question") {
                iconType = Win32ShellFolder2.SystemIcon.IDI_QUESTION;
            } else if (key == "optionPaneIcon Warning") {
                iconType = Win32ShellFolder2.SystemIcon.IDI_EXCLAMATION;
            } else {
                return null;
            }
            return Win32ShellFolder2.getSystemIcon(iconType);
        } else if (key.startsWith("shell32Icon ")) {
            int i;
            String name = key.substring(key.indexOf(" ")+1);
            try {
                i = Integer.parseInt(name);
                if (i >= 0) {
                    return Win32ShellFolder2.getShell32Icon(i);
                }
            } catch (NumberFormatException ex) {
            }
        }
        return null;
    }

    /**
     * Does <code>dir</code> represent a "computer" such as a node on the network, or
     * "My Computer" on the desktop.
     */
    public boolean isComputerNode(File dir) {
        if (dir != null && dir == getDrives()) {
            return true;
        } else {
            String path = dir.getAbsolutePath();
            return (path.startsWith("\\\\") && path.indexOf("\\", 2) < 0);      //Network path
        }
    }

    public boolean isFileSystemRoot(File dir) {
        //Note: Removable drives don't "exist" but are listed in "My Computer"
        if (dir != null) {
            Win32ShellFolder2 drives = getDrives();
            if (dir instanceof Win32ShellFolder2) {
                Win32ShellFolder2 sf = (Win32ShellFolder2)dir;
                if (sf.isFileSystem()) {
                    if (sf.parent != null) {
                        return sf.parent.equals(drives);
                    }
                    // else fall through ...
                } else {
                    return false;
                }
            }
            String path = dir.getPath();
            return (path.length() == 3
                    && path.charAt(1) == ':'
                    && Arrays.asList(drives.listFiles()).contains(dir));
        }
        return false;
    }

    private Comparator driveComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            Win32ShellFolder2 shellFolder1 = (Win32ShellFolder2) o1;
            Win32ShellFolder2 shellFolder2 = (Win32ShellFolder2) o2;

            // Put drives at first
            boolean isDrive1 = shellFolder1.getPath().endsWith(":\\");

            if (isDrive1 ^ shellFolder2.getPath().endsWith(":\\")) {
                return isDrive1 ? -1 : 1;
            } else {
                return shellFolder1.getPath().compareTo(shellFolder2.getPath());
            }
        }
    };


    public void sortFiles(List files) {
        Collections.sort(files, fileComparator);
    }

    private static List topFolderList = null;
    static int compareShellFolders(Win32ShellFolder2 sf1, Win32ShellFolder2 sf2) {
        boolean special1 = sf1.isSpecial();
        boolean special2 = sf2.isSpecial();

        if (special1 || special2) {
            if (topFolderList == null) {
                ArrayList tmpTopFolderList = new ArrayList();
                tmpTopFolderList.add(Win32ShellFolderManager2.getPersonal());
                tmpTopFolderList.add(Win32ShellFolderManager2.getDesktop());
                tmpTopFolderList.add(Win32ShellFolderManager2.getDrives());
                tmpTopFolderList.add(Win32ShellFolderManager2.getNetwork());
                topFolderList = tmpTopFolderList;
            }
            int i1 = topFolderList.indexOf(sf1);
            int i2 = topFolderList.indexOf(sf2);
            if (i1 >= 0 && i2 >= 0) {
                return (i1 - i2);
            } else if (i1 >= 0) {
                return -1;
            } else if (i2 >= 0) {
                return 1;
            }
        }

        // Non-file shellfolders sort before files
        if (special1 && !special2) {
            return -1;
        } else if (special2 && !special1) {
            return  1;
        }

        return compareNames(sf1.getAbsolutePath(), sf2.getAbsolutePath());
    }

    static int compareFiles(File f1, File f2) {
        if (f1 instanceof Win32ShellFolder2) {
            return f1.compareTo(f2);
        }
        if (f2 instanceof Win32ShellFolder2) {
            return -1 * f2.compareTo(f1);
        }
        return compareNames(f1.getName(), f2.getName());
    }

    static int compareNames(String name1, String name2) {
        // First ignore case when comparing
        int diff = name1.toLowerCase().compareTo(name2.toLowerCase());
        if (diff != 0) {
            return diff;
        } else {
            // May differ in case (e.g. "mail" vs. "Mail")
            // We need this test for consistent sorting
            return name1.compareTo(name2);
        }
    }

    private Comparator fileComparator = new Comparator() {
        public int compare(Object a, Object b) {
            return compare((File)a, (File)b);
        }

        public int compare(File f1, File f2) {
            return compareFiles(f1, f2);
        }
    };
}
