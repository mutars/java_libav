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
package com.mutar.libav.video;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;

import com.mutar.libav.api.data.IFrameConsumer;
import com.mutar.libav.api.data.IFrameProducer;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVUtilLibraryUtil;
import com.mutar.libav.api.util.SwscaleLibraryUtils;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVPixelFormat;
import com.mutar.libav.bridge.swscale.SwscaleLibrary;

public class FrameScaler implements IFrameConsumer, IFrameProducer {

    private int srcWidth;
    private int srcHeight;
    private IntValuedEnum<AVPixelFormat> srcFormat;

    private int dstWidth;
    private int dstHeight;
    private IntValuedEnum<AVPixelFormat> dstFormat;

    private Pointer<SwscaleLibrary.SwsContext> scaleContextPointer;
    private int scalingAlg;

    private AVFrame picture;

    private final Set<IFrameConsumer> consumers;

    /**
     * Create a new video frame scaler and set scaling parameters.
     *
     * @param srcWidth a width of source images
     * @param srcHeight a height of source images
     * @param srcPixelFormat a pixel format of source images
     * @param dstWidth a width of produced images
     * @param dstHeight a height of produced images
     * @param dstPixelFormat a pixel format of produced images
     * @throws LibavException if an error occurs
     */
    public FrameScaler(int srcWidth, int srcHeight, IntValuedEnum<AVPixelFormat> srcPixelFormat, int dstWidth, int dstHeight, IntValuedEnum<AVPixelFormat> dstPixelFormat) throws LibavException {
        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;
        this.srcFormat = srcPixelFormat;
        this.dstWidth = dstWidth;
        this.dstHeight = dstHeight;
        this.dstFormat = dstPixelFormat;
        scalingAlg = SwscaleLibrary.SWS_BICUBIC;

        init();

        consumers = Collections.synchronizedSet(new HashSet<IFrameConsumer>());
    }

    private void init() throws LibavException {
        if (scaleContextPointer != null)
            SwscaleLibraryUtils.free(scaleContextPointer);
        if (picture != null)
            AVUtilLibraryUtil.free(picture);
        scaleContextPointer = SwscaleLibraryUtils.createContext(srcWidth, srcHeight, srcFormat, dstWidth, dstHeight, dstFormat, scalingAlg);
        picture = AVUtilLibraryUtil.allocatePicture(dstFormat, dstWidth, dstHeight);
    }

    /**
     * Set expected format of source images.
     *
     * @param width a width
     * @param height a height
     * @param pixelFormat a pixel format
     * @throws LibavException if an error occurs
     */
    public synchronized void setSourceImageFormat(int width, int height, AVPixelFormat pixelFormat) throws LibavException {
        if (width != srcWidth || height != srcHeight || srcFormat != pixelFormat) {
            srcWidth = width;
            srcHeight = height;
            srcFormat = pixelFormat;
            init();
        }
    }

    /**
     * Get expected width of source images.
     *
     * @return expected width of source images
     */
    public int getSourceImageWidth() {
        return srcWidth;
    }

    /**
     * Get expected height of source images.
     *
     * @return expected height of source images
     */
    public int getSourceImageHeight() {
        return srcHeight;
    }

    /**
     * Get expected pixel format of source images.
     *
     * @return expected pixel format of source images
     */
    public IntValuedEnum<AVPixelFormat> getSourceImagePixelFormat() {
        return srcFormat;
    }

    /**
     * Set format of produced images.
     *
     * @param width a width
     * @param height a height
     * @param pixelFormat a pixel format
     * @throws LibavException if an error occurs
     */
    public synchronized void setDestinationImageFormat(int width, int height, AVPixelFormat pixelFormat) throws LibavException {
        if (width != dstWidth || height != dstHeight || pixelFormat != dstFormat) {
            dstWidth = width;
            dstHeight = height;
            dstFormat = pixelFormat;
            init();
        }
    }

    /**
     * Get width of produced images.
     *
     * @return width of produced images
     */
    public int getDestinationImageWidth() {
        return dstWidth;
    }

    /**
     * Get height of produced images.
     *
     * @return height of produced images
     */
    public int getDestinationImageHeight() {
        return dstHeight;
    }

    /**
     * Get pixel format of produced images.
     *
     * @return pixel format of produced images
     */
    public IntValuedEnum<AVPixelFormat> getDestinationPixelFormat() {
        return dstFormat;
    }

    /**
     * Set scaling algorithm.
     *
     * @param scalingAlgorithm a scaling algorithm
     * @throws LibavException if an error occurs
     */
    public synchronized void setScalingAlgorithm(int scalingAlgorithm) throws LibavException {
        if (scalingAlgorithm != scalingAlg) {
            scalingAlg = scalingAlgorithm;
            init();
        }
    }

    /**
     * Get scaling algorithm.
     *
     * @return scaling algorithm
     */
    public int getScalingAlgorithm() {
        return scalingAlg;
    }

    /**
     * Release all native sources.
     */
    public synchronized void dispose() {
        if (scaleContextPointer != null)
            SwscaleLibraryUtils.free(scaleContextPointer);
        if (picture != null)
            AVUtilLibraryUtil.free(picture);

        scaleContextPointer = null;
        picture = null;
    }

    @Override
    public synchronized void processFrame(Object producer, AVFrame frame) throws LibavException {
        if (scaleContextPointer == null)
            return;

        SwscaleLibraryUtils.scale(scaleContextPointer, frame, picture, 0, srcHeight);
        picture.pts(frame.pts());
        sendFrame(picture);
    }

    private void sendFrame(AVFrame frame) throws LibavException {
        synchronized (consumers) {
            for (IFrameConsumer c : consumers)
                c.processFrame(this, frame);
        }
    }

    @Override
    public void addFrameConsumer(IFrameConsumer consumer) {
        consumers.add(consumer);
    }

    @Override
    public void removeFrameConsumer(IFrameConsumer consumer) {
        consumers.remove(consumer);
    }

    public int getConsumetCount() {
        return consumers.size();
    }

}
