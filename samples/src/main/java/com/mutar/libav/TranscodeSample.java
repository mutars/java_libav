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
package com.mutar.libav;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bridj.IntValuedEnum;

import com.mutar.libav.api.IDecoder;
import com.mutar.libav.api.IEncoder;
import com.mutar.libav.api.IMediaDecoder;
import com.mutar.libav.api.IMediaEncoder;
import com.mutar.libav.api.IMediaReader;
import com.mutar.libav.api.IMediaWriter;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.audio.AudioFrameResampler;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary.AVCodecID;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVPixelFormat;
import com.mutar.libav.video.FrameScaler;

public class TranscodeSample {

    public static void main(String[] args) {
        String srcUrl = "/home/sergey/Videos/test1.mp4"; // some source multimedia file/stream
        //String srcUrl = "/home/sergey/Videos/test1.mp4"; // some source multimedia file/stream
        String dstUrl = "/home/sergey/bar2.mkv"; // destination file name
        AVCodecID videoCodecId = AVCodecID.AV_CODEC_ID_MPEG4; // output video codec
        AVCodecID audioCodecId = AVCodecID.AV_CODEC_ID_AAC;

        IMediaDecoder md = null;
        IMediaEncoder me = null;
        IMediaReader mr;
        IMediaWriter mw;
        Runnable callback = null;
        long startTime = System.currentTimeMillis();
        try {
            md = new DefaultMediaDecoder(srcUrl); // open input file/stream
            me = new DefaultMediaEncoder(dstUrl, null); // open output file
            mr = md.getMediaReader();
            mw = me.getMediaWriter();
            // init video transcoding of the first video stream if there is at
            // least one video stream
            if (mr.getVideoStreamCount() > 0) {
                md.setVideoStreamDecodingEnabled(0, true);
                callback = TranscodeSample.createEncodingChain(md, videoCodecId, me);
            }

            // init audio transcoding of the first audio stream if there is at
            // least one audio stream
            if (mr.getAudioStreamCount() > 0) {
                md.setAudioStreamDecodingEnabled(0, true);
                addAudioStreamChain(md, audioCodecId, me);
            }

            mw.writeHeader(); // write file header
            boolean hasNext = true;
            while (hasNext) {
                try {
                    hasNext = mr.readNextPacket();
                } catch (LibavException ex) {
                    Logger.getLogger(TranscodeSample.class.getName()).log(Level.WARNING, "wrong packet", ex);
                }
            }
            md.flush();
            me.flush();
            mw.writeTrailer(); // write file trailer
        } catch (Exception ex) {
            Logger.getLogger(TranscodeSample.class.getName()).log(Level.SEVERE, "oooops", ex);
        } finally {
            try {
                if (md != null)
                    md.close();
                    callback.run();
            } catch (Exception ex) {
                Logger.getLogger(TranscodeSample.class.getName()).log(Level.SEVERE, "cannot close that", ex);
            }

        }
        System.out.println("total encoding time = " + (System.currentTimeMillis() - startTime)/1000);
    }

    private static void addAudioStreamChain(IMediaDecoder md,AVCodecID audioCodecId, IMediaEncoder me) throws LibavException {
        IDecoder dec = md.getAudioStreamDecoder(0);
        AVCodecContext cc1 = dec.getCodecContext();
        int si = me.getMediaWriter().addAudioStream(audioCodecId, cc1.sample_rate(), cc1.sample_fmt(), cc1.channels());
        IEncoder audioStreamEncoder = me.getAudioStreamEncoder(si);
        AVCodecContext codecContext = audioStreamEncoder.getCodecContext();
        if (codecContext.sample_fmt() != cc1.sample_fmt()) {
            AudioFrameResampler resampler = new AudioFrameResampler(cc1.channel_layout(),codecContext.channel_layout(),cc1.sample_rate(),codecContext.sample_rate(),cc1.sample_fmt(),codecContext.sample_fmt());
            resampler.addFrameConsumer(audioStreamEncoder);
            dec.addFrameConsumer(resampler);
        } else {
            dec.addFrameConsumer(audioStreamEncoder);
        }
    }

    public static Runnable createEncodingChain(IMediaDecoder md, AVCodecID videoCodecId, IMediaEncoder me) throws LibavException {
        IDecoder dec = md.getVideoStreamDecoder(0);
        AVCodecContext cc1 = dec.getCodecContext();
        int si = me.getMediaWriter().addVideoStream(videoCodecId, cc1.width(), cc1.height());
        IEncoder enc = me.getVideoStreamEncoder(si);
        AVCodecContext cc2 = enc.getCodecContext();
        IntValuedEnum<AVPixelFormat> bestPixelFormat = AVCodecLibraryUtil.avcodecFindBestPixFmtOfList(cc2.codec().get().pix_fmts(), cc1.pix_fmt(), 0, null);
        cc2.pix_fmt(bestPixelFormat);
        FrameScaler scaler = new FrameScaler(cc1.width(), cc1.height(), cc1.pix_fmt(), cc2.width(), cc2.height(), cc2.pix_fmt());
        scaler.addFrameConsumer(enc);
        dec.addFrameConsumer(scaler);
        return new Runnable() {

            @Override
            public void run() {
                try {
                    if(me != null)
                    me.close();
                    if(scaler != null)
                    scaler.dispose();
                } catch (LibavException e) {
                    Logger.getLogger(TranscodeSample.class.getName()).log(Level.SEVERE, "cannot close that", e);
                }
            }
        };
    }


}
