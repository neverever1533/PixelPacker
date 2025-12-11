package cn.imaginary.toolkit.image;

//import cn.imaginary.toolkit.image.sheet.PixelScanner;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

//import javaev.awt.PolygonUtils;

public class PixelSheet {

    private String tag_x = "x";
    private String tag_y = "y";
    private String tag_width = "width";
    private String tag_height = "height";

    private Properties properties_pack;
    private Properties properties_unpack;

    public Properties getPackProperties() {
        return properties_pack;
    }

    public void setPackProperties(Properties properties) {
        properties_pack = properties;
    }

    public Properties getUnpackProperties() {
        return properties_unpack;
    }

    public void setUnpackProperties(Properties properties) {
        properties_unpack = properties;
    }

    public void updatePackProperties(Properties properties, int x, int y) {
        if (null == properties) {
            properties = getPackProperties();
        }
        if (null != properties) {
            Set<Object> kset = properties.keySet();
            for (Iterator<Object> iterator = kset.iterator(); iterator.hasNext(); ) {
                Object key = iterator.next();
                if (key instanceof Integer) {
                    Object value = properties.get(key);
                    if (value instanceof Properties) {
                        updatePackProperties((Properties) value, x, y);
                    }
                } else {
                    if (key.equals(tag_x)) {
                        properties.put(key, (int) properties.get(key) + x);
                    } else if (key.equals(tag_y)) {
                        properties.put(key, (int) properties.get(key) + y);
                    }
                }
            }
        }
    }

    private void check(BufferedImage image, boolean[][] records, int offsetX, int offsetY) {
        if (null != image && null != records) {
            int width = image.getWidth();
            int height = image.getHeight();
            if (width < records.length && height < records[width].length) {
                for (int x = offsetX; x < offsetX + width; x++) {
                    for (int y = offsetY; y < offsetY + height; y++) {
                        if (!records[x][y]) {
                            records[x][y] = true;
                        }
                    }
                }
            }
        }
    }

    public BufferedImage drawImage(BufferedImage[] array) {
        if (null != array) {
            int[] bounds = getBounds(array);
//            System.out.println("bounds:" + Arrays.toString(bounds));
            int w_min = bounds[0];
            int h_min = bounds[2];
            int w_max = bounds[1];
            int h_max = bounds[3];
            int w;
            int h;
            int size = array.length;
            w = w_max * size / 2;
            h = h_max * size / 2;
//            System.out.println("w:" + w + "/h:" + h);
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            drawImage(image, array);
            int[] rgb_bounds = getBounds(image);
            if (null != rgb_bounds) {
//                System.out.println("rgb bounds:" + Arrays.toString(rgb_bounds));
                int aw = image.getWidth() - rgb_bounds[0];
                int ah = image.getHeight() - rgb_bounds[1];
                if (aw != 0 || ah != 0) {
                    int x = rgb_bounds[0];
                    x += aw % w_min;
                    int y = rgb_bounds[1];
                    y += ah % h_min;
                    image = image.getSubimage(0, 0, x, y);
                }
            }
            return image;
        }
        return null;
    }

    public BufferedImage drawImage(BufferedImage[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != array) {
            if (lineSize <= 0) {
                lineSize = array.length;
            }
            if (rowSize <= 0) {
                rowSize = array.length / lineSize;
            }
            Dimension bounds = getBounds(array, width, height, lineSize, rowSize, lineWidth, rowHeight);
//            System.out.println("pack bounds:" + bounds.toString());
            BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            drawImage(image, array, width, height, lineSize, rowSize, lineWidth, rowHeight);
            if (x != 0 || y != 0) {
                updatePackProperties(getPackProperties(), x, y);
                return drawImage(image, x, y);
            } else {
                return image;
            }
        }
        return null;
    }

    public void drawImage(BufferedImage image, BufferedImage[] array) {
        if (null != image && null != array) {
            boolean[][] records = new boolean[image.getWidth()][image.getHeight()];
            Properties properties = new Properties();
            for (int i = 0; i < array.length; i++) {
                BufferedImage img = array[i];
                if (null != img) {
//                    drawImage(image, array[i], records, properties, i);
                    Properties prop = new Properties();
                    drawImage(image, array[i], records, prop);
                    properties.put(i, prop);
                }
            }
            setPackProperties(properties);
        }
    }

