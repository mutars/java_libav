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

import com.mutar.libav.bridge.avutil.AVRational;



public class AVRationalUtils {

    /**
     * Multiple this rational number with the given one and return the
     * normalized result.
     *
     * @param r a rational number
     * @return normalized result
     */
    public static AVRational mul(AVRational r1, AVRational r2) {
        long n = r1.num() * r2.num();
        long d = r1.den() * r2.den();
        long gcd = gcd(n, d);
        return createNew(n / gcd, d / gcd);
    }

    /**
     * Divide this rational number by the given one and return the normalized
     * result.
     *
     * @param r a rational number
     * @return normalized result
     */
    public static AVRational div(AVRational r1, AVRational r2) {
        long n = r1.num() * r2.den();
        long d = r1.den() * r2.num();
        long gcd = gcd(n, d);
        return createNew(n / gcd, d / gcd);
    }

    /**
     * Multiple this rational number with the given number and return the
     * normalized result.
     *
     * @param num a number
     * @return normalized result
     */
    public static AVRational mul(AVRational r1, long num) {
        long n = r1.num() * num;
        long gcd = gcd(n, r1.den());
        return createNew(n / gcd, r1.den() / gcd);
    }

    /**
     * Divide this rational number by the given number and return the normalized
     * result.
     *
     * @param num a number
     * @return normalized result
     */
    public static AVRational div(AVRational r1, long num) {
        long d = r1.den() * num;
        long gcd = gcd(r1.num(), d);
        return createNew(r1.num() / gcd, d / gcd);
    }

    /**
     * Exchange the numerator and the denominator and return the result.
     *
     * @return inverted number
     */
    public static AVRational invert(AVRational r1) {
        return createNew(r1.den(), r1.num());
    }

    /**
     * Normalize this rational number and return the result. It will divide
     * the numerator and the denominator by their greatest common divider.
     *
     * @return normalized rational
     */
    public static AVRational normalize(AVRational r1) {
        long gcd = gcd(r1.num(), r1.den());
        return createNew(r1.num() / gcd, r1.den() / gcd);
    }

    public static AVRational createNew(long num, long den) {
        AVRational avr = new AVRational();
        avr.num((int)num);
        avr.den((int)den);
        return avr;
    }

    private static long gcd(long a, long b) {
        if (a < 0)
            a = -a;
        if (b < 0)
            b = -b;

        long tmp;
        while (b > 0) {
            tmp = a % b;
            a = b;
            b = tmp;
        }

        return a;
    }

}
