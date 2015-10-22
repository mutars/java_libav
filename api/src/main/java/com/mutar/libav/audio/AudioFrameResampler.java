package com.mutar.libav.audio;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;

import com.mutar.libav.api.avresample.AudioResampleContextWrapperFactory;
import com.mutar.libav.api.avresample.IAudioResampleContextWrapper;
import com.mutar.libav.api.data.IFrameConsumer;
import com.mutar.libav.api.data.IFrameProducer;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.api.util.AVUtilLibraryUtil;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVSampleFormat;

public class AudioFrameResampler implements IFrameConsumer, IFrameProducer {

    private long inputChannelLayout;
    private int inputChannelCount;
    private int inputSampleRate;
    private IntValuedEnum<AVSampleFormat> inputSampleFormat;
    private int inputBytesPerSample;

    private long outputChannelLayout;
    private int outputChannelCount;
    private int outputSampleRate;
    private IntValuedEnum<AVSampleFormat> outputSampleFormat;

    private IAudioResampleContextWrapper resampleContext;
    private ResampleBuffer resampleBuffer;
    private AVFrame outputFrame;

    private final Set<IFrameConsumer> consumers;

    /**
     * Create a new audio frame resampler and set resampling parameters.
     *
     * @param inputChannelLayout an input channel layout
     * @param outputChannelLayout an output channel layout
     * @param inputSampleRate a sample rate of the input frames
     * @param outputSampleRate a sample rate of the output frames
     * @param inputSampleFormat a sample format of the input frames
     * @param outputSampleFormat a sample format of the output frames
     * @throws LibavException if an error occurs
     */
    public AudioFrameResampler(long inputChannelLayout, long outputChannelLayout, int inputSampleRate, int outputSampleRate, AVSampleFormat inputSampleFormat, AVSampleFormat outputSampleFormat) throws LibavException {
        this.inputChannelLayout = inputChannelLayout;
        this.inputChannelCount = AVUtilLibraryUtil.getChannelCount(inputChannelLayout);
        this.inputSampleRate = inputSampleRate;
        this.inputSampleFormat = inputSampleFormat;
        this.inputBytesPerSample = AVUtilLibraryUtil.getBytesPerSample(inputSampleFormat);

        this.outputChannelLayout = outputChannelLayout;
        this.outputChannelCount = AVUtilLibraryUtil.getChannelCount(outputChannelLayout);
        this.outputSampleRate = outputSampleRate;
        this.outputSampleFormat = outputSampleFormat;

        resampleContext = null;
        resampleBuffer = null;
        outputFrame = null;

        init();

        consumers = Collections.synchronizedSet(new HashSet<IFrameConsumer>());
    }

    private void init() throws LibavException {
        if (resampleContext != null)
            resampleContext.free();
        if (resampleBuffer != null)
            resampleBuffer.free();
        if (outputFrame != null)
            AVUtilLibraryUtil.free(outputFrame);

        resampleContext = null;
        resampleBuffer = null;
        outputFrame = null;

        if (inputChannelLayout != outputChannelLayout || inputSampleFormat != outputSampleFormat || inputSampleRate != outputSampleRate) {
            resampleContext = AudioResampleContextWrapperFactory.getInstance().allocate();
            resampleContext.setInputChannelLayout(inputChannelLayout);
            resampleContext.setInputSampleFormat(inputSampleFormat);
            resampleContext.setInputSampleRate(inputSampleRate);
            resampleContext.setOutputChannelLayout(outputChannelLayout);
            resampleContext.setOutputSampleFormat(outputSampleFormat);
            resampleContext.setOutputSampleRate(outputSampleRate);
            resampleContext.open();
            resampleBuffer = new ResampleBuffer(outputSampleFormat, outputChannelCount);
            outputFrame = AVUtilLibraryUtil.allocateFrame();
        }
    }

    /**
     * Set format of input samples.
     *
     * @param channelLayout a channel layout
     * @param sampleRate a sample rate
     * @param sampleFormat a sample format
     * @throws LibavException if an error occurs
     */
    public synchronized void setInputFormat(long channelLayout, int sampleRate, AVSampleFormat sampleFormat) throws LibavException {
        if (inputChannelLayout != channelLayout || inputSampleRate != sampleRate || inputSampleFormat != sampleFormat) {
            inputChannelLayout = channelLayout;
            inputChannelCount = AVUtilLibraryUtil.getChannelCount(channelLayout);
            inputSampleRate = sampleRate;
            inputSampleFormat = sampleFormat;
            inputBytesPerSample = AVUtilLibraryUtil.getBytesPerSample(sampleFormat);
            init();
        }
    }

    /**
     * Get expected input channel layout.
     *
     * @return expected input channel layout
     */
    public long getInputChannelLayout() {
        return inputChannelLayout;
    }

    /**
     * Get number of input channels.
     *
     * @return number of input channels
     */
    public int getInputChannelCount() {
        return inputChannelCount;
    }

    /**
     * Get expected sample format of the input frames.
     *
     * @return expected sample format of the input frames
     */
    public IntValuedEnum<AVSampleFormat> getInputSampleFormat() {
        return inputSampleFormat;
    }

