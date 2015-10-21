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
package com.mutar.libav.api.util.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.bridj.Pointer;

import com.mutar.libav.api.data.IFrameConsumer;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVUtilLibraryUtil;
import com.mutar.libav.api.util.SwscaleLibraryUtils;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVPixelFormat;
import com.mutar.libav.bridge.swscale.SwscaleLibrary;

/**
 * SWING component for video rendering.
 *
 * @author Ondrej Perutka
 */
public class VideoPane extends JComponent implements IFrameConsumer {

    /**
     *
     */
    private static final long serialVersionUID = 3052801530279034727L;

    /**
     * Choose the best output mode for current platform.
     */
    public static final int OUTPUT_DEFAULT = 0;

    /**
     * Output video frames using SWING.
     */
    public static final int OUTPUT_SWING = 1;

    /**
     * Output video frames using XVideo (if available).
     */
    public static final int OUTPUT_XV = 2;

    /**
     * Output video frames using DirectDraw (if available).
     */
    public static final int OUTPUT_DDRAW = 3;

    private Pointer<SwscaleLibrary.SwsContext> scaleContextPointer;
    private AVFrame rgbFrame;
    private Pointer<Byte> rgbFrameData;
    private int rgbFrameStride;
    private BufferedImage img;
    private int[] imageData;

    private int x;
    private int y;
    private int srcWidth;
    private int srcHeight;
    private AVPixelFormat srcPixelFormat;
    private int dstWidth;
    private int dstHeight;
    private AVPixelFormat dstPixelFormat;
    private int scalingAlgorithm;

    private Insets insts;

    /**
     * Create a new video pane.
     */
    public VideoPane() {
        setBackground(Color.black);
        setOpaque(true);

        scaleContextPointer = null;
        rgbFrame = null;
        rgbFrameData = null;
        rgbFrameStride = 0;
        img = null;
        imageData = null;

        x = 0;
        y = 0;
        srcWidth = 0;
        srcHeight = 0;
        srcPixelFormat = AVPixelFormat.AV_PIX_FMT_YUV420P;
        scalingAlgorithm = SwscaleLibrary.SWS_BICUBIC;
        dstWidth = 0;
        dstHeight = 0;
        dstPixelFormat = AVPixelFormat.AV_PIX_FMT_BGRA;
        if (ByteOrder.BIG_ENDIAN.equals(ByteOrder.nativeOrder()))
            dstPixelFormat = AVPixelFormat.AV_PIX_FMT_ARGB;

        insts = null;

        addComponentListener(new ResizeHandler());
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return true;
    }

    @Override
    public synchronized void paintComponent(Graphics grphcs) {
        Color prev = grphcs.getColor();

        grphcs.setColor(getBackground());
        grphcs.fillRect(0, 0, getWidth(), getHeight());
        grphcs.setColor(prev);

        if (scaleContextPointer != null)
            grphcs.drawImage(img, x, y, dstWidth, dstHeight, this);
    }

    /**
     * Get the X position of the last frame inside this component.
     *
     * @return X position of the last frame
     */
    public int getFrameX() {
        return x;
    }

    /**
     * Get the Y position of the last frame inside this component.
     *
     * @return Y position of the last frame
     */
    public int getFrameY() {
        return y;
    }

    /**
     * Get width of the last frame inside this component.
     *
     * @return width of the last frame
     */
    public int getFrameWidth() {
        return dstWidth > 0 ? dstWidth : getWidth();
    }

    /**
     * Get height of the last frame inside this component.
     *
     * @return height of the last frame
     */
    public int getFrameHeight() {
        return dstHeight > 0 ? dstHeight : getHeight();
    }

    /**
     * Get expected source frame width (default value is 0).
     *
     * @return expected source frame width
     */
    public int getSourceWidth() {
        return srcWidth;
    }

    /**
     * Get expected source frame height (default value is 0).
     *
     * @return expected source frame height
     */
    public int getSourceHeight() {
        return srcHeight;
    }

    /**
     * Get expected source frame pixel format.
     *
     * @return expected source frame pixel format
     */
    public AVPixelFormat getSourcePixelFormat() {
        return srcPixelFormat;
    }

    /**
     * Get ID of the selected scaling algorithm (default selection is
     * FAST_BILINEAR).
     *
     * @return ID of the selected scaling algorithm
     */
    public int getScalingAlgorithm() {
        return scalingAlgorithm;
    }

