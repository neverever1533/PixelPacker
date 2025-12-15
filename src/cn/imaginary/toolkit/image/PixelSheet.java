package cn.imaginary.toolkit.image;

//import cn.imaginary.toolkit.image.sheet.PixelScanner;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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

    private void updatePackProperties(Properties properties, int x, int y) {
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

    public int[] getBounds(BufferedImage image) {
        if (null != image) {
            int x_min = -1;
            int x_max = -1;
            int y_min = -1;
            int y_max = -1;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (isRGB(image, x, y)) {
                        if (x_min == -1) {
                            x_min = x;
                        }
                        if (x_max == -1) {
                            x_max = x;
                        }
                        if (y_min == -1) {
                            y_min = y;
                        }
                        if (y_max == -1) {
                            y_max = y;
                        }
                        if (x < x_min) {
                            x_min = x;
                        }
                        if (x > x_max) {
                            x_max = x;
                        }
                        if (y < y_min) {
                            y_min = y;
                        }
                        if (y > y_max) {
                            y_max = y;
                        }
                    }
                }
            }
            int[] arr = new int[4];
            arr[0] = x_min;
            arr[1] = y_min;
            arr[2] = x_max - x_min + 1;
            arr[3] = y_max - y_min + 1;
            return arr;
        }
        return null;
    }

    public int[] getBounds(BufferedImage[] array) {
        if (null != array) {
            int width_min = -1;
            int width_max = -1;
            int height_min = -1;
            int height_max = -1;
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = array[i];
                if (null != image) {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    if (width_min == -1) {
                        width_min = width;
                    }
                    if (width_max == -1) {
                        width_max = width;
                    }
                    if (height_min == -1) {
                        height_min = height;
                    }
                    if (height_max == -1) {
                        height_max = height;
                    }
                    if (width < width_min) {
                        width_min = width;
                    }
                    if (width > width_max) {
                        width_max = width;
                    }
                    if (height < height_min) {
                        height_min = height;
                    }
                    if (height > height_max) {
                        height_max = height;
                    }
                }
            }
            int[] arr = new int[4];
            arr[0] = width_min;
            arr[1] = width_max;
            arr[2] = height_min;
            arr[3] = height_max;
            return arr;
        }
        return null;
    }

    public Dimension getSize(BufferedImage[] array) {
        if (null != array) {
            int[] bounds = getBounds(array);
            if (null != bounds) {
                int w_max = bounds[1];
                int h_max = bounds[3];
                double size_sqrt = Math.sqrt(array.length);
                int size = (int) size_sqrt;
                if (size != size_sqrt) {
                    size += 1;
                }
                int width = w_max * size;
                int height = h_max * size;
                return new Dimension(width, height);
            }
        }
        return null;
    }

    private Dimension getSize(BufferedImage[] array, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != array) {
            int[] bounds = getBounds(array);
            int width_max = bounds[1];
            int height_max = bounds[3];
            if (width <= 0) {
                width = width_max;
            }
            if (height <= 0) {
                height = height_max;
            }
            int width_ = (width + lineWidth) * lineSize;
            int height_ = (height + rowHeight) * rowSize;
            return new Dimension(width_, height_);
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

    public void updateImage(BufferedImage[] array, boolean isTrim) {
        if (isTrim) {
            if (null != array) {
                for (int i = 0; i < array.length; i++) {
                    BufferedImage image = getSubImage(array[i], isTrim);
                    if (null != image) {
                        array[i] = image;
                    }
                }
            }
        }
    }

    public BufferedImage getSubImage(BufferedImage image, boolean isTrim) {
        if (isTrim) {
            int[] bounds = getBounds(image);
            if (null != bounds) {
                int x_trim = bounds[0];
                int y_trim = bounds[1];
                int w_trim = bounds[2];
                int h_trim = bounds[3];
                if (x_trim > 0 && y_trim > 0 && w_trim > 0 && h_trim > 0) {
                    image = image.getSubimage(x_trim, y_trim, w_trim, h_trim);
                }
            }
        }
        return image;
    }

    public BufferedImage pack(BufferedImage[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != array) {
            updateImage(array, isTrim);
            if (lineSize <= 0) {
                if (rowSize <= 0) {
                    double size_sqrt = Math.sqrt(array.length);
                    int size = (int) size_sqrt;
                    if (size != size_sqrt) {
                        size += 1;
                    }
                    lineSize = size;
                    rowSize = size;
                } else {
                    lineSize = array.length / rowSize;
                }
            } else {
                if (rowSize <= 0) {
                    rowSize = array.length / lineSize;
                }
            }
            Dimension bounds = getSize(array, width, height, lineSize, rowSize, lineWidth, rowHeight);
            BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            pack(image, array, width, height, lineSize, rowSize, lineWidth, rowHeight);
            if (x != 0 || y != 0) {
                updatePackProperties(getPackProperties(), x, y);
                return drawImage(image, x, y);
            } else {
                return image;
            }
        }
        return null;
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

    private void pack(BufferedImage root, BufferedImage[] array, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != root && null != array) {
            int[] bounds = getBounds(array);
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
            Properties properties = new Properties();
            Graphics2D graphics2D = root.createGraphics();
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = array[i];
                if (null != image) {
                    int w;
                    int h;
                    if (width == 0) {
                        w = image.getWidth();
                    } else {
                        w = width;
                    }
                    if (height == 0) {
                        h = image.getHeight();
                    } else {
                        h = height;
                    }
                    int line = i % lineSize;
                    int row = i / lineSize;
                    if (row >= rowSize) {
                        break;
                    }
                    int x = line * (w_max + lineWidth);
                    int y = row * (h_max + rowHeight);
                    Properties prop = new Properties();
                    drawImage(graphics2D, image, x, y, w, h, prop);
                    properties.put(i, prop);
                }
            }
            graphics2D.dispose();
            setPackProperties(properties);
        }
    }

    private void drawImage(Graphics2D graphics2D, BufferedImage image, int x, int y, int width, int height, Properties properties) {
        if (null != graphics2D && null != image) {
            if (width > 0) {
                x += (width - image.getWidth()) / 2;
            }
            if (height > 0) {
                y += (height - image.getHeight()) / 2;
            }
            graphics2D.drawImage(image, x, y, null);
            if (null != properties) {
                properties.put(tag_x, x);
                properties.put(tag_y, y);
                properties.put(tag_width, image.getWidth());
                properties.put(tag_height, image.getHeight());
            }
        }
    }

    public BufferedImage pack(BufferedImage[] array, Properties properties, boolean isTrim) {
        if (null != array && null != properties) {
            updateImage(array, isTrim);
            Dimension dimension = getSize(array);
            if (null != dimension) {
                BufferedImage root = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics2D = root.createGraphics();
                Object key_ = properties.get("x");
                if (null == key_) {
                    Set<Object> kset = properties.keySet();
                    int[] bounds = getBounds(array);
                    int w_min = bounds[0];
                    int h_min = bounds[2];
                    for (Iterator iterator = kset.iterator(); iterator.hasNext(); ) {
                        Object key = iterator.next();
                        Object value = properties.get(key);
                        if (value instanceof Properties) {
                            Properties prop = (Properties) value;
                            int index = -1;
                            if (key instanceof Integer) {
                                index = (int) key;
                            } else if (key instanceof String) {
                                index = Integer.parseInt((String) key);
                            }
                            if (index != -1) {
                                if (index < array.length) {
                                    BufferedImage image = array[index];
                                    if (null != image) {
                                        int x = (int) prop.get(tag_x);
                                        int y = (int) prop.get(tag_y);
                                        int width = (int) prop.get(tag_width);
                                        int height = (int) prop.get(tag_height);
                                        if (w_min < width) {
                                            w_min = width;
                                        }
                                        if (h_min < height) {
                                            h_min = height;
                                        }
                                        drawImage(graphics2D, image, x, y, width, height, null);
                                    }
                                }
                            }
                        }
                    }
                    graphics2D.dispose();
                    return getSubImage(root, w_min, h_min);
                }
            }
        }
        return null;
    }

    public BufferedImage packPolygon(BufferedImage[] array, boolean isTrim) {
        if (null != array) {
            updateImage(array, isTrim);
            Dimension dimension = getSize(array);
            BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
            packPolygon(image, array);
            return getSubImage(image, array);
        }
        return null;
    }

    private BufferedImage getSubImage(BufferedImage root, BufferedImage[] array) {
        if (null != array) {
            int[] bounds = getBounds(array);
            return getSubImage(root, bounds[0], bounds[2]);
        }
        return null;
    }

    private BufferedImage getSubImage(BufferedImage root, int width, int height) {
        if (null != root) {
            if (width < 0 || width >= root.getWidth() || height < 0 || height >= root.getHeight() || (width == 0 && height == 0)) {
                return root;
            }
            int[] bounds = getBounds(root);
            if (null != bounds) {
                int x_max = bounds[2] + bounds[0];
                int y_max = bounds[3] + bounds[1];
                int aw = root.getWidth() - x_max;
                int ah = root.getHeight() - y_max;
                if (aw != 0 || ah != 0) {
                    int x = x_max;
                    x += aw % width;
                    int y = y_max;
                    y += ah % height;
                    root = root.getSubimage(0, 0, x, y);
                }
            }
            return root;
        }
        return null;
    }

    private void packPolygon(BufferedImage root, BufferedImage[] array) {
        if (null != root && null != array) {
            boolean[][] records = new boolean[root.getWidth()][root.getHeight()];
            Properties properties = new Properties();
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = array[i];
                if (null != image) {
                    Properties prop = new Properties();
                    packPolygon(root, image, records, prop);
                    properties.put(i, prop);
                }
            }
            setPackProperties(properties);
        }
    }

    private void packPolygon(BufferedImage root, BufferedImage image, boolean[][] records, Properties properties) {
        if (null != root && null != image && null != records) {
            int width = root.getWidth();
            int height = root.getHeight();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (!records[x][y]) {
                        int width_ = width - x - 1;
                        int height_ = height - y - 1;
                        int w = image.getWidth();
                        int h = image.getHeight();
                        if (width_ >= w && height_ >= h && !records[x + w][y] && !records[x][y + h] && !records[x + w][y + h]) {
                            update(root, image, records, x, y);
                            drawImage(root, image, x, y);
                            if (null != properties) {
                                properties.put(tag_x, x);
                                properties.put(tag_y, y);
                                properties.put(tag_width, w);
                                properties.put(tag_height, h);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    private void update(BufferedImage root, BufferedImage image, boolean[][] records, int offsetX, int offsetY) {
        if (null != root && null != image && null != records) {
            int width = offsetX + image.getWidth();
            int height = offsetY + image.getHeight();
            if (width <= root.getWidth() && height <= root.getHeight()) {
                for (int x = offsetX; x < width; x++) {
                    for (int y = offsetY; y < height; y++) {
                        if (!records[x][y]) {
                            records[x][y] = true;
//                            if (isRGB(image, x - offsetX, y - offsetY)) {
//                                records[x][y] = true;
//                            }
                        }
                    }
                }
            }
        }
    }

    private void drawImage(BufferedImage root, BufferedImage image, int x, int y) {
        if (null != root && null != image) {
            Graphics2D graphics2D = root.createGraphics();
            graphics2D.drawImage(image, x, y, null);
            graphics2D.dispose();
        }
    }

    public BufferedImage unpack(BufferedImage image, Properties properties, boolean isTrim) {
        if (null != image && null != properties) {
            Object key_ = properties.get(tag_x);
            if (null != key_) {
                int x = (int) properties.get(tag_x);
                int y = (int) properties.get(tag_y);
                int width = (int) properties.get(tag_width);
                int height = (int) properties.get(tag_height);
                return unpack(image, x, y, width, height, isTrim);
            }
        }
        return null;
    }

    public BufferedImage unpack(BufferedImage root, int x, int y, int width, int height, boolean isTrim) {
        if (null != root) {
            int width_ = root.getWidth();
            int height_ = root.getHeight();
            if (width <= 0) {
                width = width_;
            }
            if (height <= 0) {
                height = height_;
            }
            BufferedImage image;
            if (width < width_ && height < height_) {
                image = root.getSubimage(x, y, width, height);
            } else {
                image = root;
            }
            return getSubImage(image, isTrim);
        }
        return null;
    }

    public ArrayList<BufferedImage> unpack(BufferedImage root, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != root) {
            int w = root.getWidth();
            int h = root.getHeight();
            if (x > 0 || y > 0) {
                w -= x;
                h -= y;
                root = root.getSubimage(x, y, w, h);
            }
            int w_;
            int h_;
            if (lineSize <= 0) {
                if (width <= 0) {
                    w_ = w;
                    lineSize = 1;
                } else {
                    w_ = width + lineWidth;
                    if (w_ < 0 || w_ > w) {
                        lineWidth = 0;
                        w_ = width;
                    }
                    lineSize = w / w_;
                }
            } else {
                if (width <= 0) {
                    w_ = w / lineSize;
                } else {
                    w_ = width + lineWidth;
                    if (w_ < 0 || w_ > w) {
                        lineWidth = 0;
                        w_ = width;
                    }
                }
            }
            if (width <= 0) {
                width = w_ - lineWidth;
            }
            if (rowSize <= 0) {
                if (height <= 0) {
                    h_ = h;
                    rowSize = 1;
                } else {
                    h_ = height + rowHeight;
                    if (h_ < 0 || h_ > h) {
                        rowHeight = 0;
                        h_ = height;
                    }
                    rowSize = h / h_;
                }
            } else {
                if (height <= 0) {
                    h_ = h / rowSize;
                } else {
                    h_ = height + rowHeight;
                    if (h_ < 0 || h_ > h) {
                        rowHeight = 0;
                        h_ = height;
                    }
                }
            }
            if (height <= 0) {
                height = h_ - rowHeight;
            }
            if (lineSize == 1 && rowSize == 1 || w_ == w && h_ == h || ((width + lineWidth >= w) && (height + rowHeight >= h))) {
                return null;
            }
            ArrayList<BufferedImage> arrayList = new ArrayList<>();
            Properties properties = new Properties();
            int index = 0;
            for (int i = 0; i < lineSize; i++) {
                for (int j = 0; j < rowSize; j++) {
                    int x_ = i * (width + lineWidth);
                    int y_ = j * (height + rowHeight);
                    BufferedImage image = root.getSubimage(x_, y_, w_, h_);
                    if (lineWidth > 0 || rowHeight > 0) {
                        image = image.getSubimage(x_, y_, width, height);
                    }
                    if (isTrim) {
                        if (null != image) {
                            int[] bounds = getBounds(image);
                            if (null != bounds) {
                                int x_trim = bounds[0];
                                int y_trim = bounds[1];
                                int w_trim = bounds[2];
                                int h_trim = bounds[3];
                                if (x_trim > 0 && y_trim > 0 && w_trim > 0 && h_trim > 0) {
                                    image = image.getSubimage(x_trim, y_trim, w_trim, h_trim);
                                    x_ += x_trim;
                                    y_ += y_trim;
                                }
                            }
                        }
                    }
                    if (null != image) {
                        arrayList.add(image);
                        Properties prop = new Properties();
                        prop.put(tag_x, x_);
                        prop.put(tag_y, y_);
                        prop.put(tag_width, image.getWidth());
                        prop.put(tag_height, image.getHeight());
                        properties.put(index, prop);
                        index++;
                    }
                }
            }
            setUnpackProperties(properties);
            return arrayList;
        }
        return null;
    }

    public ArrayList<BufferedImage> unpackPolygon(BufferedImage image, int width, int height, boolean isTrim) {
        return null;
    }
}
