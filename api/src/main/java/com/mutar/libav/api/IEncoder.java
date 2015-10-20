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
package com.mutar.libav.api;

import com.mutar.libav.api.data.IFrameConsumer;
import com.mutar.libav.api.data.IPacketProducer;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avformat.AVStream;

/**
 * Encoder interface.
 *
 * @author Ondrej Perutka
 */
public interface IEncoder extends IFrameConsumer, IPacketProducer {

    /**
     * Get timestamp generator.
     *
     * @return timestamp generator
     */
    ITimestampGenerator getTimestampGenerator();

    /**
     * Set timestamp generator.
     *
     * @param timestampGenerator a timestamp generator
     */
    void setTimestampGenerator(ITimestampGenerator timestampGenerator);

    /**
     * Get encoding codec context.
     *
     * You may use the codec context to affect the quality of the stream
     * (bitrate, ...)
     *
     * @return encoding codec context
     */
    AVCodecContext getCodecContext();

    /**
     * Get underlaying stream.
     *
     * @return underlaying stream
     */
    AVStream getStream();

    /**
     * Close the encoder.
     */
    void close();

    /**
     * Check whether the encoder has been closed.
     *
     * @return true if the encoder has been closed, false otherwise
     */
    boolean isClosed();

    /**
     * Flush the encoder. (Call this at the end of encoding process.)
     *
     * @throws LibavException if an error occurs
     */
    void flush() throws LibavException;

}
