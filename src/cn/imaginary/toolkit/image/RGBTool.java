package cn.imaginary.toolkit.image;

import java.awt.image.BufferedImage;

public class RGBTool {
    public boolean isAlpha(BufferedImage image, int x, int y) {
        return !isRGB(image, x, y);
    }

    public boolean isAlpha(int[][] array, int x, int y) {
        return !isRGB(array, x, y);
    }

    public boolean isRGB(BufferedImage image, int x, int y) {
        if (null != image) {
            if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
                int rgb = image.getRGB(x, y);
                return isRGB(rgb);
            }
        }
        return false;
    }

    public boolean isRGB(int[][] array, int x, int y) {
        if (null != array) {
            int rgb = array[x][y];
            return isRGB(rgb);
        }
        return false;
    }

    public boolean isRGB(int rgb) {
        return !isAlpha(rgb);
    }

    public boolean isAlpha(int rgb) {
        return ((rgb >> 24) & 0xff) == 0;
    }
}
