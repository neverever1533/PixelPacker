package cn.imaginary.toolkit;

import cn.imaginary.toolkit.image.PixelSheet;
import cn.imaginary.toolkit.json.JsonObject;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.imageio.ImageIO;

public class PixelPacker {
    public static boolean IsTrim = true;

    public static int Line_MaxSize = -1;
    public static int Row_MaxSize = -1;
    public static int Width_KeepSize = 0;
    public static int Height_KeepSize = 0;
    public static int Width_MaxSize = -1;
    public static int Height_MaxSize = -1;

    private String lineSeparator = System.lineSeparator();
    private String suffix_Json = ".json";
    private String suffix_Png = ".png";
    private String suffix_Pack = "_pack_";
    private String suffix_Unpack = "_unpack_";
    private String suffix_Polygon = "polygon_";
    private String suffix_Properties = "properties_";
    private String suffix_Xml = ".xml";
    private String tag_comment = "sprite pack";
    private String tag_encoding = "utf-8";
    private String tag_png = "png";

    private PixelSheet pixelSheet = new PixelSheet();

    private String readString(String filePath) {
        return readString(new File(filePath));
    }

    private String readString(File file) {
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
                string = stringBuilder.toString();
                return string;
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
        return toProperties(jsonObject);
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
        Properties properties = null;
        if (null != jsonObject) {
            properties = new Properties();
            Properties prop = jsonObject.get();
            Set<Object> kset = prop.keySet();
            for (Iterator<Object> iterator = kset.iterator(); iterator.hasNext(); ) {
                Object key = iterator.next();
                Object value = prop.get(key);
                if (value instanceof JsonObject) {
                    properties.put(key.toString(), toProperties((JsonObject) value));
                } else if (value instanceof Properties) {
                    properties.put(key.toString(), value);
                } else {
                    properties.put(key.toString(), value.toString());
                }
            }
        }
        return properties;
    }

