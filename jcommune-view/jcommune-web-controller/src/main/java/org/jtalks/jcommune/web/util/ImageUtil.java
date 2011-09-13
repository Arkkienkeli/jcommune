/**
 * Copyright (C) 2011  jtalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Class that consists util methods for working with images.
 *
 * @author Eugeny Batov
 */
public final class ImageUtil {

    /**
     * Empty constructor.
     */
    private ImageUtil() {
        //Utility classes should not have a public or default constructor
    }

    public static final int IMAGE_UNKNOWN = -1;
    public static final int IMAGE_JPEG = 0;
    public static final int IMAGE_PNG = 1;
    public static final int IMAGE_GIF = 2;

    /**
     * Converts multipart file to image.
     *
     * @param multipartFile input multipart file
     * @return image obtained from multipart file
     * @throws IOException throws if an error occurs during reading
     */
    public static Image convertMultipartFileToImage(MultipartFile multipartFile) throws IOException {
        return ImageIO.read(multipartFile.getInputStream());
    }

    /**
     * Converts image to byte array.
     *
     * @param image input image
     * @return byte array obtained from image
     * @throws IOException if an I/O error occurs
     */
    public static byte[] convertImageToByteArray(Image image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, "jpeg", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }


    /**
     * Resizes an image.
     *
     * @param image     The image to resize
     * @param maxWidth  The image's max width
     * @param maxHeight The image's max height
     * @param type      int code jpeg, png or gif
     * @return A resized <code>BufferedImage</code>
     */
    public static BufferedImage resizeImage(BufferedImage image, int type, int maxWidth, int maxHeight) {
        Dimension largestDimension = new Dimension(maxWidth, maxHeight);

        // Original size
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        float aspectRatio = (float) imageWidth / imageHeight;

        if (imageWidth > maxWidth || imageHeight > maxHeight) {
            if ((float) largestDimension.width / largestDimension.height > aspectRatio) {
                largestDimension.width = (int) Math.ceil(largestDimension.height * aspectRatio);
            } else {
                largestDimension.height = (int) Math.ceil(largestDimension.width / aspectRatio);
            }

            //Modified size
            imageWidth = largestDimension.width;
            imageHeight = largestDimension.height;
        }
        return createBufferedImage(image, type, imageWidth, imageHeight);
    }

    /**
     * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can
     * function on a completely headless system. This especially includes Linux and Unix systems
     * that do not have the X11 libraries installed, which are required for the AWT subsystem to
     * operate. The resulting image will be smoothly scaled using bilinear filtering.
     *
     * @param source The image to convert
     * @param width  The desired image width
     * @param height The desired image height
     * @param type   int code jpeg, png or gif
     * @return bufferedImage The resized image
     */
    private static BufferedImage createBufferedImage(BufferedImage source, int type, int width, int height) {
        if (type == ImageUtil.IMAGE_PNG && hasAlpha(source)) {
            type = BufferedImage.TYPE_INT_ARGB;
        } else {
            type = BufferedImage.TYPE_INT_RGB;
        }

        BufferedImage bufferedImage = new BufferedImage(width, height, type);

        int sourceX;
        int sourceY;

        double scaleX = (double) width / source.getWidth();
        double scaleY = (double) height / source.getHeight();

        int x1;
        int y1;

        double xDiff;
        double yDiff;

        int rgb;
        int rgb1;
        int rgb2;

        for (int y = 0; y < height; y++) {
            sourceY = y * source.getHeight() / bufferedImage.getHeight();
            yDiff = scale(y, scaleY) - sourceY;

            for (int x = 0; x < width; x++) {
                sourceX = x * source.getWidth() / bufferedImage.getWidth();
                xDiff = scale(x, scaleX) - sourceX;

                x1 = Math.min(source.getWidth() - 1, sourceX + 1);
                y1 = Math.min(source.getHeight() - 1, sourceY + 1);

                rgb1 = getRGBInterpolation(source.getRGB(sourceX, sourceY), source.getRGB(x1, sourceY), xDiff);
                rgb2 = getRGBInterpolation(source.getRGB(sourceX, y1), source.getRGB(x1, y1), xDiff);

                rgb = getRGBInterpolation(rgb1, rgb2, yDiff);

                bufferedImage.setRGB(x, y, rgb);
            }
        }

        return bufferedImage;
    }

    /**
     * Scales point.
     *
     * @param point point
     * @param scale scale
     * @return scaled point
     */
    private static double scale(int point, double scale) {
        return point / scale;
    }

    /**
     * Makes rgb interpolation.
     *
     * @param value1   value1
     * @param value2   value2
     * @param distance distance
     * @return rgb rgb
     */
    private static int getRGBInterpolation(int value1, int value2, double distance) {
        int alpha1 = (value1 & 0xFF000000) >>> 24;
        int red1 = (value1 & 0x00FF0000) >> 16;
        int green1 = (value1 & 0x0000FF00) >> 8;
        int blue1 = (value1 & 0x000000FF);

        int alpha2 = (value2 & 0xFF000000) >>> 24;
        int red2 = (value2 & 0x00FF0000) >> 16;
        int green2 = (value2 & 0x0000FF00) >> 8;
        int blue2 = (value2 & 0x000000FF);

        int rgb = ((int) (alpha1 * (1.0 - distance) + alpha2 * distance) << 24)
                | ((int) (red1 * (1.0 - distance) + red2 * distance) << 16)
                | ((int) (green1 * (1.0 - distance) + green2 * distance) << 8)
                | (int) (blue1 * (1.0 - distance) + blue2 * distance);

        return rgb;
    }

    /**
     * Determines if the image has transparent pixels.
     *
     * @param image The image to check for transparent pixel.s
     * @return <code>true</code> of <code>false</code>, according to the result
     */
    private static boolean hasAlpha(Image image) {
        try {
            PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
            pg.grabPixels();

            return pg.getColorModel().hasAlpha();
        } catch (InterruptedException e) {
            return false;
        }
    }
}