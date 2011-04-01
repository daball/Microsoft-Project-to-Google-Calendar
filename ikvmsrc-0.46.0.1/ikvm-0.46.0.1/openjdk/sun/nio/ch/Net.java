/*
 * Copyright (c) 2000, 2005, Oracle and/or its affiliates. All rights reserved.
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

package sun.nio.ch;

import cli.System.Net.IPAddress;
import cli.System.Net.IPEndPoint;
import cli.System.Net.Sockets.LingerOption;
import cli.System.Net.Sockets.SelectMode;
import cli.System.Net.Sockets.SocketOptionName;
import cli.System.Net.Sockets.SocketOptionLevel;
import cli.System.Net.Sockets.SocketFlags;
import cli.System.Net.Sockets.SocketType;
import cli.System.Net.Sockets.ProtocolType;
import cli.System.Net.Sockets.AddressFamily;
import cli.System.Net.Sockets.SocketShutdown;
import ikvm.lang.CIL;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;


class Net {                                             // package-private

    private Net() { }

    static FileDescriptor serverSocket(boolean stream) throws IOException
    {
        return socket(stream);
    }

    static FileDescriptor socket(boolean stream) throws IOException
    {
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            FileDescriptor fd = new FileDescriptor();
            if (stream)
            {
                fd.setSocket(new cli.System.Net.Sockets.Socket(AddressFamily.wrap(AddressFamily.InterNetwork), SocketType.wrap(SocketType.Stream), ProtocolType.wrap(ProtocolType.Tcp)));
            }
            else
            {
                fd.setSocket(new cli.System.Net.Sockets.Socket(AddressFamily.wrap(AddressFamily.InterNetwork), SocketType.wrap(SocketType.Dgram), ProtocolType.wrap(ProtocolType.Udp)));
            }
            return fd;
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
            throw SocketUtil.convertSocketExceptionToIOException(x);
        }
    }

    static void bind(FileDescriptor fd, InetAddress addr, int port) throws IOException
    {
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            if (false) throw new cli.System.ObjectDisposedException("");
            fd.getSocket().Bind(new IPEndPoint(SocketUtil.getAddressFromInetAddress(addr), port));
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
            throw SocketUtil.convertSocketExceptionToIOException(x);
        }
        catch (cli.System.ObjectDisposedException x1)
        {
            throw new SocketException("Socket is closed");
        }
    }

    static void configureBlocking(FileDescriptor fd, boolean blocking) throws IOException
    {
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            if (false) throw new cli.System.ObjectDisposedException("");
            fd.getSocket().set_Blocking(blocking);
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
            if (x.get_ErrorCode() == SocketUtil.WSAEINVAL)
            {
                // Work around for winsock issue. You can't set a socket to blocking if a connection request is pending,
                // so we'll have to set the blocking again in SocketChannelImpl.checkConnect().
                return;
            }
            throw SocketUtil.convertSocketExceptionToIOException(x);
        }
        catch (cli.System.ObjectDisposedException _)
        {
            throw new SocketException("Socket is closed");
        }
    }

    static InetSocketAddress localAddress(FileDescriptor fd)
    {
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            if (false) throw new cli.System.ObjectDisposedException("");
            IPEndPoint ep = (IPEndPoint)fd.getSocket().get_LocalEndPoint();
            return new InetSocketAddress(SocketUtil.getInetAddressFromIPEndPoint(ep), ep.get_Port());
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
            throw new Error(x);
        }
        catch (cli.System.ObjectDisposedException x)
        {
            throw new Error(x);
        }
    }

    static int localPortNumber(FileDescriptor fd)
    {
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            if (false) throw new cli.System.ObjectDisposedException("");
            IPEndPoint ep = (IPEndPoint)fd.getSocket().get_LocalEndPoint();
            return ep == null ? 0 : ep.get_Port();
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
            throw new Error(x);
        }
        catch (cli.System.ObjectDisposedException x)
        {
            throw new Error(x);
        }
    }

    private static int mapSocketOptionLevel(int opt) throws IOException
    {
        switch (opt)
        {
            case SocketOptions.SO_BROADCAST:
            case SocketOptions.SO_KEEPALIVE:
            case SocketOptions.SO_LINGER:
            case SocketOptions.SO_OOBINLINE:
            case SocketOptions.SO_RCVBUF:
            case SocketOptions.SO_SNDBUF:
            case SocketOptions.SO_REUSEADDR:
                return SocketOptionLevel.Socket;
            case SocketOptions.IP_MULTICAST_LOOP:
            case SocketOptions.IP_TOS:
                return SocketOptionLevel.IP;
            case SocketOptions.TCP_NODELAY:
                return SocketOptionLevel.Tcp;
            default:
                throw new SocketException("unsupported socket option: " + opt);
        }
    }

    private static int mapSocketOptionName(int opt) throws IOException
    {
        switch (opt)
        {
            case SocketOptions.SO_BROADCAST:
                return SocketOptionName.Broadcast;
            case SocketOptions.SO_KEEPALIVE:
                return SocketOptionName.KeepAlive;
            case SocketOptions.SO_LINGER:
                return SocketOptionName.Linger;
            case SocketOptions.SO_OOBINLINE:
                return SocketOptionName.OutOfBandInline;
            case SocketOptions.SO_RCVBUF:
                return SocketOptionName.ReceiveBuffer;
            case SocketOptions.SO_SNDBUF:
                return SocketOptionName.SendBuffer;
            case SocketOptions.SO_REUSEADDR:
                return SocketOptionName.ReuseAddress;
            case SocketOptions.IP_MULTICAST_LOOP:
                return SocketOptionName.MulticastLoopback;
            case SocketOptions.IP_TOS:
                return SocketOptionName.TypeOfService;
            case SocketOptions.TCP_NODELAY:
                return SocketOptionName.NoDelay;
            default:
                throw new SocketException("unsupported socket option: " + opt);
        }
    }

    static void setIntOption(FileDescriptor fd, int opt, int arg) throws IOException
    {
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            if (false) throw new cli.System.ObjectDisposedException("");
            int level = mapSocketOptionLevel(opt);
            int name = mapSocketOptionName(opt);
            fd.getSocket().SetSocketOption(SocketOptionLevel.wrap(level), SocketOptionName.wrap(name), arg);
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
            throw SocketUtil.convertSocketExceptionToIOException(x);
        }
        catch (cli.System.ObjectDisposedException x1)
        {
            throw new SocketException("Socket is closed");
        }
    }

    static int getIntOption(FileDescriptor fd, int opt) throws IOException
    {
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            if (false) throw new cli.System.ObjectDisposedException("");
            int level = mapSocketOptionLevel(opt);
            int name = mapSocketOptionName(opt);
            Object obj = fd.getSocket().GetSocketOption(SocketOptionLevel.wrap(level), SocketOptionName.wrap(name));
            if (obj instanceof LingerOption)
            {
                LingerOption lo = (LingerOption)obj;
                return lo.get_Enabled() ? lo.get_LingerTime() : -1;
            }
            return CIL.unbox_int(obj);
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
            throw SocketUtil.convertSocketExceptionToIOException(x);
        }
        catch (cli.System.ObjectDisposedException x1)
        {
            throw new SocketException("Socket is closed");
        }
    }

    private static int readImpl(FileDescriptor fd, byte[] buf, int offset, int length) throws IOException
    {
        if (length == 0)
        {
            return 0;
        }
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            if (false) throw new cli.System.ObjectDisposedException("");
            int read = fd.getSocket().Receive(buf, offset, length, SocketFlags.wrap(SocketFlags.None));
            return read == 0 ? IOStatus.EOF : read;
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
            if (x.get_ErrorCode() == SocketUtil.WSAESHUTDOWN)
            {
                // the socket was shutdown, so we have to return EOF
                return IOStatus.EOF;
            }
            else if (x.get_ErrorCode() == SocketUtil.WSAEWOULDBLOCK)
            {
                // nothing to read and would block
                return IOStatus.UNAVAILABLE;
            }
            throw SocketUtil.convertSocketExceptionToIOException(x);
        }
        catch (cli.System.ObjectDisposedException x1)
        {
            throw new SocketException("Socket is closed");
        }
    }

    static int read(FileDescriptor fd, ByteBuffer dst) throws IOException
    {
        if (dst.hasArray())
        {
            byte[] buf = dst.array();
            int len = readImpl(fd, buf, dst.arrayOffset() + dst.position(), dst.remaining());
            if (len > 0)
            {
                dst.position(dst.position() + len);
            }
            return len;
        }
        else
        {
            byte[] buf = new byte[dst.remaining()];
            int len = readImpl(fd, buf, 0, buf.length);
            if (len > 0)
            {
                dst.put(buf, 0, len);
            }
            return len;
        }
    }

    static long read(FileDescriptor fd, ByteBuffer[] dsts) throws IOException
    {
        long totalRead = 0;
        for (int i = 0; i < dsts.length; i++)
        {
            int size = dsts[i].remaining();
            if (size > 0)
            {
                int read = read(fd, dsts[i]);
                if (read < 0)
                {
                    break;
                }
                totalRead += read;
                if (read < size || safeGetAvailable(fd) == 0)
                {
                    break;
                }
            }
        }
        return totalRead;
    }

    private static int safeGetAvailable(FileDescriptor fd)
    {
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            if (false) throw new cli.System.ObjectDisposedException("");
            return fd.getSocket().get_Available();
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
        }
        catch (cli.System.ObjectDisposedException x1)
        {
        }
        return 0;
    }

    private static int writeImpl(FileDescriptor fd, byte[] buf, int offset, int length) throws IOException
    {
        try
        {
            if (false) throw new cli.System.Net.Sockets.SocketException();
            if (false) throw new cli.System.ObjectDisposedException("");
            return fd.getSocket().Send(buf, offset, length, SocketFlags.wrap(SocketFlags.None));
        }
        catch (cli.System.Net.Sockets.SocketException x)
        {
            if (x.get_ErrorCode() == SocketUtil.WSAEWOULDBLOCK)
            {
                return IOStatus.UNAVAILABLE;
            }
            throw SocketUtil.convertSocketExceptionToIOException(x);
        }
        catch (cli.System.ObjectDisposedException x1)
        {
            throw new SocketException("Socket is closed");
        }
    }

    static int write(FileDescriptor fd, ByteBuffer src) throws IOException
    {
        if (src.hasArray())
        {
            byte[] buf = src.array();
            int len = writeImpl(fd, buf, src.arrayOffset() + src.position(), src.remaining());
            if (len > 0)
            {
                src.position(src.position() + len);
            }
            return len;
        }
        else
        {
            int pos = src.position();
            byte[] buf = new byte[src.remaining()];
            src.get(buf);
            int len = writeImpl(fd, buf, 0, buf.length);
            if (len > 0)
            {
                src.position(pos + len);
            }
            return len;
        }
    }

    static long write(FileDescriptor fd, ByteBuffer[] srcs) throws IOException
    {
        long totalWritten = 0;
        for (int i = 0; i < srcs.length; i++)
        {
            int size = srcs[i].remaining();
            if (size > 0)
            {
                int written = write(fd, srcs[i]);
                if (written < 0)
                {
                    break;
                }
                totalWritten += written;
                if (written < size)
                {
                    break;
                }
            }
        }
        return totalWritten;
    }

    // -- Miscellaneous utilities --

    static InetSocketAddress checkAddress(SocketAddress sa) {
        if (sa == null)
            throw new IllegalArgumentException();
        if (!(sa instanceof InetSocketAddress))
            throw new UnsupportedAddressTypeException(); // ## needs arg
        InetSocketAddress isa = (InetSocketAddress)sa;
        if (isa.isUnresolved())
            throw new UnresolvedAddressException(); // ## needs arg
        InetAddress addr = isa.getAddress();
        if (!(addr instanceof Inet4Address || addr instanceof Inet6Address))
            throw new IllegalArgumentException("Invalid address type");
        return isa;
    }

    static InetSocketAddress asInetSocketAddress(SocketAddress sa) {
        if (!(sa instanceof InetSocketAddress))
            throw new UnsupportedAddressTypeException();
        return (InetSocketAddress)sa;
    }

    static void translateToSocketException(Exception x)
        throws SocketException
    {
        if (x instanceof SocketException)
            throw (SocketException)x;
        Exception nx = x;
        if (x instanceof ClosedChannelException)
            nx = new SocketException("Socket is closed");
        else if (x instanceof AlreadyBoundException)
            nx = new SocketException("Already bound");
        else if (x instanceof NotYetBoundException)
            nx = new SocketException("Socket is not bound yet");
        else if (x instanceof UnsupportedAddressTypeException)
            nx = new SocketException("Unsupported address type");
        else if (x instanceof UnresolvedAddressException) {
            nx = new SocketException("Unresolved address");
        }
        if (nx != x)
            nx.initCause(x);

        if (nx instanceof SocketException)
            throw (SocketException)nx;
        else if (nx instanceof RuntimeException)
            throw (RuntimeException)nx;
        else
            throw new Error("Untranslated exception", nx);
    }

    static void translateException(Exception x,
                                   boolean unknownHostForUnresolved)
        throws IOException
    {
        if (x instanceof IOException)
            throw (IOException)x;
        // Throw UnknownHostException from here since it cannot
        // be thrown as a SocketException
        if (unknownHostForUnresolved &&
            (x instanceof UnresolvedAddressException))
        {
             throw new UnknownHostException();
        }
        translateToSocketException(x);
    }

    static void translateException(Exception x)
        throws IOException
    {
        translateException(x, false);
    }
}
