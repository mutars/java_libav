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
package com.mutar.libav.api.avresample;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;

import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.api.util.AVUtilLibraryUtil;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVSampleFormat;

public class AudioResampleContextWrapperLAVC implements IAudioResampleContextWrapper<AvcodecLibrary.ReSampleContext> {

    private long inChannelLayout;
    private IntValuedEnum<AVSampleFormat> inSampleFormat;
    private int inSampleRate;
    private long outChannelLayout;
    private IntValuedEnum<AVSampleFormat> outSampleFormat;
    private int outSampleRate;

    private Pointer<AvcodecLibrary.ReSampleContext > rc;

    public AudioResampleContextWrapperLAVC(Pointer<AvcodecLibrary.ReSampleContext > resampleContext) {
        this.rc = null;

        inChannelLayout = AvutilLibrary.AV_CH_LAYOUT_STEREO;
        inSampleFormat = AVSampleFormat.AV_SAMPLE_FMT_S16;
        inSampleRate = 44100;
        outChannelLayout = AvutilLibrary.AV_CH_LAYOUT_STEREO;
        outSampleFormat = AVSampleFormat.AV_SAMPLE_FMT_S16;
        outSampleRate = 44100;
    }

    @Override
    public void clearWrapperCache() {
    }

    @Override
    public Pointer<AvcodecLibrary.ReSampleContext > getPointer() {
        return rc;
    }

    @Override
    public void rebind(Pointer<AvcodecLibrary.ReSampleContext > pointer) {
        rc = pointer;
    }

    @Override
    public void open() throws LibavException {
        if (rc != null)
            close();

        int inChannelCount = AVUtilLibraryUtil.getChannelCount(inChannelLayout);
        int outChannelCount = AVUtilLibraryUtil.getChannelCount(outChannelLayout);
        rc = AVCodecLibraryUtil.audioResampleInit(outChannelCount, inChannelCount, outSampleRate, inSampleRate, outSampleFormat, inSampleFormat, 16, 10, 0, 0.8);
        if (rc == null)
            throw new LibavException("unable to create audio resample context");
    }

    @Override
    public void close() {
        if (rc == null)
            return;

        AVCodecLibraryUtil.audioResampleClose(rc);
        rc = null;
    }

    @Override
    public void free() {
        close();
    }

    @Override
    public long getInputChannelLayout() {
        return inChannelLayout;
    }

    @Override
    public void setInputChannelLayout(long channelLayout) {
        inChannelLayout = channelLayout;
    }

    @Override
    public IntValuedEnum<AVSampleFormat> getInputSampleFormat() {
        return inSampleFormat;
    }

    @Override
    public void setInputSampleFormat(IntValuedEnum<AVSampleFormat> sampleFormat) {
        inSampleFormat = sampleFormat;
    }

    @Override
    public int getInputSampleRate() {
        return inSampleRate;
    }

    @Override
    public void setInputSampleRate(int sampleRate) {
        inSampleRate = sampleRate;
    }

    @Override
    public long getOutputChannelLayout() {
        return outChannelLayout;
    }

    @Override
    public void setOutputChannelLayout(long channelLayout) {
        outChannelLayout = channelLayout;
    }

    @Override
    public IntValuedEnum<AVSampleFormat> getOutputSampleFormat() {
        return outSampleFormat;
    }

    @Override
    public void setOutputSampleFormat(IntValuedEnum<AVSampleFormat> sampleFormat) {
        outSampleFormat = sampleFormat;
    }

    @Override
    public int getOutputSampleRate() {
        return outSampleRate;
    }

    @Override
    public void setOutputSampleRate(int sampleRate) {
        outSampleRate = sampleRate;
    }

    @Override
    public double[] getMatrix(int stride) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void setMatrix(double[] matrix, int stride) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public int convert(Pointer<Pointer<Byte>> output, int outPlaneSize, int outSampleCount, Pointer<Pointer<Byte>> input, int inPlaneSize, int inSampleCount) throws LibavException {
        if (rc == null || input == null)
            return 0;

        Pointer<Short> inputBuffer = input.get().as(Short.class);
        Pointer<Short> outputBuffer = output.get().as(Short.class);
        int len = AVCodecLibraryUtil.audioResample(rc, outputBuffer, inputBuffer, inSampleCount);
        if (len == 0)
            throw new LibavException("audio resample error");

        return len;
    }

    public static IAudioResampleContextWrapper allocate() {
        return new AudioResampleContextWrapperLAVC(null);
    }

}
