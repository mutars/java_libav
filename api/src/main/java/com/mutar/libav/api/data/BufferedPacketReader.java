/*
 * Copyright (C) 2012 Ondrej Perutka
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.mutar.libav.api.data;

import java.util.concurrent.locks.ReentrantLock;

import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.api.util.AVFormatLibraryUtil;
import com.mutar.libav.api.util.Buffer;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avformat.AVFormatContext;

/**
 * Buffered packet reader.
 *
 * @author Ondrej Perutka
 */
public class BufferedPacketReader {

    private final AVFormatContext formatContext;
    private final PacketPool packetPool;
    private AVPacket packet;

    private final Buffer<AVPacket> buffer;
    private boolean eof;

    private ReaderThread readerThread;
    private Thread t;
    private final ReentrantLock lock;

    /**
     * Create a new pcket reader.
     *
     * @param formatContext a format context
     * @param bufferSize size of the buffer
     */
    public BufferedPacketReader(AVFormatContext formatContext, int bufferSize) {
        this.formatContext = formatContext;
        packetPool = new PacketPool();
        packet = packetPool.getEmptyPacket();

        buffer = new Buffer<AVPacket>(bufferSize);
        eof = false;

        readerThread = null;
        t = null;
        lock = new ReentrantLock();
    }

    private void start() {
        if (t != null)
            return;

        readerThread = new ReaderThread();
        t = new Thread(readerThread);
        t.setDaemon(true);
        t.start();
    }

    private void stop() throws InterruptedException {
        if (t == null)
            return;

        readerThread.stop();
        t.interrupt();
        t.join();

        t = null;
    }

    /**
     * Drop buffered data.
     */
    public void dropBuffer() {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        try {
            stop();
        } catch (InterruptedException ex) {
            lock.unlock();
            throw new RuntimeException(ex);
        }

        AVPacket pw;
        synchronized (buffer) {
            while (buffer.getItemCount() > 0) {
                pw = buffer.get();
                if (pw != null)
                    AVCodecLibraryUtil.free(pw);
            }
        }

        lock.unlock();
    }

    /**
     * Reset the EOF flag. (It is usefull when seeking.)
     */
    public void resetEof() {
        eof = false;
    }

    /**
     * Release all associated resources.
     */
    public void close() {
        dropBuffer();

        try {
            lock.lockInterruptibly();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        packet = null;
        packetPool.dispose();

        lock.unlock();
    }

    /**
     * Check whether the packet reader is closed or not.
     *
     * @return true if it is close, false otherwise
     */
    public boolean isClosed() {
        return packet == null;
    }

    /**
     * Get next packet.
     *
     * @return packet wrapper or null in case of EOF
     */
    public AVPacket nextPacket() {
        AVPacket pw;
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        synchronized (buffer) {
            if ((eof && buffer.getItemCount() == 0) || isClosed()) {
                lock.unlock();
                return null;
            }
            if (t == null)
                start();

            try {
                pw = buffer.waitGet();
            } catch (InterruptedException ex) {
                lock.unlock();
                throw new RuntimeException(ex);
            }
        }

        lock.unlock();
        return pw;
    }

    private boolean putPacket(AVPacket pw) {
        if (pw == null)
            eof = true;

        try {
            buffer.waitPut(pw);
        } catch (InterruptedException ex) {
            return false;
        }

        return true;
    }

    private class ReaderThread implements Runnable {
        private boolean stop;

        public ReaderThread() {
            stop = false;
        }

        public void stop() {
            stop = true;
        }

        @Override
        public void run() {
            AVPacket pw;
            boolean put;

            while (!stop) {
                if (AVFormatLibraryUtil.readNextPacket(formatContext,packet))
                    pw = packetPool.clonePacket(packet);
                else
                    pw = null;
                AVCodecLibraryUtil.free(packet);

                do {
                    //System.out.printf("got next packet: pts = %d, dts = %d, stream_index = %d, pos = %d\n", pw.getPts(), pw.getDts(), pw.getStreamIndex(), pw.getPosition());
                    put = putPacket(pw);
                } while (!put && !stop);

                if (pw == null)
                    stop = true;
                else if (!put)
                    AVCodecLibraryUtil.free(pw);
            }
        }
    }

}
