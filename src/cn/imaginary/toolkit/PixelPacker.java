package cn.imaginary.toolkit;

import cn.imaginary.toolkit.image.PixelSheet;
import cn.imaginary.toolkit.json.JsonArray;
import cn.imaginary.toolkit.json.JsonObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class PixelPacker {
    public static int MaxSize = -1;
    public static int KeepSize = 0;
    public static int Direction_Up = PixelSheet.Direction_Up;
    public static int Direction_Left = PixelSheet.Direction_Left;
    public static int Direction_Right = PixelSheet.Direction_Right;
    public static int Direction_Down = PixelSheet.Direction_Down;

    private int type_Prefix = -1;
    private int type_Suffix = 1;

    private String lineSeparator = System.lineSeparator();
    private String sign_comment = "sprite pack";
    private String sign_encoding = "utf-8";
    private String sign_png = "png";

    public String suffix_Json = ".json";
    public String suffix_Png = ".png";
    public String suffix_Pack = "_pack_";
    public String suffix_Unpack = "_unpack_";
    public String suffix_Polygon = "polygon_";
    public String suffix_Polygon_Non = "polygon_non_";
    public String suffix_Properties = "properties_";
    public String suffix_Shape = "shape_";
    public String suffix_Xml = ".xml";

    public String tag_bounds_x = PixelSheet.tag_bounds_x;
    public String tag_bounds_y = PixelSheet.tag_bounds_y;
    public String tag_bounds_width = PixelSheet.tag_bounds_width;
    public String tag_bounds_height = PixelSheet.tag_bounds_height;
    public String tag_bounds_left = PixelSheet.tag_bounds_left;
    public String tag_bounds_right = PixelSheet.tag_bounds_right;
    public String tag_bounds_top = PixelSheet.tag_bounds_top;
    public String tag_bounds_bottom = PixelSheet.tag_bounds_bottom;
    public String tag_bounds = "bounds";
    public String tag_trims = "trims";
    public String tag_name = "name";
    public String tag_shape = "shape";
    public String tag_file = "file";

    private PixelSheet pixelSheet = new PixelSheet();

    public String readString(File file) {
        if (null != file) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String string;
                StringBuilder stringBuilder = new StringBuilder();
                while ((string = reader.readLine()) != null) {
                    stringBuilder.append(string);
                    stringBuilder.append(lineSeparator);
                }
                reader.close();
                return stringBuilder.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public Properties readProperties(File file) {
        return readJson(file);
//        return readXML(file);
    }

    private Properties readJson(File file) {
        String string = readString(file);
        JsonUtils jsonUtils = new JsonUtils();
        JsonObject jsonObject = jsonUtils.parseJsonObject(string);
        Properties properties = toProperties(jsonObject);
        properties = format(properties);
//        System.out.println("read properties:" + properties);
        return properties;
    }

    private Properties readXML(File file) {
        Properties properties = new Properties();
        try {
            properties.loadFromXML(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    private Properties toProperties(JsonObject jsonObject) {
        if (null != jsonObject) {
            Properties properties = new Properties();
            Properties prop = jsonObject.get();
            Set<Object> kset = prop.keySet();
            for (Iterator<Object> iterator = kset.iterator(); iterator.hasNext(); ) {
                Object key = iterator.next();
                Object value = prop.get(key);
                if (value instanceof JsonObject) {
                    value = toProperties((JsonObject) value);
                } else if (value instanceof JsonArray) {
                    value = ((JsonArray) value).toArray();
                }
                properties.put(key, value);
            }
            return properties;
        }
        return null;
    }

    public void writeString(String string, File file) {
        if (null != string) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(string);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void writeProperties(Properties properties, File file) {
        properties = formatInfo(properties);
//        System.out.println("write properties: " + properties);
        writeJson(properties, file);
//        writeXML(properties, file);
    }

    private Properties format(Properties properties) {
        if (null != properties) {
            Properties prop = new Properties();
            for (int i = 0; i < properties.size(); i++) {
                Object object = properties.get(i);
                if (null == object) {
                    object = properties.get(String.valueOf(i));
                }
                if (object instanceof Properties) {
                    Properties p_info = (Properties) object;
                    Properties p = new Properties();
                    Object obj = p_info.get(tag_bounds);
                    if (obj instanceof Properties) {
                        Properties p_bounds = (Properties) obj;
                        Rectangle bounds = pixelSheet.getBounds(p_bounds);
                        if (null != bounds) {
                            p.put(tag_bounds_x, bounds.x);
                            p.put(tag_bounds_y, bounds.y);
                            p.put(tag_bounds_width, bounds.width);
                            p.put(tag_bounds_height, bounds.height);
                        }
                    } else {
                        return properties;
                    }
                    obj = p_info.get(tag_trims);
                    if (obj instanceof Properties) {
                        Properties p_trims = (Properties) obj;
                        Rectangle trims = pixelSheet.getTrimBounds(p_trims);
                        if (null != trims) {
                            p.put(tag_bounds_left, trims.x);
                            p.put(tag_bounds_right, trims.x + trims.width);
                            p.put(tag_bounds_top, trims.y);
                            p.put(tag_bounds_bottom, trims.y + trims.height);
                        }
                    }
                    obj = p_info.get(tag_name);
                    if (null != obj) {
                        p.put(tag_name, obj);
                    }
                    obj = p_info.get(tag_shape);
                    if (null != obj) {
                        p.put(tag_shape, obj);
                    }
                    if (!p.isEmpty()) {
                        prop.put(i, p);
                    }
                }
            }
            if (!prop.isEmpty()) {
                return prop;
            }
        }
        return null;
    }

    private Properties formatInfo(Properties properties) {
        if (null != properties) {
            Properties prop = new Properties();
            for (int i = 0; i < properties.size(); i++) {
                Object object = properties.get(i);
                if (null == object) {
                    object = properties.get(String.valueOf(i));
                }
                if (object instanceof Properties) {
                    Properties p = (Properties) object;
                    Properties p_info = new Properties();
                    Rectangle bounds = pixelSheet.getBounds(p);
                    if (null != bounds) {
                        Properties p_bounds = new Properties();
                        p_bounds.put(tag_bounds_x, bounds.x);
                        p_bounds.put(tag_bounds_y, bounds.y);
                        p_bounds.put(tag_bounds_width, bounds.width);
                        p_bounds.put(tag_bounds_height, bounds.height);
                        p_info.put(tag_bounds, p_bounds);
                    }
                    Rectangle trims = pixelSheet.getTrimBounds(p);
                    if (null != trims) {
                        Properties p_trims = new Properties();
                        p_trims.put(tag_bounds_left, trims.x);
                        p_trims.put(tag_bounds_right, trims.x + trims.width);
                        p_trims.put(tag_bounds_top, trims.y);
                        p_trims.put(tag_bounds_bottom, trims.y + trims.height);
                        p_info.put(tag_trims, p_trims);
                    }
                    Object obj = p.get(tag_name);
                    if (null != obj) {
                        p_info.put(tag_name, obj);
                    }
                    obj = p.get(tag_shape);
                    if (null != obj) {
                        p_info.put(tag_shape, obj);
                    }
                    if (!p_info.isEmpty()) {
                        prop.put(i, p_info);
                    }
                }
            }
            if (!prop.isEmpty()) {
                return prop;
            }
        }
        return null;
    }

    private void writeJson(Properties properties, File file) {
        if (null != properties) {
            JsonObject jsonObject = toJsonObject(properties);
            String info = jsonObject.toString();
            writeString(info, file);
        }
    }

    private void writeXML(Properties properties, File file) {
        if (null != properties && null != file) {
            try {
                properties.storeToXML(Files.newOutputStream(file.toPath()), sign_comment, sign_encoding);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private JsonObject toJsonObject(Properties properties) {
        JsonObject jsonObject = null;
        if (null != properties) {
            jsonObject = new JsonObject();
            Set<Object> kset = properties.keySet();
            for (Iterator<Object> iterator = kset.iterator(); iterator.hasNext(); ) {
                Object key = iterator.next();
                Object value = properties.get(key);
                if (value instanceof Properties) {
                    jsonObject.add(key.toString(), toJsonObject((Properties) value));
                } else {
                    jsonObject.add(key.toString(), value);
                }
            }
        }
        return jsonObject;
    }

    public BufferedImage read(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(BufferedImage image, File file) {
        if (null != image && null != file) {
            try {
                ImageIO.write(image, sign_png, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private File newFile(File file, Object name, String suffix, boolean isSuffix) {
        if (null != file && null != name && null != suffix) {
            File dirFile = file.getParentFile();
            if (null != dirFile) {
                String fileName = file.getName();
                if (isSuffix) {
                    fileName = getPrefix(fileName) + name + suffix;
                } else {
                    fileName = getPrefix(name.toString()) + suffix;
                }
                return new File(dirFile, fileName);
            }
        }
        return null;
    }

    private String getPrefix(String string) {
        return newString(string, type_Prefix);
    }

    private String getSuffix(String string) {
        return newString(string, type_Suffix);
    }

    private String newString(String string, int suffixType) {
        if (null != string) {
            String prefix;
            String suffix;
            int index = string.lastIndexOf(".");
            if (index != -1) {
                prefix = string.substring(0, index);
                suffix = string.substring(index);
            } else {
                prefix = string;
                suffix = "";
            }
            if (suffixType == type_Prefix) {
                return prefix;
            } else if (suffixType == type_Suffix) {
                return suffix;
            }
        }
        return string;
    }

    public void setPackDirection(int directionX, int directionY) {
        pixelSheet.setPackDirection(directionX, directionY);
    }

    public void setPackVertical(boolean isPackVertical) {
        pixelSheet.setPackVertical(isPackVertical);
    }

    public void setPackLevel(int packLevel) {
        pixelSheet.setPackLevel(packLevel);
    }

    public void setPackSize(int width, int height) {
        pixelSheet.setPackSize(width, height);
    }

    public void pack(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != file) {
            File dirFile;
            if (file.isFile()) {
                dirFile = file.getParentFile();
            } else {
                dirFile = file;
            }
            if (null != dirFile) {
                pack(dirFile.listFiles(), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
            }
        }
    }

    public void pack(File[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != array) {
            BufferedImage image = packTool(array, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
            String info = suffix_Pack + x + y + width + height + lineSize + rowSize + lineWidth + rowHeight + isTrim;
            Properties properties = pixelSheet.getPackProperties();
            updateProperties(properties, array);
            File file = array[0];
            if (null != file) {
                pack(file.getParentFile().getAbsolutePath(), image, properties, info);
            }
        }
    }

    private void updateProperties(Properties properties, File[] array) {
        if (null != properties && null != array) {
            for (int i = 0; i < array.length; i++) {
                Object key = i;
                Object value = properties.get(key);
                if (null != value) {
                    if (value instanceof Properties) {
                        Properties prop = (Properties) value;
                        prop.put(tag_name, getPrefix(array[i].getName()));
                        properties.put(key, value);
                    }
                }
            }
        }
    }

    private void pack(String filePath, BufferedImage image, Properties properties, String info) {
        if (null != filePath && null != image && null != info) {
            String imagePath = filePath + info + suffix_Png;
            write(image, new File(imagePath));
            String propPath = filePath + info + suffix_Json;
            if (null != properties) {
                writeProperties(properties, new File(propPath));
            }
        }
    }

    public void pack(File imageFile, File[] array, File propFile) {
        pack(imageFile, array, readProperties(propFile));
    }

    public void pack(File imageFile, File[] array, Properties properties) {
        array = sort(array, properties);
//        System.out.println("pp426:" + array.length);
        BufferedImage image = pack(imageFile, toList(array), properties);
        String info = suffix_Pack + suffix_Properties;
        if (null != imageFile) {
            pack(getPrefix(imageFile.getAbsolutePath()), image, null, info);
        }
    }

    private BufferedImage pack(File rootImageFile, ArrayList<BufferedImage> arrayList, Properties properties) {
        return pack(rootImageFile, toArray(arrayList), properties);
    }

    private BufferedImage pack(File rootImageFile, BufferedImage[] array, Properties properties) {
        return pixelSheet.pack(read(rootImageFile), array, properties);
    }

    public void pack(File rootImageFile, File imageFile, File propFile) {
        pack(rootImageFile, imageFile, readProperties(propFile));
    }

    public void pack(File rootImageFile, File imageFile, Properties properties) {
        if (null != properties) {
//            System.out.println("pp450:" + properties);
            if (null != imageFile) {
                Object object = properties.get(tag_name);
                if (null == object) {
                    for (int i = 0; i < properties.size(); i++) {
                        object = properties.get(i);
                        if (null == object) {
                            object = properties.get(String.valueOf(i));
                        }
                        if (object instanceof Properties) {
                            Properties prop = (Properties) object;
                            object = prop.get(tag_name);
                            if (null != object) {
                                String name = object.toString();
                                if (imageFile.getName().equals(name) || imageFile.getName().startsWith(name)) {
                                    pack(rootImageFile, imageFile, prop, name);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    pack(rootImageFile, imageFile, properties, object.toString());
                }
            }
        }
    }

    private void pack(File rootImageFile, File imageFile, Properties properties, String name) {
        if (null != imageFile && null != properties && null != name) {
            if (imageFile.getName().equals(name) || imageFile.getName().startsWith(name)) {
                int x = -1;
                int y = -1;
                Object obj = properties.get(tag_bounds_x);
                if (null != obj) {
                    x = (int) obj;
                }
                obj = properties.get(tag_bounds_y);
                if (null != obj) {
                    y = (int) obj;
                }
                pack(rootImageFile, imageFile, x, y);
            }
        }
    }

    public void pack(File rootImageFile, File imageFile, int x, int y) {
        BufferedImage image = pixelSheet.pack(read(rootImageFile), read(imageFile), x, y);
//        System.out.println("pp502: x:" + x + "/y:" + y + "/img:" + (null == image));
        String info = suffix_Pack + x + y;
        pack(getPrefix(rootImageFile.getAbsolutePath()), image, null, info);
    }

    public void pack(File imageFile, File propFile, boolean isTrim) {
        pack(imageFile, readProperties(propFile), isTrim);
    }

    public void pack(File imageFile, Properties properties, boolean isTrim) {
        if (null != imageFile) {

            File dirFile;
            if (imageFile.isFile()) {
                dirFile = imageFile.getParentFile();
            } else {
                dirFile = imageFile;
            }
            if (null != dirFile) {
                pack(dirFile.listFiles(), properties, isTrim);
            }
        }
    }

    public void pack(File[] array, Properties properties, boolean isTrim) {
        if (null != array) {
            String info = suffix_Pack + suffix_Properties + isTrim;
            array = sort(array, properties);
            BufferedImage image = packTool(array, properties, isTrim);
            File file = array[0];
            if (null != file) {
                pack(file.getParentFile().getAbsolutePath(), image, null, info);
            }
        } else {
            packMaxSize(array, isTrim);
        }
    }

    private File[] sort(File[] array, Properties properties) {
        if (null != array && null != properties) {
            File[] arr = new File[properties.size()];
            for (int i = 0; i < properties.size(); i++) {
                Object object = properties.get(i);
                if (null == object) {
                    object = properties.get(String.valueOf(i));
                }
                if (object instanceof Properties) {
                    Properties prop = (Properties) object;
                    Object obj = prop.get(tag_name);
                    if (null != obj) {
                        String name = obj.toString();
                        for (int j = 0; j < array.length; j++) {
                            File file = array[j];
                            if (null != file) {
                                String prefix = getPrefix(file.getName());
                                if (prefix.equals(name)) {
                                    arr[i] = file;
                                }
                            }
                        }
                    }
                }
            }
            return arr;
        }
        return array;
    }

    private BufferedImage packTool(File[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return packTool(toList(array), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
    }

    private BufferedImage packTool(File[] array, Properties properties, boolean isTrim) {
        return packTool(toList(array), properties, isTrim);
    }

    private BufferedImage packTool(ArrayList<BufferedImage> arrayList, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return packTool(toArray(arrayList), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
    }

    private BufferedImage packTool(ArrayList<BufferedImage> arrayList, Properties properties, boolean isTrim) {
        return packTool(toArray(arrayList), properties, isTrim);
    }

    private BufferedImage packTool(BufferedImage[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return pixelSheet.pack(array, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
    }

    private BufferedImage packTool(BufferedImage[] array, Properties properties, boolean isTrim) {
        return pixelSheet.pack(array, properties, isTrim);
    }

    public void packKeepSize(File file, boolean isTrim) {
        packKeepSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packKeepSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, KeepSize, KeepSize, MaxSize, MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packKeepSize(File[] array, boolean isTrim) {
        packKeepSize(array, 0, 0, 0, 0, isTrim);
    }

    public void packKeepSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(array, x, y, KeepSize, KeepSize, MaxSize, MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packMaxSize(File file, boolean isTrim) {
        packMaxSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packMaxSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, MaxSize, MaxSize, MaxSize, MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packMaxSize(File[] array, boolean isTrim) {
        packMaxSize(array, 0, 0, 0, 0, isTrim);
    }

    public void packMaxSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(array, x, y, MaxSize, MaxSize, MaxSize, MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packLineKeepSize(File file, boolean isTrim) {
        packLineKeepSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packLineKeepSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, KeepSize, KeepSize, MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public void packLineKeepSize(File[] array, boolean isTrim) {
        packLineKeepSize(array, 0, 0, 0, 0, isTrim);
    }

    public void packLineKeepSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(array, x, y, KeepSize, KeepSize, MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public void packLineMaxSize(File file, boolean isTrim) {
        packLineMaxSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packLineMaxSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, MaxSize, MaxSize, MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public void packLineMaxSize(File[] array, boolean isTrim) {
        packLineMaxSize(array, 0, 0, 0, 0, isTrim);
    }

    public void packLineMaxSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(array, x, y, MaxSize, MaxSize, MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public void packRowKeepSize(File file, boolean isTrim) {
        packRowKeepSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packRowKeepSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, KeepSize, KeepSize, 1, MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packRowKeepSize(File[] array, boolean isTrim) {
        packRowKeepSize(array, 0, 0, 0, 0, isTrim);
    }

    public void packRowKeepSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(array, x, y, KeepSize, KeepSize, 1, MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packRowMaxSize(File file, boolean isTrim) {
        packRowMaxSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packRowMaxSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, MaxSize, MaxSize, 1, MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packRowMaxSize(File[] array, boolean isTrim) {
        packRowMaxSize(array, 0, 0, 0, 0, isTrim);
    }

    public void packRowMaxSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(array, x, y, MaxSize, MaxSize, 1, MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packPolygon(File file, boolean isTrim) {
        packPolygon(file, isTrim, false);
    }

    public void packPolygon(File[] array, boolean isTrim) {
        packPolygon(array, isTrim, false);
    }

    private BufferedImage packPolygon(BufferedImage[] array, boolean isTrim) {
        return pixelSheet.packPolygon(array, isTrim);
    }

    public void packPolygonNon(File file, boolean isTrim) {
        packPolygon(file, isTrim, true);
    }

    public void packPolygonNon(File[] array, boolean isTrim) {
        packPolygon(array, isTrim, true);
    }

    private void packPolygon(File file, boolean isTrim, boolean isAlpha) {
        if (null != file) {
            File dirFile;
            if (file.isFile()) {
                dirFile = file.getParentFile();
            } else {
                dirFile = file;
            }
            if (null != dirFile) {
                File[] array = dirFile.listFiles();
                packPolygon(array, isTrim, isAlpha);
            }
        }
    }

    private void packPolygon(File[] array, boolean isTrim, boolean isAlpha) {
        if (null != array) {
            BufferedImage image = packPolygonTool(array, isTrim, isAlpha);
            String info = suffix_Pack;
            if (isAlpha) {
                info += suffix_Polygon_Non + isTrim;
            } else {
                info += suffix_Polygon + isTrim;
            }
            Properties properties = pixelSheet.getPackProperties();
            updateProperties(properties, array);
            File file = array[0];
            if (null != file) {
                pack(file.getParentFile().getAbsolutePath(), image, properties, info);
            }
        }
    }

    private BufferedImage packPolygonTool(File[] array, boolean isTrim, boolean isAlpha) {
        return packPolygonTool(toList(array), isTrim, isAlpha);
    }

    private BufferedImage packPolygonTool(ArrayList<BufferedImage> arrayList, boolean isTrim, boolean isAlpha) {
        if (isAlpha) {
            return packPolygonNon(toArray(arrayList), isTrim);
        } else {
            return packPolygon(toArray(arrayList), isTrim);
        }
    }

    private BufferedImage packPolygonNon(BufferedImage[] array, boolean isTrim) {
        return pixelSheet.packPolygonNon(array, isTrim);
    }

    public void packShape(File file, boolean isTrim) {
        pixelSheet.setCheckStyle(PixelSheet.Area_Style_Shape);
        packPolygon(file, isTrim, true);
    }

    public void packShape(File[] array, boolean isTrim) {
        pixelSheet.setCheckStyle(PixelSheet.Area_Style_Shape);
        packPolygon(array, isTrim, true);
    }

    public void unpack(File file, int x, int y, int width, int height, boolean isTrim) {
        if (null != file) {
            BufferedImage image = read(file);
            image = unpack(image, x, y, width, height, isTrim);
            String name = suffix_Unpack + x + y + width + height + isTrim;
            write(image, newFile(file, name, suffix_Png, true));
        }
    }

    public void unpack(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != file) {
            ArrayList<BufferedImage> arrayList = unpack(read(file), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
            if (null != arrayList) {
                String info = suffix_Unpack + x + y + width + height + lineSize + rowSize + lineWidth + rowHeight + isTrim;
                unpack(file, arrayList, pixelSheet.getUnpackProperties(), info);
            }
        }
    }

    public void unpack(File file, ArrayList<BufferedImage> arrayList, Properties properties, String info) {
        if (null != file && null != arrayList && null != info) {
            int index = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                BufferedImage image = arrayList.get(i);
                Object name = null;
                if (null != properties) {
                    Object object = properties.get(i);
                    if (null == object) {
                        object = properties.get(String.valueOf(i));
                    }
                    if (object instanceof Properties) {
                        Properties prop = (Properties) object;
                        name = prop.get(tag_name);
                    }
                }
                File imageFile;
                if (null == name) {
                    name = info + "_" + index;
                    imageFile = newFile(file, name, suffix_Png, true);
                } else {
                    imageFile = new File(file.getParentFile(), name.toString() + suffix_Png);
                }
                write(image, imageFile);
                index++;
            }
        }
    }

    public void unpack(File imageFile, File propFile, boolean isTrim) {
        unpack(imageFile, readProperties(propFile), isTrim);
    }

    public void unpack(File imageFile, Properties properties, boolean isTrim) {
        unpack(imageFile, properties, isTrim, false);
    }

    private void unpack(File imageFile, Properties properties, boolean isTrim, boolean isAlpha) {
        if (null != properties) {
            if (null == imageFile) {
                return;
            }
            String info = suffix_Unpack + suffix_Properties + isTrim;
            ArrayList<BufferedImage> arrayList = unpackList(read(imageFile), properties, isTrim, isAlpha);
//            unpack(imageFile, arrayList, null, info);
            unpack(imageFile, arrayList, properties, info);
        }
    }

    private BufferedImage unpack(BufferedImage root, int x, int y, int width, int height, boolean isTrim) {
        return pixelSheet.unpack(root, x, y, width, height, isTrim);
    }

    private ArrayList<BufferedImage> unpack(BufferedImage root, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return pixelSheet.unpack(root, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
    }

    private ArrayList<BufferedImage> unpackList(BufferedImage root, Properties properties, boolean isTrim, boolean isAlpha) {
        return pixelSheet.unpackList(root, properties, isTrim, isAlpha);
    }

    public void unpackNon(File imageFile, File propFile, boolean isTrim) {
        unpackNon(imageFile, readProperties(propFile), isTrim);
    }

    public void unpackNon(File imageFile, Properties properties, boolean isTrim) {
        unpack(imageFile, properties, isTrim, true);
    }

    public void unpackShape(File imageFile, File propFile, boolean isTrim) {
        unpackShape(imageFile, readProperties(propFile), isTrim);
    }

    public void unpackShape(File imageFile, Properties properties, boolean isTrim) {
        pixelSheet.setCheckStyle(PixelSheet.Area_Style_Shape);
//        unpack(imageFile, properties, isTrim, true);
    }

    public BufferedImage[] toArray(ArrayList<BufferedImage> arrayList) {
        if (null != arrayList) {
            BufferedImage[] array = new BufferedImage[arrayList.size()];
            arrayList.toArray(array);
            return array;
        }
        return null;
    }

    public ArrayList<BufferedImage> toList(File[] array) {
        if (null != array) {
            ArrayList<BufferedImage> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = read(array[i]);
                if (null != image) {
                    arrayList.add(image);
                }
            }
            return arrayList;
        }
        return null;
    }
}