    /**
     * Set expected format of source images.
     *
     * @param width a width
     * @param height a height
     * @param pixelFormat a pixel format
     */
    public synchronized void setSourceImageFormat(int width, int height, AVPixelFormat pixelFormat) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("illegal frame size");

        srcWidth = width;
        srcHeight = height;
        srcPixelFormat = pixelFormat;

        resetScaleContext();
    }

    /**
     * Set scaling algorithm.
     *
     * @param scalingAlgorithm a scaling algorithm
     */
    public synchronized void setScalingAlgorithm(int scalingAlgorithm) {
        this.scalingAlgorithm = scalingAlgorithm;
        resetScaleContext();
    }

    private synchronized void resetScaleContext() {
        disposeScaleContext();

        if (srcWidth <= 0 || srcHeight <= 0)
            return;

        insts = getInsets(insts);
        int w = getWidth() - insts.left - insts.right;
        int h = getHeight() - insts.top - insts.bottom;
        double r = Math.min((double)w / srcWidth, (double)h / srcHeight);

        x = insts.left + (int)((w - srcWidth * r) / 2);
        y = insts.top + (int)((h - srcHeight * r) / 2);
        dstWidth = (int)(srcWidth * r);
        dstHeight = (int)(srcHeight * r);

        try {
            scaleContextPointer = SwscaleLibraryUtils.createContext(srcWidth, srcHeight, srcPixelFormat, dstWidth, dstHeight, dstPixelFormat, scalingAlgorithm);
            rgbFrame = AVUtilLibraryUtil.allocatePicture(dstPixelFormat, dstWidth, dstHeight);
            rgbFrameData = rgbFrame.data().get();
            rgbFrameStride = rgbFrame.linesize().get();
        } catch (LibavException ex) {
            Logger.getLogger(VideoPane.class.getName()).log(Level.SEVERE, "unable initialize video pane scaling context", ex);
            if (scaleContextPointer != null)
                SwscaleLibraryUtils.free(scaleContextPointer);
            scaleContextPointer = null;
            return;
        }

        imageData = new int[dstWidth * dstHeight];
        DataBuffer db = new DataBufferInt(imageData, imageData.length);
        int[] masks = new int[] { 0x00ff0000, 0x0000ff00, 0x000000ff };
        SampleModel sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, dstWidth, dstHeight, masks);
        WritableRaster wr = Raster.createWritableRaster(sm, db, new Point());
        img = new BufferedImage(new DirectColorModel(24, 0x00ff0000, 0x0000ff00, 0x000000ff), wr, false, null);
    }

    private synchronized void disposeScaleContext() {
        if (scaleContextPointer != null)
            SwscaleLibraryUtils.free(scaleContextPointer);
        if (rgbFrame != null)
            AVUtilLibraryUtil.free(rgbFrame);

        scaleContextPointer = null;
    }

    /**
     * Dispose all resources held by this object.
     */
    public void dispose() {
        disposeScaleContext();
    }

    /**
     * Clear the component with the background color.
     */
    public synchronized void clear() {
        if (scaleContextPointer != null) {
            for (int i = 0; i < imageData.length; i++)
                imageData[i] = 0;
        }

        repaint();
    }

    @Override
    public synchronized void processFrame(Object producer, AVFrame frame) {
        if (scaleContextPointer == null)
            return;

        try {
            SwscaleLibraryUtils.scale(scaleContextPointer, frame, rgbFrame, 0, srcHeight);
            if ((dstWidth * 4) == rgbFrameStride)
                rgbFrameData.getIntsAtOffset(0, imageData, 0, imageData.length);
            else {
                int frameOffset = 0;
                int dataOffset = 0;
                while (dataOffset < imageData.length) {
                    rgbFrameData.getIntsAtOffset(frameOffset, imageData, dataOffset, dstWidth);
                    frameOffset += rgbFrameStride;
                    dataOffset += dstWidth;
                }
            }
            repaint();
        } catch (LibavException ex) {
            Logger.getLogger(VideoPane.class.getName()).log(Level.WARNING, "video pane has uninitielized source image format", ex);
        }
    }

    private class ResizeHandler extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            resetScaleContext();
            repaint();
        }
    }

}
