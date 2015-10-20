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

import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.bridge.avcodec.AVPacket;

/**
 * Packet consumer interface.
 *
 * @author Ondrej Perutka
 */
public interface IPacketConsumer {

    /**
     * Send a packet to the consumer. The consumer should free the packet after
     * use. The producer should not send one packet to multiple consumers.
     *
     * @param producer packet producer
     * @param packet a packet
     * @throws LibavException if an error occurs
     */
    void processPacket(Object producer, AVPacket packet) throws LibavException;

}
