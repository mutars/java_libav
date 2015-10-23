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
    public PooledPacket getEmptyPacket() {
        PooledPacket result = getPacket();
        result.alloc();

        return result;
    }

    /**
     * Clone existing packet.
     *
     * @param packet a packet to be clonned
     * @return packet clone
     */
    public PooledPacket clonePacket(AVPacket packet) {
        PooledPacket result = getPacket();
        result.clone(packet);

        return result;
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

    public class PooledPacket {

        private AvcodecLibrary avcodecLibrary= LibraryManager.getInstance().getAvCodec();
        private int bufferSize;
        private AVPacket internal;

        public PooledPacket(AVPacket internal) {
            this.internal = internal;
            this.bufferSize = internal.size();
        }

        @Override
        protected void finalize() throws Throwable {
            dispose();
            super.finalize();
        }

        public synchronized void dispose() {
            if (internal == null)
                return;
            Pointer<AVPacket> pointer = Pointer.getPointer(internal);
            avcodecLibrary.av_free_packet(pointer);
            internal = null;
            //pointer.release();
        }

        public void grow(int growBy) {
            int result = avcodecLibrary.av_grow_packet(Pointer.getPointer(internal), growBy);
            if (result != 0)
                throw new LibavRuntimeException(result);
            bufferSize = internal.size();
        }

        //public void shrink(int size) {
            //avcodecLibrary.av_shrink_packet(Pointer.getPointer(internal), size);
        //}

        public int getStreamIndex() {
            return internal.stream_index();
        }

        /*public void setStreamIndex(int streamIndex) {
            delegate.stream_index(streamIndex);
        } */

        public int getSize() {
            return internal.size();
        }

        public void setSize(int size) {
            internal.size(size);
        }

        public Pointer<Byte> getData() {
            return internal.data();
        }

        /*public void setData(Pointer<Byte> data) {
            internal.data(data);
        }*/

        public PooledPacket alloc() {
            avcodecLibrary.av_init_packet(Pointer.getPointer(internal));
            bufferSize = internal.size();
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
        } */

        public long getDts() {
            return internal.dts();
        }

        public void setDts(long dts) {
            internal.dts(dts);
        }

/*
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


        public PooledPacket clone() {
            return clonePacket(internal);
        }

        public AVPacket getDelegate() {
            return internal;
        }


        public void clone(AVPacket packet) {
            //growing must be done here (to get the new buffer size)
            int growBy = packet.size() - getSize();
            if (growBy > 0)
                grow(growBy);

            int res = avcodecLibrary.av_packet_copy_props(Pointer.getPointer(internal), Pointer.getPointer(packet));
            if (res != 0)
                throw new LibavRuntimeException(res);

            Pointer<Byte> pData = packet.data();
            if (pData != null)
                pData.copyTo(getData(), packet.size());
            setSize(packet.size());
        }

        /*public void rebind(Pointer<AVPacket> pointer) {
            internal = new AVPacket(pointer);
            bufferSize = internal.size();
        }*/

        public void free() {
            setSize(bufferSize);
            recycle(this);
        }
    }

}
