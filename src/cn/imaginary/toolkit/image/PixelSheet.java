package cn.imaginary.toolkit.image;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.*;

public class PixelSheet {
    public static boolean Is_Pack_Vertical = true;

    public static String tag_bounds_x = "x";
    public static String tag_bounds_y = "y";
    public static String tag_bounds_width = "width";
    public static String tag_bounds_height = "height";
    public static String tag_bounds_left = "left";
    public static String tag_bounds_right = "right";
    public static String tag_bounds_top = "top";
    public static String tag_bounds_bottom = "bottom";
    public static String tag_shape = "shape";

    public static int Area_Style_RGB = 0;
    public static int Area_Style_Subtract = 1;
    public static int Area_Style_Shape = 2;
    public static int Direction_Up = -1;
    public static int Direction_Left = -1;
    public static int Direction_Right = 1;
    public static int Direction_Down = 1;
    public static int Pack_Horizontal = -1;
    public static int Pack_Vertical = 1;

    private boolean is_pack_vertical = Is_Pack_Vertical;

    private final int[] styles = {Area_Style_RGB, Area_Style_Subtract, Area_Style_Shape};

    private int area_style = 1;
    private int direction_x = Direction_Right;
    private int direction_y = Direction_Down;

    private RGBTool RGBTool = new RGBTool();

    private Properties properties_pack;
    private Properties properties_unpack;

    public void setCheckStyle(int areaStyle) {
        area_style = areaStyle;
    }

