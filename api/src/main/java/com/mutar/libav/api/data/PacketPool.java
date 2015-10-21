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

import java.util.ArrayDeque;
import java.util.Deque;

import org.bridj.Pointer;

import com.mutar.libav.api.bridge.AbstractWrapper;
import com.mutar.libav.api.exception.LibavRuntimeException;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.service.LibraryManager;

/**
 * Packet pool. Allows to reuse old packets to avoid pointless memory
 * allocations.
 *
 * @author Ondrej Perutka
 */
public class PacketPool {

    //private final PacketWrapperFactory packetFactory;
    private final Deque<PooledPacket> recycle;

    /**
     * Create a new packet pool instance.
     */
    public PacketPool() {
        recycle = new ArrayDeque<PooledPacket>();
    }

    /**
     * Release all resources held by this pool.
     */
    public synchronized void dispose() {
        while (!recycle.isEmpty())
            recycle.poll().dispose();
    }

    /**
     * Get an empty packet (packet with a null data pointer).
     *
     * @return packet wrapper
     */
    public AVPacket getEmptyPacket() {
        PooledPacket result = getPacket();
        result.init();

        return result.getDelegate();
    }

    /**
     * Clone existing packet.
     *
     * @param packet a packet to be clonned
     * @return packet clone
     */
    public AVPacket clonePacket(AVPacket packet) {
        PooledPacket result = getPacket();
        result.clone(packet);

        return result.getDelegate();
    }

    private PooledPacket getPacket() {
        PooledPacket result;

        synchronized (this) {
            result = recycle.poll();
        }

        if (result == null)
            return new PooledPacket(new AVPacket()).alloc();

        return result;
    }

    private synchronized void recycle(PooledPacket packet) {
        recycle.add(packet);
    }

    private class PooledPacket extends AbstractWrapper<AVPacket> {

        private AvcodecLibrary avcodecLibrary= LibraryManager.getInstance().getAvCodec();
        private int bufferSize;

        public PooledPacket(AVPacket internal) {
            super(internal);
            this.bufferSize = internal.size();
        }

        @Override
        protected void finalize() throws Throwable {
            dispose();
            super.finalize();
        }

        public synchronized void dispose() {
            if (delegate == null)
                return;
            avcodecLibrary.av_free_packet(getPointer());
            delegate = null;
        }


        public void init() {
            avcodecLibrary.av_init_packet(getPointer());
        }

        public void free() {
            setSize(bufferSize);
            recycle(this);
        }

        public void grow(int growBy) {
            int result = avcodecLibrary.av_grow_packet(getPointer(), growBy);
            if (result != 0)
                throw new LibavRuntimeException(result);
            bufferSize = delegate.size();
        }

        public void shrink(int size) {
            avcodecLibrary.av_shrink_packet(getPointer(), size);
        }

 /*       public int getStreamIndex() {
            return delegate.stream_index();
        }

        public void setStreamIndex(int streamIndex) {
            delegate.stream_index(streamIndex);
        } */

        public int getSize() {
            return delegate.size();
        }

        public void setSize(int size) {
            delegate.size(size);
        }

        public Pointer<Byte> getData() {
            return delegate.data();
        }

        public void setData(Pointer<Byte> data) {
            delegate.data(data);
        }

        public PooledPacket alloc() {
            avcodecLibrary.av_init_packet(getPointer());
            //clearWrapperCache();
            return this;
        }
        /*
        public int getFlags() {
            return delegate.flags();
        }


        public void setFlags(int flags) {
            delegate.flags(flags);
        }

        public long getPts() {
            return delegate.pts();
        }

        public void setPts(long pts) {
            delegate.pts(pts);
        }

        public long getDts() {
            return delegate.dts();
        }

        public void setDts(long dts) {
            delegate.dts(dts);
        }


        public long getDuration() {
            return delegate.duration();
        }

        public void setDuration(long duration) {
            delegate.duration(duration);
        }


        public long getConvergenceDuration() {
            return delegate.convergence_duration();
        }


        public void setConvergenceDuration(long convergenceDuration) {
            delegate.convergence_duration(convergenceDuration);
        }


        public long getPosition() {
            return delegate.pos();
        }


        public void setPosition(long position) {
            delegate.pos(position);
        }


        public Pointer<AVPacketSideData> getSideData() {
            return delegate.side_data();
        }


        public void setSideData(Pointer<?> sideData) {
            delegate.side_data(sideData);
        }


        public int getSideDataElems() {
            return delegate.SideDataElems();
        }


        public void setSideDataElems(int sideDataElems) {
            delegate.setSideDataElems(sideDataElems);
        } */


        public AVPacket clone() {
            return clonePacket(delegate);
        }


        public void clone(AVPacket packet) {
            // growing must be done here (to get the new buffer size)
            /*int growBy = packet.Size() - getSize();
            if (growBy > 0)
                grow(growBy);

            delegate.clone(packet);*/

            int growBy = packet.size() - getSize();
            if (growBy > 0)
                grow(growBy);

            int res = avcodecLibrary.av_packet_copy_props(getPointer(), Pointer.getPointer(packet));
            if (res != 0)
                throw new LibavRuntimeException(res);

            Pointer<Byte> pData = packet.data();
            if (pData != null)
                pData.copyTo(getData(), packet.size());
            setSize(packet.size());

            clearWrapperCache();
        }

        public void rebind(Pointer<AVPacket> pointer) {
            delegate = new AVPacket(pointer);
        }
    }

}
