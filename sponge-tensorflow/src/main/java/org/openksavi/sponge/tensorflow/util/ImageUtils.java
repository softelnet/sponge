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

package org.openksavi.sponge.tensorflow.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.openksavi.sponge.core.util.SpongeUtils;

/**
 * A set of image utility methods.
 */
public abstract class ImageUtils {

    public static byte[] getImageBytes(String fileName) {
        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(ImageIO.read(new File(fileName)), FilenameUtils.getExtension(fileName), out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    public static void writeImageBytes(byte[] imageBytes, String fileName) {
        try {
            FileUtils.writeByteArrayToFile(new File(fileName), imageBytes != null ? imageBytes : new byte[0]);
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    private ImageUtils() {
        //
    }
}
