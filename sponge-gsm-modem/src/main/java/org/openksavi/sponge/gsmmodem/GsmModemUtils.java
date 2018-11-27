/*
 * Copyright 2016-2018 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.gsmmodem;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Hex;

import threegpp.charset.gsm.GSMCharset;

/**
 * A set of GSM modem utility methods.
 */
public abstract class GsmModemUtils {

    public static final Charset CHARSET_GSM = new GSMCharset();

    public static final Charset CHARSET_UTF_16BE = Charset.forName("UTF-16BE");

    private GsmModemUtils() {
        //
    }

    public static boolean canEncodeGsm(String text) {
        return text != null ? CHARSET_GSM.newEncoder().canEncode(text) : true;
    }

    public static String encodeUcs2(String text) {
        return text != null ? Hex.encodeHexString(text.getBytes(CHARSET_UTF_16BE), false) : null;
    }
}