    public int getCheckStyle() {
        if (area_style >= 0 && area_style < styles.length) {
            return styles[area_style];
        }
        return Area_Style_Subtract;
    }

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
                Object value = properties.get(key);
                if (key instanceof Integer) {
                    if (value instanceof Properties) {
                        updatePackProperties((Properties) value, x, y);
                    }
                } else {
                    if (key.equals(tag_bounds_x)) {
                        properties.put(key, (int) value + x);
                    } else if (key.equals(tag_bounds_y)) {
                        properties.put(key, (int) value + y);
                    }
                }
            }
        }
    }

    private int[] getBounds(BufferedImage image) {
        if (null != image) {
            int x_min = -1;
            int x_max = -1;
            int y_min = -1;
            int y_max = -1;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (RGBTool.isRGB(image, x, y)) {
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

    private Dimension getSize(BufferedImage[] array) {
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

    private void updateImage(BufferedImage[] array, boolean isTrim) {
        if (isTrim) {
            if (null != array) {
                for (int i = 0; i < array.length; i++) {
                    BufferedImage image = getSubImage(array[i], true);
                    if (null != image) {
                        array[i] = image;
                    }
                }
            }
        }
    }

    private BufferedImage getSubImage(BufferedImage image, boolean isTrim) {
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

    public void drawImage(BufferedImage root, BufferedImage image, int x, int y) {
        if (null != root && null != image) {
            Graphics2D graphics2D = root.createGraphics();
            drawImage(graphics2D, image, x, y, root.getWidth(), root.getHeight());
            graphics2D.dispose();
        }
    }

    private void drawImage(Graphics2D graphics2D, BufferedImage image, int x, int y, int width, int height) {
        if (null != graphics2D && null != image) {
            if (width > 0) {
                x += (width - image.getWidth()) / 2;
            }
            if (height > 0) {
                y += (height - image.getHeight()) / 2;
            }
            graphics2D.drawImage(image, x, y, null);
        }
    }

    private void drawImage(Graphics2D graphics2D, BufferedImage image, int x, int y, int width, int height, Properties properties) {
        drawImage(graphics2D, image, x, y, width, height);
        updatePackProperties(properties, image, x, y, false);
    }

    private void updatePackProperties(Properties properties, BufferedImage image, int x, int y, boolean isAlpha) {
        if (null != properties && null != image) {
            int[] bounds = getBounds(image);
            if (null != bounds) {
                int left = bounds[0];
                int right = bounds[0] + bounds[2];
                int top = bounds[1];
                int bottom = bounds[1] + bounds[3];
                properties.put(tag_bounds_left, left);
                properties.put(tag_bounds_right, right);
                properties.put(tag_bounds_top, top);
                properties.put(tag_bounds_bottom, bottom);
                if (isAlpha) {
                    if (x >= left && y >= top) {
                        x -= left;
                        y -= top;
                    }
                }
            }
            properties.put(tag_bounds_x, x);
            properties.put(tag_bounds_y, y);
            properties.put(tag_bounds_width, image.getWidth());
            properties.put(tag_bounds_height, image.getHeight());
            updatePackProperties(properties, image, getCheckStyle());
        }
    }

    private void updatePackProperties(Properties properties, BufferedImage image, int areaStyle) {
    }

    public BufferedImage pack(BufferedImage[] array, Properties properties, boolean isTrim) {
        if (null != array) {
            updateImage(array, isTrim);
            Dimension dimension = getSize(array);
            if (null != dimension) {
                BufferedImage root = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics2D = root.createGraphics();
                if (null != properties) {
                    Set<Object> kset = properties.keySet();
                    int[] bounds = getBounds(array);
                    int w_min = bounds[0];
                    int h_min = bounds[2];
                    for (Iterator<Object> iterator = kset.iterator(); iterator.hasNext(); ) {
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
                                        Rectangle rectangle = getBounds(prop);
                                        if (null != rectangle) {
                                            int w = rectangle.width;
                                            int h = rectangle.height;
                                            if (w_min < w) {
                                                w_min = w;
                                            }
                                            if (h_min < h) {
                                                h_min = h;
                                            }
                                            drawImage(graphics2D, image, rectangle.x, rectangle.y, w, h, null);
                                        }
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

    public void setPackDirection(int directionX, int directionY) {
        direction_x = directionX;
        direction_y = directionY;
    }

    public BufferedImage packPolygon(BufferedImage[] array, boolean isTrim) {
        return packPolygonTool(array, isTrim, false, direction_x, direction_y);
    }

    public BufferedImage packPolygon(BufferedImage[] array, boolean isTrim, int directionX, int directionY) {
        return packPolygonTool(array, isTrim, false, directionX, directionY);
    }

    public BufferedImage packPolygonNon(BufferedImage[] array, boolean isTrim) {
        return packPolygonTool(array, isTrim, true, direction_x, direction_y);
    }

    public BufferedImage packPolygonNon(BufferedImage[] array, boolean isTrim, int directionX, int directionY) {
        return packPolygonTool(array, isTrim, true, directionX, directionY);
    }

    private BufferedImage packPolygonTool(BufferedImage[] array, boolean isTrim, boolean isAlpha, int directionX, int directionY) {
        if (null != array) {
            updateImage(array, isTrim);
            Dimension dimension = getSize(array);
            BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
            packPolygonTool(image, array, isAlpha, directionX, directionY);
            return image;
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

    private void packPolygonTool(BufferedImage root, BufferedImage[] array, boolean isAlpha, int directionX, int directionY) {
        if (null != root && null != array) {
            boolean[][] records = new boolean[root.getWidth()][root.getHeight()];
            Properties properties = new Properties();
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = array[i];
                if (null != image) {
                    Properties prop = new Properties();
                    packPolygonTool(root, image, records, prop, isAlpha, directionX, directionY);
                    properties.put(i, prop);
                }
            }
            setPackProperties(properties);
        }
    }

    public void setPackVertical(boolean isPackVertical) {
        is_pack_vertical = isPackVertical;
    }

    public boolean isPackVertical() {
        return is_pack_vertical;
    }

    private void packPolygonTool(BufferedImage root, BufferedImage image, boolean[][] records, Properties properties, boolean isAlpha, int directionX, int directionY) {
        if (null != root && null != image && null != records) {
            int width = root.getWidth();
            int height = root.getHeight();
            if (isPackVertical()) {
                if (directionX > 0 && directionY > 0) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            if (!records[x][y]) {
                                if (check(root, image, records, x, y)) {
                                    update(root, image, records, x, y, properties, isAlpha);
                                    return;
                                }
                            }
                        }
                    }
                } else if (directionX > 0 && directionY < 0) {
                    for (int x = 0; x < width; x++) {
                        for (int y = height - 1; y >= 0; y--) {
                            if (!records[x][y]) {
                                if (check(root, image, records, x, y)) {
                                    update(root, image, records, x, y, properties, isAlpha);
                                    return;
                                }
                            }
                        }
                    }
                } else if (directionX < 0 && directionY > 0) {
                    for (int x = width - 1; x >= 0; x--) {
                        for (int y = 0; y < height; y++) {
                            if (!records[x][y]) {
                                if (check(root, image, records, x, y)) {
                                    update(root, image, records, x, y, properties, isAlpha);
                                    return;
                                }
                            }
                        }
                    }
                } else if (directionX < 0 && directionY < 0) {
                    for (int x = width - 1; x >= 0; x--) {
                        for (int y = height - 1; y >= 0; y--) {
                            if (!records[x][y]) {
                                if (check(root, image, records, x, y)) {
                                    update(root, image, records, x, y, properties, isAlpha);
                                    return;
                                }
                            }
                        }
                    }
                }
            } else {
                if (directionX > 0 && directionY > 0) {
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            if (!records[x][y]) {
                                if (check(root, image, records, x, y)) {
                                    update(root, image, records, x, y, properties, isAlpha);
                                    return;
                                }
                            }
                        }
                    }
                } else if (directionX > 0 && directionY < 0) {
                    for (int y = height - 1; y >= 0; y--) {
                        for (int x = 0; x < width; x++) {
                            if (!records[x][y]) {
                                if (check(root, image, records, x, y)) {
                                    update(root, image, records, x, y, properties, isAlpha);
                                    return;
                                }
                            }
                        }
                    }
                } else if (directionX < 0 && directionY > 0) {
                    for (int y = 0; y < height; y++) {
                        for (int x = width - 1; x >= 0; x--) {
                            if (!records[x][y]) {
                                if (check(root, image, records, x, y)) {
                                    update(root, image, records, x, y, properties, isAlpha);
                                    return;
                                }
                            }
                        }
                    }
                } else if (directionX < 0 && directionY < 0) {
                    for (int y = height - 1; y >= 0; y--) {
                        for (int x = width - 1; x >= 0; x--) {
                            if (!records[x][y]) {
                                if (check(root, image, records, x, y)) {
                                    update(root, image, records, x, y, properties, isAlpha);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean check(BufferedImage root, BufferedImage image, boolean[][] records, int offsetX, int offsetY) {
        if (null != root && null != image && null != records) {
            int width = offsetX + image.getWidth();
            int height = offsetY + image.getHeight();
            if (width <= root.getWidth() && height <= root.getHeight()) {
                for (int x = offsetX; x < width; x++) {
                    for (int y = offsetY; y < height; y++) {
                        if (records[x][y]) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void update(BufferedImage root, BufferedImage image, boolean[][] records, int x, int y, Properties properties, boolean isAlpha) {
        update(root, image, records, x, y, isAlpha);
        drawImage(root, image, x, y, false);
        updatePackProperties(properties, image, x, y, false);
    }

    private void update(BufferedImage root, BufferedImage image, boolean[][] records, int offsetX, int offsetY, boolean isAlpha) {
        if (null != root && null != image && null != records) {
            int width = offsetX + image.getWidth();
            int height = offsetY + image.getHeight();
            if (width <= root.getWidth() && height <= root.getHeight()) {
                for (int x = offsetX; x < width; x++) {
                    for (int y = offsetY; y < height; y++) {
                        if (!records[x][y]) {
                            if (isAlpha) {
                                if (RGBTool.isRGB(image, x - offsetX, y - offsetY)) {
                                    records[x][y] = true;
                                }
                            } else {
                                records[x][y] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private void drawImage(BufferedImage root, BufferedImage image, int x, int y, boolean isAlpha) {
        if (null != root && null != image) {
            if (isAlpha) {
                int[] bounds = getBounds(image);
                if (null != bounds) {
                    int left = bounds[0];
                    int top = bounds[1];
                    if (x >= left && y >= top) {
                        x -= left;
                        y -= top;
                    }
                }
            }
            Graphics2D graphics2D = root.createGraphics();
            graphics2D.drawImage(image, x, y, null);
            graphics2D.dispose();
        }
    }

    public ArrayList<BufferedImage> unpackList(BufferedImage root, Properties properties, boolean isTrim, boolean isAlpha) {
        if (null != root && null != properties) {
            ArrayList<BufferedImage> arrayList = new ArrayList<>();
            for (int i = 0; i < properties.size(); i++) {
                BufferedImage image = unpack(root, properties, i, isTrim, isAlpha);
                if (null != image) {
                    arrayList.add(image);
                }
            }
            return arrayList;
        }
        return null;
    }

    private BufferedImage unpack(BufferedImage root, Properties properties, int index, boolean isTrim, boolean isAlpha) {
        if (null != root && null != properties) {
            Rectangle r = getBounds(properties, index);
            if (null != r) {
                if (isAlpha) {
                    return unpackNon(root, properties, index, isTrim);
                } else {
                    return unpack(root, properties, index, isTrim);
                }
            }
        }
        return null;
    }

    private BufferedImage unpack(BufferedImage root, Properties properties, int index, boolean isTrim) {
        if (null != root && null != properties) {
            Rectangle rectangle = getBounds(properties, index);
            if (null != rectangle) {
                return unpack(root, rectangle.x, rectangle.y, rectangle.width, rectangle.height, isTrim);
            }
        }
        return null;
    }

    private BufferedImage unpackNon(BufferedImage root, Properties properties, int index, boolean isTrim) {
        if (null != root && null != properties) {
            BufferedImage image;
            if (getCheckStyle() == Area_Style_Shape) {
                image = drawImage(root, properties, index);
            } else {
                image = subtractImage(root, properties, index);
            }
            if (null != image && isTrim) {
                Rectangle rect = getTrimBounds(properties, index);
                image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            }
            return image;
        }
        return null;
    }

    private BufferedImage drawImage(BufferedImage root, Properties properties, int index) {
        return null;
    }

    private BufferedImage subtractImage(BufferedImage root, Properties properties, int index) {
        if (null != root && null != properties) {
            ArrayList<Rectangle> arrayList = new ArrayList<>();
            for (int i = 0; i < properties.size(); i++) {
                Rectangle rectangle = getBounds(properties, i);
                if (null != rectangle) {
                    arrayList.add(rectangle);
                }
            }
            Rectangle[] array = new Rectangle[arrayList.size()];
            arrayList.toArray(array);
            return subtractImage(root, array, index);
        }
        return null;
    }

    private BufferedImage subtractImage(BufferedImage root, Rectangle[] array, int index) {
        if (null != root && null != array) {
            Rectangle rectangle = array[index];
            Area area = new Area(rectangle);
            for (int i = index + 1; i < array.length; i++) {
                Rectangle rect = array[i];
                Area a = new Area(rect);
                area.subtract(a);
            }
            if (getCheckStyle() == Area_Style_RGB) {
                return drawRGB(root, area, rectangle);
            } else {
                return drawImage(root, area, rectangle);
            }
        }
        return null;
    }

    private BufferedImage drawImage(BufferedImage root, Area area, Rectangle rectangle) {
        if (null != root && null != area && null != rectangle) {
            BufferedImage image = new BufferedImage(root.getWidth(), root.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setClip(area);
            graphics2D.drawImage(root, 0, 0, null);
            graphics2D.dispose();
            image = unpack(image, rectangle.x, rectangle.y, rectangle.width, rectangle.height, false);
            return image;
        }
        return null;
    }

    private BufferedImage drawRGB(BufferedImage root, Area area, Rectangle rectangle) {
        if (null != root && null != area && null != rectangle) {
            int width = root.getWidth();
            int height = root.getHeight();
            int x = rectangle.x;
            int y = rectangle.y;
            int w = rectangle.width;
            int h = rectangle.height;
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int x_ = i + x;
                    int y_ = j + y;
                    if (area.contains(x_, y_)) {
                        if (x < width && y_ < height) {
                            image.setRGB(i, j, root.getRGB(x_, y_));
                        }
                    }
                }
            }
            return image;
        }
        return null;
    }

    public Rectangle getBounds(Properties properties, int index) {
        if (null != properties) {
            Object object = properties.get(index);
            if (null == object) {
                object = properties.get(String.valueOf(index));
            }
            if (object instanceof Properties) {
                return getBounds((Properties) object);
            }
        }
        return null;
    }

    public Rectangle getBounds(Properties properties) {
        if (null != properties) {
            Object x = properties.get(tag_bounds_x);
            Object y = properties.get(tag_bounds_y);
            Object width = properties.get(tag_bounds_width);
            Object height = properties.get(tag_bounds_height);
            if (null != x && null != y && null != width && null != height) {
                return new Rectangle((int) x, (int) y, (int) width, (int) height);
            }
        }
        return null;
    }

    public Rectangle getTrimBounds(Properties properties, int index) {
        if (null != properties) {
            Object object = properties.get(index);
            if (null == object) {
                object = properties.get(String.valueOf(index));
            }
            if (object instanceof Properties) {
                return getTrimBounds((Properties) object);
            }
        }
        return null;
    }

    public Rectangle getTrimBounds(Properties properties) {
        if (null != properties) {
            Object left = properties.get(tag_bounds_left);
            Object right = properties.get(tag_bounds_right);
            Object top = properties.get(tag_bounds_top);
            Object bottom = properties.get(tag_bounds_bottom);
            if (null != left && null != right && null != top && null != bottom) {
                return new Rectangle((int) left, (int) top, (int) right - (int) left, (int) bottom - (int) top);
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
                        updatePackProperties(prop, image, x_, y_, false);
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
}
