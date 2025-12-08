package cn.imaginary.toolkit.image;

//import cn.imaginary.toolkit.image.sheet.PixelScanner;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

//import javaev.awt.PolygonUtils;

import javax.imageio.ImageIO;

public class PixelPacker {

    private String suffix_Pack = "_pack_";
    private String suffix_Unpack = "_unpack_";
    private String suffix_Png = "png";

    public BufferedImage drawImage(BufferedImage[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != array) {
            if (lineSize <= 0) {
                lineSize = array.length;
            }
            if (rowSize <= 0) {
                rowSize = array.length / lineSize;
            }
            Rectangle rectangle = getBounds(array, width, height, lineSize, rowSize, lineWidth, rowHeight);
//            System.out.println("rectangle:" + rectangle.toString());
            BufferedImage image = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_ARGB);
            drawImage(image, array, width, height, lineSize, rowSize, lineWidth, rowHeight);
            if (x != 0 || y != 0) {
                return drawImage(image, x, y);
            } else {
                return image;
            }
        }
        return null;
    }

    public void drawImage(BufferedImage image, BufferedImage[] array, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != image && null != array) {
            int[] bounds = getBounds(array);
//            System.out.println("bounds:" + bounds);
            int w_max = bounds[1];
            int h_max = bounds[3];
            if (width <= 0) {
                width = w_max;
            }
            if (height <= 0) {
                height = h_max;
            }
//            System.out.println("width:" + width + "/height:" + height);
            Graphics2D graphics2D = image.createGraphics();
            for (int i = 0; i < array.length; i++) {
                int line = i % lineSize;
                int row = i / lineSize;
                if (row >= rowSize) {
                    break;
                }
                int x = line * (width + lineWidth);
                int y = row * (height + rowHeight);
//                System.out.println("x:" + x + "/y:" + y);
                drawImage(graphics2D, array[i], x, y, width, height);
            }
            graphics2D.dispose();
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

    private void drawImage(Graphics2D graphics2D, BufferedImage image, int x, int y, int width, int height) {
        if (null != graphics2D && null != image) {
            if (width > 0 && height > 0) {
                int w = image.getWidth();
                int h = image.getHeight();
                int x_ = (width - w) / 2;
                int y_ = (height - h) / 2;
                x += x_;
                y += y_;
                graphics2D.drawImage(image, x, y, null);
            }
        }
    }

    public Rectangle getBounds(BufferedImage[] array, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
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
            return new Rectangle(0, 0, w, h);
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
                ImageIO.write(image, suffix_Png, file);
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

    public void pack(File file) {
        pack(file, 0, 0, -1, -1, -1, -1, 0, 0);
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
                String path = dirFile.getAbsolutePath() + suffix_Pack + x + y + width + height + lineSize + rowSize + lineWidth + rowHeight + ".png";
                File packFile = new File(path);
                write(image, packFile);
            }
        }
    }

    public BufferedImage pack(File[] array) {
        return pack(array, 0, 0, 0, 0, -1, -1, 0, 0);
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
            return pack(arrayList, x, y, width, height, lineSize, rowSize, lineWidth, rowHeight);
        }
        return null;
    }

    public void pack(String filePath) {
        pack(filePath, 0, 0, 0, 0, -1, -1, 0, 0);
    }

    public void pack(String filePath, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        if (null != filePath) {
            pack(new File(filePath), x, y, width, height, lineSize, rowSize, lineWidth, rowHeight);
        }
    }

    public BufferedImage pack(ArrayList<BufferedImage> arrayList) {
        return pack(arrayList, 0, 0, 0, 0, -1, -1, 0, 0);
    }

    public BufferedImage pack(BufferedImage[] array) {
        return pack(array, 0, 0, 0, 0, -1, -1, 0, 0);
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

    public void packPolygon(File file) {
    }

    public void packPolygon(File[] array) {
    }

    public void packPolygon(String filePath) {
    }

    public BufferedImage packPolygon(ArrayList<BufferedImage> arrayList) {
        return null;
    }

    public BufferedImage packPolygon(BufferedImage[] array) {
        return null;
    }

    public void unpack(File file) {
    }

    public void unpack(File[] array) {
    }

    public void unpack(String filePath) {
    }

    public ArrayList<BufferedImage> unpack(BufferedImage image) {
        return null;
    }

    public void unpack(File file, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
    }

    public void unpack(File[] array, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
    }

    public void unpack(String filePath, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
    }

    public ArrayList<BufferedImage> unpack(BufferedImage image, int x, int y, int width, int height, int lineSize, int rowSize, int lineWidth, int rowHeight) {
        return null;
    }

    public void unpackPolygon(File file) {
    }

    public void unpackPolygon(File[] array) {
    }

    public void unpackPolygon(String filePath) {
    }

    public ArrayList<BufferedImage> unpackPolygon(BufferedImage image) {
        return null;
    }

    public void unpackPolygon(File file, int width, int height) {
    }

    public void unpackPolygon(File[] array, int width, int height) {
    }

    public void unpackPolygon(String filePath, int width, int height) {
    }

    public ArrayList<BufferedImage> unpackPolygon(BufferedImage image, int width, int height) {
        return null;
    }

    public void unpackPolygonTrim(File file) {
    }

    public void unpackPolygonTrim(File[] array) {
    }

    public void unpackPolygonTrim(String filePath) {
    }

    public ArrayList<BufferedImage> unpackPolygonTrim(BufferedImage image) {
        return null;
    }
}