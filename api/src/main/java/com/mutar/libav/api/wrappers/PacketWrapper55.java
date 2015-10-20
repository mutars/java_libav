/*
 * Copyright (C) 2013 Ondrej Perutka
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
package com.mutar.libav.api.wrappers;

import org.bridj.Pointer;

import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.exception.LibavRuntimeException;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.service.LibraryManager;

/**
 * Wrapper class for the AVPacket55.
 *
 * @author Ondrej Perutka
 */
public class PacketWrapper extends AbstractPacketWrapper {

    private static final AvcodecLibrary codecLib = LibraryManager.getInstance().getAvCodec();

    private AVPacket packet;

    /**
     * Create a new wrapper for the given AVPacket.
     *
     * @param packet an AVPacket structure
     */
    public PacketWrapper(AVPacket packet) {
        this.packet = packet;
    }

    @Override
    public Pointer<?> getPointer() {
        return Pointer.pointerTo(packet);
    }

    @Override
    public void rebind(Pointer<?> pointer) {
        packet = new AVPacket55(pointer);
    }

    @Override
    public void init() {
        codecLib.av_init_packet(getPointer());
        clearWrapperCache();
    }

    @Override
    public void free() {
        codecLib.av_free_packet(getPointer());
        clearWrapperCache();
    }

    @Override
    public void grow(int growBy) {
        int result = codecLib.av_grow_packet(getPointer(), growBy);
        if (result != 0)
            throw new LibavRuntimeException(result);
        data = null;
        size = null;
    }

    @Override
    public void shrink(int size) {
        codecLib.av_shrink_packet(getPointer(), size);
        this.data = null;
        this.size = null;
    }

    @Override
    public int getStreamIndex() {
        if (streamIndex == null)
            streamIndex = packet.stream_index();

        return streamIndex;
    }

    @Override
    public void setStreamIndex(int streamIndex) {
        this.streamIndex = streamIndex;
        packet.stream_index(streamIndex);
    }

    @Override
    public int getSize() {
        if (size == null)
            size = packet.size();

        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
        packet.size(size);
    }

    @Override
    public Pointer<Byte> getData() {
        if (data == null)
            data = packet.data();

        return data;
    }

    @Override
    public void setData(Pointer<Byte> data) {
        this.data = data;
        packet.data(data);
    }

    @Override
    public int getFlags() {
        if (flags == null)
            flags = packet.flags();

        return flags;
    }

    @Override
    public void setFlags(int flags) {
        this.flags = flags;
        packet.flags(flags);
    }

    @Override
    public long getPts() {
        if (pts == null)
            pts = packet.pts();

        return pts;
    }

    @Override
    public void setPts(long pts) {
        this.pts = pts;
        packet.pts(pts);
    }

    @Override
    public long getDts() {
        if (dts == null)
            dts = packet.dts();

        return dts;
    }

    @Override
    public void setDts(long dts) {
        this.dts = dts;
        packet.dts(dts);
    }

    @Override
    public int getDuration() {
        if (duration == null)
            duration = packet.duration();

        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
        packet.duration(duration);
    }

    @Override
    public long getConvergenceDuration() {
        if (convergenceDuration == null)
            convergenceDuration = packet.convergence_duration();

        return convergenceDuration;
    }

    @Override
    public void setConvergenceDuration(long convergenceDuration) {
        this.convergenceDuration = convergenceDuration;
        packet.convergence_duration(convergenceDuration);
    }

    @Override
    public long getPosition() {
        if (position == null)
            position = packet.pos();

        return position;
    }

    @Override
    public void setPosition(long position) {
        this.position = position;
        packet.pos(position);
    }

    @Override
    public Pointer<?> getSideData() {
        if (sideData == null)
            sideData = packet.side_data();

        return sideData;
    }

    @Override
    public void setSideData(Pointer<?> sideData) {
        this.sideData = sideData;
        packet.side_data(sideData == null ? null : sideData.as(AVPacket55.SideData.class));
    }

    @Override
    public int getSideDataElems() {
        if (sideDataElems == null)
            sideDataElems = packet.side_data_elems();

        return sideDataElems;
    }

    @Override
    public void setSideDataElems(int sideDataElems) {
        this.sideDataElems = sideDataElems;
        packet.side_data_elems(sideDataElems);
    }

    @Override
    public PacketWrapper55 clone() {
        PacketWrapper55 result = new PacketWrapper55(new AVPacket55());
        int res = codecLib.av_new_packet(result.getPointer(), getSize());
        if (res != 0)
            throw new LibavRuntimeException(res);

        res = codecLib.av_packet_copy_props(result.getPointer(), getPointer());
        if (res != 0)
            throw new LibavRuntimeException(res);

        Pointer<Byte> pData = getData();
        if (pData != null)
            pData.copyTo(result.getData(), getSize());
        result.setSize(getSize());

        return result;
    }

    @Override
    public void clone(IPacketWrapper packet) {
        int growBy = packet.getSize() - getSize();
        if (growBy > 0)
            grow(growBy);

        int res = codecLib.av_packet_copy_props(getPointer(), packet.getPointer());
        if (res != 0)
            throw new LibavRuntimeException(res);

        Pointer<Byte> pData = packet.getData();
        if (pData != null)
            pData.copyTo(getData(), packet.getSize());
        setSize(packet.getSize());

        clearWrapperCache();
    }

    public static PacketWrapper55 allocatePacket() {
        PacketWrapper55 result = new PacketWrapper55(new AVPacket55());
        result.init();

        return result;
    }

    public static PacketWrapper55 allocatePacket(int size) throws LibavException {
        PacketWrapper55 result = allocatePacket();
        int res = codecLib.av_new_packet(result.getPointer(), size);
        if (res != 0)
            throw new LibavException(res);

        return result;
    }

}
