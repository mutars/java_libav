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
package com.mutar.libav.api.util;

import org.bridj.Pointer;

import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVPixelFormat;
import com.mutar.libav.bridge.swscale.SwsFilter;
import com.mutar.libav.bridge.swscale.SwscaleLibrary;
import com.mutar.libav.service.LibraryManager;

public class SwscaleLibraryUtils {

    private static final SwscaleLibrary scaleLib ;

    static {
        scaleLib  = LibraryManager.getInstance().getSwScale();
    }

    public static void free(Pointer<SwscaleLibrary.SwsContext > scaleContextPointer) {
        if (scaleContextPointer == null)
            return;

        scaleLib.sws_freeContext(scaleContextPointer);
        scaleContextPointer = null;
    }

    public static int scale(Pointer<SwscaleLibrary.SwsContext > scaleContextPointer, AVFrame src, AVFrame dst, int srcSliceY, int srcSliceHeight) throws LibavException {
        if (scaleContextPointer == null)
            throw new LibavException("current context has been freed");

        return scaleLib.sws_scale(scaleContextPointer, src.data(), src.linesize(), srcSliceY, srcSliceHeight, dst.data(), dst.linesize());
    }

    public static Pointer<SwscaleLibrary.SwsContext > createContext(int srcWidth, int srcHeight, AVPixelFormat srcFormat, int dstWidth, int dstHeight, AVPixelFormat dstFormat, int flags) throws LibavException {
        return allocateContext(srcWidth, srcHeight, srcFormat, dstWidth, dstHeight, dstFormat, flags, null, null, null);
    }

    public static Pointer<SwscaleLibrary.SwsContext > createContext(int srcWidth, int srcHeight, AVPixelFormat srcFormat, int dstWidth, int dstHeight, AVPixelFormat dstFormat, int flags, Pointer<SwsFilter > srcFilter, Pointer<SwsFilter > dstFilter) throws LibavException {
        return allocateContext(srcWidth, srcHeight, srcFormat, dstWidth, dstHeight, dstFormat, flags, srcFilter, dstFilter, null);
    }

    public static Pointer<SwscaleLibrary.SwsContext > createContext(int srcWidth, int srcHeight, AVPixelFormat srcFormat, int dstWidth, int dstHeight, AVPixelFormat dstFormat, int flags, Pointer<SwsFilter > srcFilter, Pointer<SwsFilter > dstFilter, Pointer<Double > param) throws LibavException {
        return allocateContext(srcWidth, srcHeight, srcFormat, dstWidth, dstHeight, dstFormat, flags, srcFilter, dstFilter, param);
    }

    private static Pointer<SwscaleLibrary.SwsContext > allocateContext(int srcWidth, int srcHeight, AVPixelFormat srcFormat, int dstWidth, int dstHeight, AVPixelFormat dstFormat, int flags, Pointer<SwsFilter > srcFilter, Pointer<SwsFilter > dstFilter, Pointer<Double > param) throws LibavException {
        Pointer<SwscaleLibrary.SwsContext > result = scaleLib.sws_getCachedContext(null, srcWidth, srcHeight, srcFormat, dstWidth, dstHeight, dstFormat, flags, srcFilter, dstFilter, param);
        if (result == null)
            throw new LibavException("unable to create scale context");

        return result;
    }

}