    /**
     * Get expected sample rate of the input frames.
     *
     * @return expected sample rate of the input frames
     */
    public int getInputSampleRate() {
        return inputSampleRate;
    }

    /**
     * Set format of output samples.
     *
     * @param channelLayout a channel layout
     * @param sampleRate a sample rate
     * @param sampleFormat a sample format
     * @throws LibavException if an error occurs
     */
    public synchronized void setOutputFormat(long channelLayout, int sampleRate, AVSampleFormat sampleFormat) throws LibavException {
        if (outputChannelLayout != channelLayout || outputSampleRate != sampleRate || outputSampleFormat != sampleFormat) {
            outputChannelLayout = channelLayout;
            outputChannelCount = AVUtilLibraryUtil.getChannelCount(channelLayout);
            outputSampleRate = sampleRate;
            outputSampleFormat = sampleFormat;
            init();
        }
    }

    /**
     * Get output channel layout.
     *
     * @return output channel layout
     */
    public long getOutputChannelLayout() {
        return outputChannelLayout;
    }

    /**
     * Get number of output channels.
     *
     * @return number of output channels
     */
    public int getOutputChannelCount() {
        return outputChannelCount;
    }

    /**
     * Get sample format of the output frames.
     *
     * @return sample format of the output frames
     */
    public IntValuedEnum<AVSampleFormat> getOutputSampleFormat() {
        return outputSampleFormat;
    }

    /**
     * Get sample rate of the output frames.
     *
     * @return sample rate of the output frames
     */
    public int getOutputSampleRate() {
        return outputSampleRate;
    }

    /**
     * Release all native resources.
     */
    public synchronized void dispose() {
        //if (resampleContext != null)
            //resampleContext.free();
        if (resampleBuffer != null)
            resampleBuffer.free();
        if (outputFrame != null)
            AVUtilLibraryUtil.free(outputFrame);

        //resampleContext = null;
        resampleBuffer = null;
        outputFrame = null;
    }

    @Override
    public void processFrame(Object producer, AVFrame frame) throws LibavException {
        if (resampleContext == null)
            sendFrame(frame);
        else {
            Pointer<Pointer<Byte>> inputData = frame.data();
            int inPlaneSize = frame.linesize().get(0);
            int inSampleCount = inPlaneSize / inputBytesPerSample;
            if (AVUtilLibraryUtil.isPlanar(inputSampleFormat))
                inputData = frame.extended_data();
            else
                inSampleCount /= inputChannelCount;

            int outSampleCount = resampleContext.convert(resampleBuffer.getData(),
                    resampleBuffer.getLineSize(), resampleBuffer.getMaxSampleCount(),
                    inputData, inPlaneSize, inSampleCount);

            AVCodecLibraryUtil.fillAudioFrame(outputFrame, outSampleCount, outputChannelCount, outputSampleFormat,
                    resampleBuffer.getBuffer(), resampleBuffer.getBufferSize(), resampleBuffer.getMaxSampleCount());

            outputFrame.key_frame(frame.key_frame());
            outputFrame.pkt_dts(frame.pkt_dts());
            outputFrame.pkt_pts(frame.pkt_pts());
            outputFrame.pts(frame.pts());

            sendFrame(outputFrame);
        }
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

    public int getConsumerCount() {
        return consumers.size();
    }

    private static class ResampleBuffer {
        private Pointer<Pointer<?>> data;
        private Pointer<Byte> buffer;
        private final int bufferSize;
        private int lineSize;
        private int maxSampleCount;

        public ResampleBuffer(IntValuedEnum<AVSampleFormat> sampleFormat, int channelCount) {
            int planes = AVUtilLibraryUtil.isPlanar(sampleFormat) ? channelCount : 1;

            bufferSize = AVCodecLibraryUtil.AVCODEC_MAX_AUDIO_FRAME_SIZE;
            buffer = AVUtilLibraryUtil.malloc(bufferSize + AvcodecLibrary.FF_INPUT_BUFFER_PADDING_SIZE).as(Byte.class);
            if (buffer == null)
                throw new OutOfMemoryError("not enough memory for the audio frame resampler");

            int bytesPerSample = AVUtilLibraryUtil.getBytesPerSample(sampleFormat);
            lineSize = bufferSize / planes;
            lineSize -= lineSize % bytesPerSample;

            data = Pointer.allocatePointers(planes);
            for (int i = 0; i < planes; i++)
                data.set(i, buffer.offset(i * lineSize));

            maxSampleCount = lineSize * planes / (channelCount * bytesPerSample);
        }

        public void free() {
            if (buffer == null)
                return;

            AVUtilLibraryUtil.av_free(buffer);

            buffer = null;
            data = null;
        }

        public Pointer<Pointer<?>> getData() {
            return data;
        }

        public int getLineSize() {
            return lineSize;
        }

        public Pointer<Byte> getBuffer() {
            return buffer;
        }

        public int getBufferSize() {
            return bufferSize;
        }

        public int getMaxSampleCount() {
            return maxSampleCount;
        }
    }

}