    private void writeString(String string, File file) {
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
//        System.out.println("properties: " + properties);
        writeJson(properties, file);
//        writeXML(properties, file);
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
                properties.storeToXML(Files.newOutputStream(file.toPath()), tag_comment, tag_encoding);
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

    private BufferedImage read(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void read(String filePath) {
        if (null != filePath) {
            read(new File(filePath));
        }
    }

    private void write(BufferedImage image, File file) {
        if (null != image && null != file) {
            try {
                ImageIO.write(image, tag_png, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void write(BufferedImage image, String filePath) {
        if (null != filePath) {
            write(image, new File(filePath));
        }
    }

    private File newFile(File file, Object name, String suffix, boolean isSuffix) {
        File newFile = null;
        if (null != file && null != name && null != suffix) {
            String fname = file.getName();
            if (isSuffix) {
                fname = name.toString();
            }
            File pfile = file.getParentFile();
            if (null != pfile) {
                int index = fname.lastIndexOf(".");
                if (index != -1) {
                    fname = fname.substring(0, index);
                }
                if (isSuffix) {
                    fname += name + suffix;
                }
                fname += suffix;
                newFile = new File(pfile, fname);
            }
        }
        return newFile;
    }

    public void pack(String filePath, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != filePath) {
            pack(new File(filePath), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
        }
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
                BufferedImage image = pack(dirFile.listFiles(), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
                String info = suffix_Pack + x + y + width + height + lineSize + rowSize + lineWidth + rowHeight + isTrim;
                pack(dirFile, image, info);
            }
        }
    }

    private void pack(File dirFile, BufferedImage image, String info) {
        if (null != dirFile && null != image && null != info) {
            String imagePath = dirFile.getAbsolutePath() + info + suffix_Png;
            File imageFile = new File(imagePath);
            write(image, imageFile);
//            String fname = dirFile.getName() + suffix_Pack_Properties + suffix_Xml;
            String fname = dirFile.getName() + info;
            File propFile = newFile(imageFile, fname, suffix_Json, false);
            writeProperties(pixelSheet.getPackProperties(), propFile);
        }
    }

    public BufferedImage pack(File[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != array) {
            ArrayList<BufferedImage> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = read(array[i]);
                if (null != image) {
                    arrayList.add(image);
                }
            }
            return pack(arrayList, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
        }
        return null;
    }

    public BufferedImage pack(ArrayList<BufferedImage> arrayList, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(toArray(arrayList), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage pack(BufferedImage[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return pixelSheet.pack(array, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
    }

    public void packKeepSize(String filePath, boolean isTrim) {
        if (null != filePath) {
            packKeepSize(new File(filePath), isTrim);
        }
    }

    public void packKeepSize(String filePath, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != filePath) {
            packKeepSize(new File(filePath), x, y, lineWidth, rowHeight, isTrim);
        }
    }

    public void packKeepSize(File file, boolean isTrim) {
        packKeepSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packKeepSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packKeepSize(File[] array, boolean isTrim) {
        return packKeepSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packKeepSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(array, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packKeepSize(ArrayList<BufferedImage> arrayList, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(arrayList, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packKeepSize(BufferedImage[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(array, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packMaxSize(String filePath, boolean isTrim) {
        if (null != filePath) {
            packMaxSize(new File(filePath), isTrim);
        }
    }

    public void packMaxSize(String filePath, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != filePath) {
            packMaxSize(new File(filePath), x, y, lineWidth, rowHeight, isTrim);
        }
    }

    public void packMaxSize(File file, boolean isTrim) {
        packMaxSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packMaxSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packMaxSize(File[] array, boolean isTrim) {
        return packMaxSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packMaxSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(array, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packMaxSize(ArrayList<BufferedImage> arrayList, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(arrayList, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packMaxSize(BufferedImage[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(array, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packLineKeepSize(String filePath, boolean isTrim) {
        if (null != filePath) {
            packLineKeepSize(new File(filePath), isTrim);
        }
    }

    public void packLineKeepSize(String filePath, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != filePath) {
            packLineKeepSize(new File(filePath), x, y, lineWidth, rowHeight, isTrim);
        }
    }

    public void packLineKeepSize(File file, boolean isTrim) {
        packLineKeepSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packLineKeepSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packLineKeepSize(File[] array, boolean isTrim) {
        return packLineKeepSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packLineKeepSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(array, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packLineKeepSize(ArrayList<BufferedImage> arrayList, boolean isTrim) {
        return packLineKeepSize(arrayList, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packLineKeepSize(ArrayList<BufferedImage> arrayList, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(arrayList, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packLineKeepSize(BufferedImage[] array, boolean isTrim) {
        return packLineKeepSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packLineKeepSize(BufferedImage[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pixelSheet.pack(array, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public void packLineMaxSize(String filePath, boolean isTrim) {
        if (null != filePath) {
            packLineMaxSize(new File(filePath), isTrim);
        }
    }

    public void packLineMaxSize(String filePath, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != filePath) {
            packLineMaxSize(new File(filePath), x, y, lineWidth, rowHeight, isTrim);
        }
    }

    public void packLineMaxSize(File file, boolean isTrim) {
        packLineMaxSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packLineMaxSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packLineMaxSize(File[] array, boolean isTrim) {
        return packLineMaxSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packLineMaxSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(array, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packLineMaxSize(ArrayList<BufferedImage> arrayList, boolean isTrim) {
        return packLineMaxSize(arrayList, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packLineMaxSize(ArrayList<BufferedImage> arrayList, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(arrayList, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packLineMaxSize(BufferedImage[] array, boolean isTrim) {
        return packLineMaxSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packLineMaxSize(BufferedImage[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pixelSheet.pack(array, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, 1, lineWidth, rowHeight, isTrim);
    }

    public void packRowKeepSize(String filePath, boolean isTrim) {
        if (null != filePath) {
            packRowKeepSize(new File(filePath), isTrim);
        }
    }

    public void packRowKeepSize(String filePath, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != filePath) {
            packRowKeepSize(new File(filePath), x, y, lineWidth, rowHeight, isTrim);
        }
    }

    public void packRowKeepSize(File file, boolean isTrim) {
        packRowKeepSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packRowKeepSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, Width_KeepSize, Height_KeepSize, 1, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packRowKeepSize(File[] array, boolean isTrim) {
        return packRowKeepSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packRowKeepSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(array, x, y, Width_KeepSize, Height_KeepSize, 1, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packRowKeepSize(ArrayList<BufferedImage> arrayList, boolean isTrim) {
        return packRowKeepSize(arrayList, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packRowKeepSize(ArrayList<BufferedImage> arrayList, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(arrayList, x, y, Width_KeepSize, Height_KeepSize, 1, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packRowKeepSize(BufferedImage[] array, boolean isTrim) {
        return packRowKeepSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packRowKeepSize(BufferedImage[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pixelSheet.pack(array, x, y, Width_KeepSize, Height_KeepSize, 1, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packRowMaxSize(String filePath, boolean isTrim) {
        if (null != filePath) {
            packRowMaxSize(new File(filePath), isTrim);
        }
    }

    public void packRowMaxSize(String filePath, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != filePath) {
            packRowMaxSize(new File(filePath), x, y, lineWidth, rowHeight, isTrim);
        }
    }

    public void packRowMaxSize(File file, boolean isTrim) {
        packRowMaxSize(file, 0, 0, 0, 0, isTrim);
    }

    public void packRowMaxSize(File file, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        pack(file, x, y, Width_MaxSize, Height_MaxSize, 1, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packRowMaxSize(File[] array, boolean isTrim) {
        return packRowMaxSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packRowMaxSize(File[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(array, x, y, Width_MaxSize, Height_MaxSize, 1, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packRowMaxSize(ArrayList<BufferedImage> arrayList, boolean isTrim) {
        return packRowMaxSize(arrayList, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packRowMaxSize(ArrayList<BufferedImage> arrayList, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(arrayList, x, y, Width_MaxSize, Height_MaxSize, 1, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage packRowMaxSize(BufferedImage[] array, boolean isTrim) {
        return packRowMaxSize(array, 0, 0, 0, 0, isTrim);
    }

    public BufferedImage packRowMaxSize(BufferedImage[] array, int x, int y, int lineWidth, int rowHeight, boolean isTrim) {
        return pack(array, x, y, Width_MaxSize, Height_MaxSize, 1, Row_MaxSize, lineWidth, rowHeight, isTrim);
    }

    public void packPolygon(String filePath, boolean isTrim) {
        if (null != filePath) {
            packPolygon(new File(filePath), isTrim);
        }
    }

    public void packPolygon(File file, boolean isTrim) {
        if (null != file) {
            File dirFile;
            if (file.isFile()) {
                dirFile = file.getParentFile();
            } else {
                dirFile = file;
            }
            if (null != dirFile) {
                BufferedImage image = packPolygon(dirFile.listFiles(), isTrim);
                String info = suffix_Pack + suffix_Polygon + isTrim;
                pack(dirFile, image, info);
            }
        }
    }

    public BufferedImage packPolygon(File[] array, boolean isTrim) {
        if (null != array) {
            ArrayList<BufferedImage> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = read(array[i]);
                if (null != image) {
                    arrayList.add(image);
                }
            }
            return packPolygon(arrayList, isTrim);
        }
        return null;
    }

    public BufferedImage packPolygon(ArrayList<BufferedImage> arrayList, boolean isTrim) {
        return packPolygon(toArray(arrayList), isTrim);
    }

    public BufferedImage packPolygon(BufferedImage[] array, boolean isTrim) {
        return pixelSheet.packPolygon(array, isTrim);
    }

    public void unpack(String propPath, boolean isTrim) {
    }

    public void unpack(String filePath, int x, int y, int width, int height, boolean isTrim) {
        if (null != filePath) {
            unpack(new File(filePath), x, y, width, height, isTrim);
        }
    }

    public void unpack(String filePath, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != filePath) {
            unpack(new File(filePath), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
        }
    }

    public void unpack(String imagePath, String propPath, boolean isTrim) {
    }

    public void unpack(File file, boolean isTrim) {
    }

    public void unpack(File file, int x, int y, int width, int height, boolean isTrim) {
        if (null != file) {
            BufferedImage image = read(file);
            image = unpack(image, x, y, width, height, isTrim);
            String fname = suffix_Unpack + x + y + width + height + isTrim;
            write(image, newFile(file, fname, suffix_Png, true));
        }
    }

    public void unpack(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != file) {
            ArrayList<BufferedImage> imageList;
            BufferedImage image = read(file);
            imageList = unpack(image, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
            if (null != imageList) {
                String info = suffix_Unpack + x + y + width + height + lineSize + rowSize + lineWidth + rowHeight + isTrim;
                unpack(file, imageList, pixelSheet.getUnpackProperties(), info);
            }
        }
    }

    private void unpack(File file, ArrayList<BufferedImage> arrayList, Properties properties, String info) {
        if (null != file && null != arrayList && null != properties && null != info) {
            int index = 0;
            for (Iterator<BufferedImage> iterator = arrayList.iterator(); iterator.hasNext(); ) {
                String fname = info + "_" + index;
                write(iterator.next(), newFile(file, fname, suffix_Png, true));
                index++;
            }
            String fname = file.getName();
            index = fname.lastIndexOf(".");
            if (index != -1) {
                fname = fname.substring(0, index);
            }
            fname += info;
            File propFile = newFile(file, fname, suffix_Json, false);
            writeProperties(properties, propFile);
        }
    }

    public void unpack(File imageFile, File propFile, boolean isTrim) {
    }

    public void unpack(File imageFile, Properties properties, boolean isTrim) {
    }

    public void unpack(File[] array, int x, int y, int width, int height, boolean isTrim) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                unpack(array[i], x, y, width, height, isTrim);
            }
        }
    }

    public void unpack(File[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                unpack(array[i], x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
            }
        }
    }

    public BufferedImage unpack(BufferedImage root, int x, int y, int width, int height, boolean isTrim) {
        return pixelSheet.unpack(root, x, y, width, height, isTrim);
    }

    public ArrayList<BufferedImage> unpack(BufferedImage image, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return pixelSheet.unpack(image, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
    }

    public BufferedImage unpack(BufferedImage root, Properties properties, boolean isTrim) {
        return null;
    }

    public ArrayList<BufferedImage> unpack(BufferedImage[] array, int x, int y, int width, int height, boolean isTrim) {
        if (null != array) {
            ArrayList<BufferedImage> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = unpack(array[i], x, y, width, height, isTrim);
                if (null != image) {
                    arrayList.add(image);
                }
            }
            return arrayList;
        }
        return null;
    }

    public ArrayList<ArrayList<BufferedImage>> unpack(BufferedImage[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        if (null != array) {
            ArrayList<ArrayList<BufferedImage>> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                ArrayList<BufferedImage> imageList = unpack(array[i], x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
                if (null != imageList) {
                    arrayList.add(imageList);
                }
            }
            return arrayList;
        }
        return null;
    }

    public ArrayList<BufferedImage> unpack(ArrayList<BufferedImage> arrayList, int x, int y, int width, int height, boolean isTrim) {
        return unpack(toArray(arrayList), x, y, width, height, isTrim);
    }

    public ArrayList<ArrayList<BufferedImage>> unpack(ArrayList<BufferedImage> arrayList, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
        return unpack(toArray(arrayList), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight, isTrim);
    }

    public ArrayList<BufferedImage> unpackList(BufferedImage root, Properties properties, boolean isTrim) {
        return null;
    }

    public void unpackPolygon(String filePath, boolean isTrim) {
        if (null != filePath) {
            unpackPolygon(new File(filePath), isTrim);
        }
    }

    public void unpackPolygon(String filePath, int width, int height, boolean isTrim) {
        if (null != filePath) {
            unpackPolygon(new File(filePath), width, height, isTrim);
        }
    }

    public void unpackPolygon(File file, boolean isTrim) {
        unpackPolygon(file, Width_KeepSize, Height_KeepSize, isTrim);
    }

    public void unpackPolygon(File file, int width, int height, boolean isTrim) {
        if (null != file) {
            BufferedImage image = read(file);
            ArrayList<BufferedImage> imageList = unpackPolygon(image, width, height, isTrim);
            if (null != imageList) {
                String info = suffix_Unpack + width + height + isTrim;
                unpack(file, imageList, pixelSheet.getUnpackProperties(), info);
            }
        }
    }

    public void unpackPolygon(File[] array, boolean isTrim) {
        unpackPolygon(array, Width_KeepSize, Height_KeepSize, isTrim);
    }

    public void unpackPolygon(File[] array, int width, int height, boolean isTrim) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                unpackPolygon(array[i], width, height, isTrim);
            }
        }
    }

    public ArrayList<BufferedImage> unpackPolygon(BufferedImage image, boolean isTrim) {
        return unpackPolygon(image, Width_KeepSize, Height_KeepSize, isTrim);
    }

    public ArrayList<BufferedImage> unpackPolygon(BufferedImage image, int width, int height, boolean isTrim) {
        return pixelSheet.unpackPolygon(image, width, height, isTrim);
    }

    public ArrayList<ArrayList<BufferedImage>> unpackPolygon(BufferedImage[] array, int width, int height, boolean isTrim) {
        if (null != array) {
            ArrayList<ArrayList<BufferedImage>> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                ArrayList<BufferedImage> imageList = unpackPolygon(array[i], width, height, isTrim);
                if (null != imageList) {
                    arrayList.add(imageList);
                }
            }
            return arrayList;
        }
        return null;
    }

    public ArrayList<ArrayList<BufferedImage>> unpackPolygon(ArrayList<BufferedImage> arrayList, int width, int height, boolean isTrim) {
        return unpackPolygon(toArray(arrayList), width, height, isTrim);
    }

    private BufferedImage[] toArray(ArrayList<BufferedImage> arrayList) {
        if (null != arrayList) {
            BufferedImage[] array = new BufferedImage[arrayList.size()];
            arrayList.toArray(array);
            return array;
        }
        return null;
    }
}