    public void drawImage(BufferedImage root, BufferedImage image, boolean[][] records, Properties properties) {
        if (null != root && null != image && null != records) {
            int w = root.getWidth();
            int h = root.getHeight();
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    if (!records[x][y]) {
                        if (null != properties) {
                            properties.put(tag_x, x);
                            properties.put(tag_y, y);
                            properties.put(tag_width, image.getWidth());
                            properties.put(tag_height, image.getHeight());
                        }
                        check(image, records, x, y);
                        drawImage(root, image, x, y);
                        return;
                    }
                }
            }
        }
    }

    public void drawImage(BufferedImage root, BufferedImage image, int x, int y) {
        if (null != root && null != image) {
            Graphics2D graphics2D = root.createGraphics();
            graphics2D.drawImage(image, x, y, null);
            graphics2D.dispose();
        }
    }

    public void drawImage(BufferedImage image, BufferedImage[] array, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != image && null != array) {
            int[] bounds = getBounds(array);
//            System.out.println("bounds:" + Arrays.toString(bounds));
            int w_max = bounds[1];
            int h_max = bounds[3];
            if (width < 0) {
                width = w_max;
            }
            if (width > w_max) {
                w_max = width;
            }
            if (height < 0) {
                height = h_max;
            }
            if (height > h_max) {
                h_max = height;
            }
//            System.out.println("width:" + width + "/height:" + height);
            Properties properties = new Properties();
            Graphics2D graphics2D = image.createGraphics();
            for (int i = 0; i < array.length; i++) {
                BufferedImage img = array[i];
                if (null != img) {
                    int w;
                    int h;
                    if (width == 0) {
                        w = img.getWidth();
                    } else {
                        w = width;
                    }
                    if (height == 0) {
                        h = img.getHeight();
                    } else {
                        h = height;
                    }
//                    System.out.println("w:" + w + "/h:" + h);
                    int line = i % lineSize;
                    int row = i / lineSize;
                    if (row >= rowSize) {
                        break;
                    }
                    int x = line * (w_max + lineWidth);
                    int y = row * (h_max + rowHeight);
//                    System.out.println("x:" + x + "/y:" + y);
                    Properties prop = new Properties();
                    drawImage(graphics2D, img, x, y, w, h, prop);
                    properties.put(i, prop);
                }
            }
            graphics2D.dispose();
            setPackProperties(properties);
        }
    }

    private BufferedImage drawImage(BufferedImage image, int x, int y) {
        if (null != image) {
            BufferedImage root = new BufferedImage(image.getWidth() + x, image.getHeight() + y, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = root.createGraphics();
            graphics2D.drawImage(image, x, y, null);
            graphics2D.dispose();
            return root;
        }
        return null;
    }

    private void drawImage(Graphics2D graphics2D, BufferedImage image, int x, int y, int width, int height, Properties properties) {
        if (null != graphics2D && null != image) {
            if (null != properties) {
                properties.put(tag_x, x);
                properties.put(tag_y, y);
                properties.put(tag_width, width);
                properties.put(tag_height, height);
            }
            if (width > 0) {
                x += (width - image.getWidth()) / 2;
            }
            if (height > 0) {
                y += (height - image.getHeight()) / 2;
            }
            graphics2D.drawImage(image, x, y, null);
        }
    }

    public int[] getBounds(BufferedImage image) {
        if (null != image) {
            int x_max = 0;
            int y_max = 0;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (isRGB(image, x, y)) {
                        if (x > x_max) {
                            x_max = x;
                        }
                        if (y > y_max) {
                            y_max = y;
                        }
                    }
                }
            }
            int[] arr = new int[2];
            arr[0] = x_max;
            arr[1] = y_max;
            return arr;
        }
        return null;
    }

    public Dimension getBounds(BufferedImage[] array, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != array) {
            int[] bounds = getBounds(array);
            int w_max = bounds[1];
            int h_max = bounds[3];
            if (width <= 0) {
                width = w_max;
            }
            if (height <= 0) {
                height = h_max;
            }
            int w = (width + lineWidth) * lineSize;
            int h = (height + rowHeight) * rowSize;
            return new Dimension(w, h);
        }
        return null;
    }

    public int[] getBounds(BufferedImage[] array) {
        if (null != array) {
            int w_min = -1;
            int w_max = -1;
            int h_min = -1;
            int h_max = -1;
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = array[i];
                if (null != image) {
                    int w = image.getWidth();
                    int h = image.getHeight();
                    if (w_min == -1) {
                        w_min = w;
                    }
                    if (w_max == -1) {
                        w_max = w;
                    }
                    if (h_min == -1) {
                        h_min = h;
                    }
                    if (h_max == -1) {
                        h_max = h;
                    }
                    if (w < w_min) {
                        w_min = w;
                    }
                    if (w > w_max) {
                        w_max = w;
                    }
                    if (h < h_min) {
                        h_min = h;
                    }
                    if (h > h_max) {
                        h_max = h;
                    }
                }
            }
            int[] arr = new int[4];
            arr[0] = w_min;
            arr[1] = w_max;
            arr[2] = h_min;
            arr[3] = h_max;
            return arr;
        }
        return null;
    }

    public boolean isAlpha(BufferedImage image, int x, int y) {
        if (null != image) {
            return isAlpha(image.getRGB(x, y));
        }
        return false;
    }

    public boolean isAlpha(int rgb) {
        return ((rgb >> 24) & 0xff) == 0;
    }

    public boolean isRGB(BufferedImage image, int x, int y) {
        if (null != image) {
            return isRGB(image.getRGB(x, y));
        }
        return false;
    }

    public boolean isRGB(int rgb) {
        return !isAlpha(rgb);
    }

    public BufferedImage packLine(ArrayList<BufferedImage> arrayList) {
        return pack(arrayList, 0, 0, -1, -1, -1, -1, 0, 0);
    }

    public BufferedImage packRow(ArrayList<BufferedImage> arrayList) {
        return pack(arrayList, 0, 0, -1, -1, 1, -1, 0, 0);
    }

    public BufferedImage packLine(BufferedImage[] array) {
        return pack(array, 0, 0, -1, -1, -1, -1, 0, 0);
    }

    public BufferedImage packRow(BufferedImage[] array) {
        return pack(array, 0, 0, -1, -1, 1, -1, 0, 0);
    }

    public BufferedImage pack(ArrayList<BufferedImage> arrayList, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != arrayList) {
            BufferedImage[] array = new BufferedImage[arrayList.size()];
            arrayList.toArray(array);
            return pack(array, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight);
        }
        return null;
    }

    public BufferedImage pack(BufferedImage[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        return drawImage(array, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight);
    }

    public BufferedImage packPolygon(ArrayList<BufferedImage> arrayList) {
        if (null != arrayList) {
            BufferedImage[] array = new BufferedImage[arrayList.size()];
            arrayList.toArray(array);
            return packPolygon(array);
        }
        return null;
    }

    public BufferedImage packPolygon(BufferedImage[] array) {
        return drawImage(array);
    }

    public void unpack(BufferedImage image) {
    }

    public ArrayList<BufferedImage> unpack(BufferedImage image, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        return null;
    }

    private ArrayList<BufferedImage> unpack(BufferedImage image, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return null;
    }

    public ArrayList<BufferedImage> unpackList(BufferedImage image, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return null;
    }

    public ArrayList<BufferedImage> unpackList(BufferedImage image, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        return null;
    }

    public ArrayList<BufferedImage> unpackTrim(BufferedImage image, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        return null;
    }

    public ArrayList<BufferedImage> unpackPolygon(BufferedImage image) {
        return null;
    }

    private ArrayList<BufferedImage> unpackPolygon(BufferedImage image, boolean isTrim) {
        return null;
    }

    private BufferedImage unpackPolygon(BufferedImage image, ArrayList<Point> arrayList, Properties properties, boolean isTrim) {
        return null;
    }

    private BufferedImage drawImageList(BufferedImage image, int width, int height) {
        if (null != image) {
            int w = image.getWidth();
            int h = image.getHeight();
            if (width <= 0 || width > w) {
                width = w;
            }
            if (height <= 0 || height > h) {
                height = h;
            }
            if (width < w || height < h) {
                image = image.getSubimage(0, 0, width, height);
            }
            BufferedImage root = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = root.createGraphics();
            drawImage(graphics2D, image, 0, 0, width, height, null);
            graphics2D.dispose();
            return root;
        }
        return null;
    }

    public void drawImageList(ArrayList<BufferedImage> arrayList, int width, int height) {
        if (null != arrayList) {
            if (width <= 0 && height <= 0) {
                return;
            }
            for (int i = 0; i < arrayList.size(); i++) {
                BufferedImage image = drawImageList(arrayList.get(i), width, height);
                if (null != image) {
                    arrayList.set(i, image);
                }
            }
        }
    }

    private void drawImageList(BufferedImage[] array, int width, int height) {
        if (null != array) {
            if (width <= 0 && height <= 0) {
                return;
            }
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = drawImageList(array[i], width, height);
                if (null != image) {
                    array[i] = image;
                }
            }
        }
    }

    private ArrayList<BufferedImage> unpackPolygonList(BufferedImage image, ArrayList<ArrayList<Point>> arrayList, boolean isTrim) {
        return null;
    }

    public ArrayList<BufferedImage> unpackPolygonTrim(BufferedImage image) {
        return null;
    }
}
