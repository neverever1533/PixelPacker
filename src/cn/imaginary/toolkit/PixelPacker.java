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
    public static int Line_MaxSize = -1;
    public static int Row_MaxSize = -1;
    public static int Width_KeepSize = 0;
    public static int Height_KeepSize = 0;
    public static int Width_MaxSize = -1;
    public static int Height_MaxSize = -1;

    private String lineSeparator = System.lineSeparator();
    private String suffix_Pack = "_pack_";
    private String suffix_Unpack = "_unpack_";
    private String suffix_Json = ".json";
    private String suffix_Png = ".png";
    private String suffix_Xml = ".xml";
    private String tag_comment = "sprite pack";
    private String tag_encoding = "utf-8";
    private String tag_png = "png";
    private String tag_polygon = "polygon";

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
//            System.out.println("file is exists:" + file.exists());
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
//        System.out.println("image is null:" + (null == image));
        if (null != image && null != file) {
//            System.out.println("file:" + file.getAbsolutePath());
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

    private File newFile(File file, Object name, boolean isSuffix) {
        File newFile = null;
        if (null != file && null != name) {
            String fname = file.getName();
            File pfile = file.getParentFile();
            if (null != pfile) {
                if (isSuffix) {
                    int index = fname.lastIndexOf(".");
                    if (index != -1) {
                        fname = fname.substring(0, index) + name + fname.substring(index);
                    } else {
                        fname += name;
                    }
                } else {
                    fname = name.toString();
                }
                newFile = new File(pfile, fname);
            }
//            System.out.println("name:" + fname);
        }
        return newFile;
    }

    public void pack(String filePath, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != filePath) {
            pack(new File(filePath), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight);
        }
    }

    public void pack(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != file) {
            File dirFile;
            if (file.isFile()) {
                dirFile = file.getParentFile();
            } else {
                dirFile = file;
            }
            if (null != dirFile) {
                BufferedImage image = pack(dirFile.listFiles(), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight);
                String info = suffix_Pack + x + y + width + height + lineSize + rowSize + lineWidth + rowHeight;
                String path = dirFile.getAbsolutePath() + info + suffix_Png;
                File packFile = new File(path);
                write(image, packFile);
                String fname = dirFile.getName() + info + suffix_Json;
                File propFile = newFile(packFile, fname, false);
                writeProperties(pixelSheet.getPackProperties(), propFile);
            }
        }
    }

    public BufferedImage pack(File[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != array) {
            ArrayList<BufferedImage> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = read(array[i]);
                if (null != image) {
                    arrayList.add(image);
                }
            }
            return pixelSheet.pack(arrayList, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight);
        }
        return null;
    }

    public void packLineKeepSize(String filePath) {
        if (null != filePath) {
            packLineKeepSize(new File(filePath));
        }
    }

    public void packLineKeepSize(String filePath, int x, int y, int lineWidth, int rowHeight) {
        if (null != filePath) {
            packLineKeepSize(new File(filePath), x, y, lineWidth, rowHeight);
        }
    }

    public void packLineKeepSize(File file) {
        packLineKeepSize(file, 0, 0, 0, 0);
    }

    public void packLineKeepSize(File file, int x, int y, int lineWidth, int rowHeight) {
        pack(file, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, 1, lineWidth, rowHeight);
    }

    public BufferedImage packLineKeepSize(File[] array) {
        return packLineKeepSize(array, 0, 0, 0, 0);
    }

    public BufferedImage packLineKeepSize(File[] array, int x, int y, int lineWidth, int rowHeight) {
        return pack(array, x, y, Width_KeepSize, Height_KeepSize, Line_MaxSize, 1, lineWidth, rowHeight);
    }

    public void packLineMaxSize(String filePath) {
        if (null != filePath) {
            packLineMaxSize(new File(filePath));
        }
    }

    public void packLineMaxSize(String filePath, int x, int y, int lineWidth, int rowHeight) {
        if (null != filePath) {
            packLineMaxSize(new File(filePath), x, y, lineWidth, rowHeight);
        }
    }

    public void packLineMaxSize(File file) {
        packLineMaxSize(file, 0, 0, 0, 0);
    }

    public void packLineMaxSize(File file, int x, int y, int lineWidth, int rowHeight) {
        pack(file, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, 1, lineWidth, rowHeight);
    }

    public BufferedImage packLineMaxSize(File[] array) {
        return packLineMaxSize(array, 0, 0, 0, 0);
    }

    public BufferedImage packLineMaxSize(File[] array, int x, int y, int lineWidth, int rowHeight) {
        return pack(array, x, y, Width_MaxSize, Height_MaxSize, Line_MaxSize, 1, lineWidth, rowHeight);
    }

    public void packRowKeepSize(String filePath) {
        if (null != filePath) {
            packRowKeepSize(new File(filePath));
        }
    }

    public void packRowKeepSize(String filePath, int x, int y, int lineWidth, int rowHeight) {
        if (null != filePath) {
            packRowKeepSize(new File(filePath), x, y, lineWidth, rowHeight);
        }
    }

    public void packRowKeepSize(File file) {
        packRowKeepSize(file, 0, 0, 0, 0);
    }

    public void packRowKeepSize(File file, int x, int y, int lineWidth, int rowHeight) {
        pack(file, x, y, Width_KeepSize, Height_KeepSize, 1, Row_MaxSize, lineWidth, rowHeight);
    }

    public BufferedImage packRowKeepSize(File[] array) {
        return packRowKeepSize(array, 0, 0, 0, 0);
    }

    public BufferedImage packRowKeepSize(File[] array, int x, int y, int lineWidth, int rowHeight) {
        return pack(array, x, y, Width_KeepSize, Height_KeepSize, 1, Row_MaxSize, lineWidth, rowHeight);
    }

    public void packRowMaxSize(String filePath) {
        if (null != filePath) {
            packRowMaxSize(new File(filePath));
        }
    }

    public void packRowMaxSize(String filePath, int x, int y, int lineWidth, int rowHeight) {
        if (null != filePath) {
            packRowMaxSize(new File(filePath), x, y, lineWidth, rowHeight);
        }
    }

    public void packRowMaxSize(File file) {
        packRowMaxSize(file, 0, 0, 0, 0);
    }

    public void packRowMaxSize(File file, int x, int y, int lineWidth, int rowHeight) {
        pack(file, x, y, Width_MaxSize, Height_MaxSize, 1, Row_MaxSize, lineWidth, rowHeight);
    }

    public BufferedImage packRowMaxSize(File[] array) {
        return packRowMaxSize(array, 0, 0, 0, 0);
    }

    public BufferedImage packRowMaxSize(File[] array, int x, int y, int lineWidth, int rowHeight) {
        return pack(array, x, y, Width_MaxSize, Height_MaxSize, 1, Row_MaxSize, lineWidth, rowHeight);
    }

    public void packPolygon(String filePath) {
        if (null != filePath) {
            packPolygon(new File(filePath));
        }
    }

    public void packPolygon(File file) {
        if (null != file) {
            File dirFile;
            if (file.isFile()) {
                dirFile = file.getParentFile();
            } else {
                dirFile = file;
            }
            if (null != dirFile) {
                BufferedImage image = packPolygon(dirFile.listFiles());
                String info = suffix_Pack + tag_polygon;
                String path = dirFile.getAbsolutePath() + info + suffix_Png;
                File packFile = new File(path);
                write(image, packFile);
//                String fname = dirFile.getName() + suffix_Pack_Properties + suffix_Xml;
                String fname = dirFile.getName() + info + suffix_Json;
                File propFile = newFile(packFile, fname, false);
                writeProperties(pixelSheet.getPackProperties(), propFile);
            }
        }
    }

    public BufferedImage packPolygon(File[] array) {
        if (null != array) {
            ArrayList<BufferedImage> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                BufferedImage image = read(array[i]);
                if (null != image) {
                    arrayList.add(image);
                }
            }
            return pixelSheet.packPolygon(arrayList);
        }
        return null;
    }

    public void unpack(String filePath) {
    }

    public void unpack(String filePath, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
    }

    public void unpack(File file) {
    }

    public void unpack(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
    }

    public void unpack(File[] array) {
    }

    public void unpack(File[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
    }

    private void unpack(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight, boolean isTrim) {
    }

    public void unpackTrim(File file) {
    }

    public void unpackTrim(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
    }

    public void unpackTrim(File[] array) {
    }

    public void unpackTrim(File[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
    }

    public void unpackPolygon(String filePath) {
    }

    public void unpackPolygon(String filePath, int width, int height) {
    }

    public void unpackPolygon(File file) {
    }

    public void unpackPolygon(File file, int width, int height) {
    }

    public void unpackPolygon(File[] array) {
    }

    public void unpackPolygon(File[] array, int width, int height) {
    }

    private void unpackPolygon(File file, int width, int height, boolean isTrim) {
    }

    public void unpackPolygonTrim(String filePath) {
    }

    private void unpackPolygonTrim(String filePath, int width, int height) {
    }

    public void unpackPolygonTrim(File file) {
    }

    private void unpackPolygonTrim(File file, int width, int height) {
    }

    public void unpackPolygonTrim(File[] array) {
    }

    private void unpackPolygonTrim(File[] array, int width, int height) {
    }
}
