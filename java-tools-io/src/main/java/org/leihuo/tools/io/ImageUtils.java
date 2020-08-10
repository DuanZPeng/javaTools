/**
 * @创建人 段志鹏
 */
package org.leihuo.tools.io;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Date;


/**
 * ImageUtils
 * @创建人 段志鹏
 * @创建时间 2019年7月10日 上午9:37:52
 */
@Slf4j
public class ImageUtils {

    private static String DEFAULT_THUMB_PREVFIX = "thumb_";
    private static String DEFAULT_CUT_PREVFIX = "cut_";
    private static Boolean DEFAULT_FORCE = false;

    private static String DEFAULT_PREVFIX = "thumb_";
    //private static final transient Logger log = LoggerFactory.getLogger(ImageUtils.class);

    /**
     * 将图片写到 硬盘指定目录下
     *
     * @param in
     * @param dirPath
     * @param filePath
     */
    public static void savePicToDisk(InputStream in, String dirPath, String filePath) {

        try {
            File dir = new File(dirPath);
            if (dir == null || !dir.exists()) {
                dir.mkdirs();
            }

            //文件真实路径
            String realPath = dirPath.concat(filePath);
            File file = new File(realPath);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	/*public static byte[] cutImage(InputStream srcIn, String rect) {
		return cutImage(srcIn, rect, "jpg");
	}*/

	/*public static byte[] cutImage(InputStream srcIn, String rect, String suffix) {

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		Rect r = Rect.getRect(rect);
		cutImage(output, suffix, r, srcIn);
		return output.toByteArray();

	}*/

    public static byte[] cutImage(byte[] imageBytes, String rect) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Rect r = Rect.getRect(rect);
        cutImage(output, "jpg", r, imageBytes);
        return output.toByteArray();

    }

    public static void parseImage(byte[] imageBytes, String rect, String formatName, String path1, String path2) {
        try {
            Date d1 = new Date();
            //BufferedImage image = Thumbnails.of(new ByteArrayInputStream(imageBytes)).scale(1.0f).asBufferedImage();

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            Date d2 = new Date();
            System.out.println("取图耗时：" + (d2.getTime() - d1.getTime()));
            if (path1 != null) {
                //ImageIO.write(image, formatName, new FileOutputStream(path1+"."+formatName));
                FileUtils.writeFileByLines(path1 + "." + formatName, imageBytes);
            }
            Date d3 = new Date();
            System.out.println("保存大图耗时：" + (d3.getTime() - d2.getTime()));

            if (path2 != null) {
                Rect r = Rect.getRect(rect);
                r.setSrch(image.getHeight());
                r.setSrcw(image.getWidth());
                BufferedImage subimage = image.getSubimage(r.getRx(), r.getRy(), r.getRw(), r.getRh());

                Date d4 = new Date();
                System.out.println("取小图耗时：" + (d4.getTime() - d3.getTime()));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(subimage, formatName, bos);
                //new FileOutputStream(path2+"."+formatName)
                FileUtils.writeFileByLines(path2 + "." + formatName, bos.toByteArray());
                Date d5 = new Date();
                System.out.println("保存小图耗时：" + (d5.getTime() - d4.getTime()));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //ByteArrayOutputStream output = new ByteArrayOutputStream();
        //Rect r = Rect.getRect(rect);
        //cutImage(output, "jpg", r, imageBytes);
        //return output.toByteArray();

    }

    public static byte[] cutImage(byte[] imageBytes, String rect, String format) {
        try {
            //BufferedImage image = Thumbnails.of(new ByteArrayInputStream(imageBytes)).scale(1.0f).asBufferedImage();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            Rect r = Rect.getRect(rect);
            r.setSrch(image.getHeight());
            r.setSrcw(image.getWidth());
            BufferedImage subimage = image.getSubimage(r.getRx(), r.getRy(), r.getRw(), r.getRh());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(subimage, format, bos);
            return bos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static byte[] cutImage(byte[] imageBytes, int x, int y, int width, int height) {
        return cutImage(imageBytes, "jpg", new java.awt.Rectangle(x, y, width, height));
    }

    public static byte[] cutImage(byte[] imageBytes, String suffix, java.awt.Rectangle rect) {
        InputStream srcIn = new ByteArrayInputStream(imageBytes);
        return cutImage(srcIn, suffix, rect);
    }

    public static byte[] cutImage(InputStream srcIn, String suffix, java.awt.Rectangle rect) {
        ImageInputStream iis = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            iis = ImageIO.createImageInputStream(srcIn);
            cutImage(output, suffix, rect, iis);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (iis != null)
                    iis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 方法功能：
     * @param output
     * @param suffix
     * @param rect
     * @param iis
     * @throws IOException
     * @创建人 段志鹏
     * @创建时间 2019年7月11日 下午2:13:49
     */
    private static void cutImage(OutputStream output, String suffix, java.awt.Rectangle rect, ImageInputStream iis) throws IOException {
        // 根据图片类型获取该种类型的ImageReader
        ImageReader reader = null;
        try {
            reader = ImageIO.getImageReadersBySuffix(suffix).next();
            reader.setInput(iis, true);

            Image img = ImageIO.read(iis);

            ImageReadParam param = reader.getDefaultReadParam();
            BufferedImage destination = param.getDestination();
            destination.getWidth();
            destination.getHeight();
            //BufferedImage
            int[] sourceBands = param.getSourceBands();

            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            ImageIO.write(bi, suffix, output);
        } finally {
            if (reader != null) {
                reader.dispose();
            }

        }
    }

    public static class Rect {

        public static Rect getRect(String rectStr) {
            //rect里面四个数字，依次是height，width，x，y。可用于人脸抠图。
            Rect r = new Rect();
            String[] restArr = rectStr.split(",");
            r.setX(Double.valueOf(restArr[2]));
            r.setY(Double.valueOf(restArr[3]));
            r.setHeight(Double.valueOf(restArr[0]));
            r.setWidth(Double.valueOf(restArr[1]));
            return r;
        }

        /**
         * @return the srcw
         */
        public int getSrcw() {
            return srcw;
        }

        /**
         * @param srcw the srcw to set
         */
        public void setSrcw(int srcw) {
            this.srcw = srcw;
        }

        /**
         * @return the srch
         */
        public int getSrch() {
            return srch;
        }

        /**
         * @param srch the srch to set
         */
        public void setSrch(int srch) {
            this.srch = srch;
        }

        private int srcw;
        private int srch;

        private double x;
        private double y;
        private double width;
        private double height;

        /**
         * @return the x
         */
        public double getX() {
            return x;
        }

        /**
         * @param x the x to set
         */
        public void setX(double x) {
            this.x = x;
        }

        /**
         * @return the y
         */
        public double getY() {
            return y;
        }

        /**
         * @param y the y to set
         */
        public void setY(double y) {
            this.y = y;
        }

        /**
         * @return the width
         */
        public double getWidth() {
            return width;
        }

        /**
         * @param width the width to set
         */
        public void setWidth(double width) {
            this.width = width;
        }

        /**
         * @return the height
         */
        public double getHeight() {
            return height;
        }

        /**
         * @param height the height to set
         */
        public void setHeight(double height) {
            this.height = height;
        }

        public int getRx() {
            return (int) (srcw * x);
        }

        public int getRy() {
            return (int) (srch * y);
        }

        public int getRw() {
            return (int) (srcw * width);
        }

        public int getRh() {
            return (int) (srch * height);
        }
    }

    private static void cutImage(OutputStream output, String suffix, Rect r, byte[] bytes) {
        // 根据图片类型获取该种类型的ImageReader
        ImageReader reader = null;
        ImageInputStream iis = null;
        try {

            InputStream in = new ByteArrayInputStream(bytes);
            //BufferedImage read = ImageIO.read(in);

            BufferedImage destination = ImageIO.read(in);
            int width = destination.getWidth(null);
            int height = destination.getHeight(null);

            InputStream in2 = new ByteArrayInputStream(bytes);
            //int width = 1920;
            //int height = 1080;
            reader = ImageIO.getImageReadersBySuffix(suffix).next();
            iis = ImageIO.createImageInputStream(in2);
            //Image img = ImageIO.read(iis);

            ImageReadParam param = reader.getDefaultReadParam();

            reader.setInput(iis, true);

            //ImageReadParam param1 = reader.getDefaultReadParam();
            //BufferedImage destination = param.getDestination();
            //int width = destination.getWidth(null);
            //int height = destination.getHeight(null);
            //BufferedImage
            java.awt.Rectangle rect = new Rectangle((int) (width * r.getX()), (int) (height * r.getY()), (int) (width * r.getWidth()),
                    (int) (height * r.getHeight()));
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            ImageIO.write(bi, suffix, output);
        } catch (IOException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (reader != null) {
                    reader.dispose();
                }
                if (iis != null) {
                    iis.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>Title: cutImage</p>
     * <p>Description:  根据原图与裁切size截取局部图片</p>
     * @param srcImg    源图片
     * @param output    图片输出流
     * @param rect        需要截取部分的坐标和大小
     */
    public static void cutImage(File srcImg, OutputStream output, java.awt.Rectangle rect) {
        if (srcImg.exists()) {
            java.io.FileInputStream fis = null;
            ImageInputStream iis = null;
            try {
                fis = new FileInputStream(srcImg);
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
                String suffix = null;
                // 获取图片后缀
                if (srcImg.getName().indexOf(".") > -1) {
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                } // 类型和图片后缀全部小写，然后判断后缀是否合法
                if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase() + ",") < 0) {
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return;
                }
                // 将FileInputStream 转换为ImageInputStream
                iis = ImageIO.createImageInputStream(fis);
                cutImage(output, suffix, rect, iis);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                    if (iis != null)
                        iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            log.warn("the src image is not exist.");
        }
    }

    public static void cutImage(File srcImg, OutputStream output, int x, int y, int width, int height) {
        cutImage(srcImg, output, new java.awt.Rectangle(x, y, width, height));
    }

    public static void cutImage(File srcImg, String destImgPath, java.awt.Rectangle rect) {
        File destImg = new File(destImgPath);
        if (destImg.exists()) {
            String p = destImg.getPath();
            try {
                if (!destImg.isDirectory())
                    p = destImg.getParent();
                if (!p.endsWith(File.separator))
                    p = p + File.separator;
                cutImage(srcImg, new java.io.FileOutputStream(p + DEFAULT_CUT_PREVFIX + "_" + new java.util.Date().getTime() + "_" + srcImg.getName()), rect);
            } catch (FileNotFoundException e) {
                log.warn("the dest image is not exist.");
                throw new RuntimeException("the dest image is not exist.");
            }
        } else {
            log.warn("the dest image folder is not exist.");
            throw new RuntimeException("the dest image folder is not exist.");
        }
    }

    public static void cutImage(File srcImg, String destImg, int x, int y, int width, int height) {
        cutImage(srcImg, destImg, new java.awt.Rectangle(x, y, width, height));
    }

    public static void cutImage(String srcImg, String destImg, int x, int y, int width, int height) {
        cutImage(new File(srcImg), destImg, new java.awt.Rectangle(x, y, width, height));
    }

    /**
     *
     * 方法功能：
     * @param srcIn
     * @param w            缩略图宽
     * @param h            缩略图高
     * @param force        是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     * @return
     * @创建人 段志鹏
     * @创建时间 2019年7月11日 下午12:50:10
     */
    public static byte[] thumbnailImage(InputStream srcIn, int w, int h, boolean force, String suffix) {
        try {
            Image img = ImageIO.read(srcIn);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            thumbnailImage(out, w, h, force, suffix, img);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * 方法功能：
     * @param bytes
     * @param w            缩略图宽
     * @param h            缩略图高
     * @param force        是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     * @return
     * @创建人 段志鹏
     * @创建时间 2019年7月11日 下午12:52:21
     */
    public static byte[] thumbnailImage(byte[] bytes, int w, int h, boolean force, String suffix) {
        InputStream srcIn = new ByteArrayInputStream(bytes);
        return thumbnailImage(srcIn, w, h, force, suffix);
    }

    /**
     * <p>Title: thumbnailImage</p>
     * <p>Description: 根据图片路径生成缩略图 </p>
     * @param w            缩略图宽
     * @param h            缩略图高
     * @param prevfix    生成缩略图的前缀
     * @param force        是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     */
    public static void thumbnailImage(File srcImg, OutputStream output, int w, int h, String prevfix, boolean force) {
        if (srcImg.exists()) {
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
                String suffix = null;
                // 获取图片后缀
                if (srcImg.getName().indexOf(".") > -1) {
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                } // 类型和图片后缀全部小写，然后判断后缀是否合法
                if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase() + ",") < 0) {
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return;
                }
                log.debug("target image's size, width:{}, height:{}.", w, h);
                Image img = ImageIO.read(srcImg);
                // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
                thumbnailImage(output, w, h, force, suffix, img);
                output.close();
            } catch (IOException e) {
                log.error("generate thumbnail image failed.", e);
                throw new RuntimeException(e);
            }
        } else {
            log.warn("the src image is not exist.");
            throw new RuntimeException("the src image is not exist.");
        }
    }

    /**
     * 方法功能：
     * @param output
     * @param w
     * @param h
     * @param force
     * @param suffix
     * @param img
     * @throws IOException
     * @创建人 段志鹏
     * @创建时间 2019年7月11日 下午12:48:38
     */
    private static void thumbnailImage(OutputStream output, int w, int h, boolean force, String suffix, Image img) throws IOException {
        if (!force) {
            int width = img.getWidth(null);
            int height = img.getHeight(null);
            if ((width * 1.0) / w < (height * 1.0) / h) {
                if (width > w) {
                    h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w / (width * 1.0)));
                    log.debug("change image's height, width:{}, height:{}.", w, h);
                }
            } else {
                if (height > h) {
                    w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h / (height * 1.0)));
                    log.debug("change image's width, width:{}, height:{}.", w, h);
                }
            }
        }
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
        g.dispose();
        // 将图片保存在原目录并加上前缀
        ImageIO.write(bi, suffix, output);
    }

    public static void thumbnailImage(File srcImg, int w, int h, String prevfix, boolean force) {
        String p = srcImg.getAbsolutePath();
        try {
            if (!srcImg.isDirectory())
                p = srcImg.getParent();
            if (!p.endsWith(File.separator))
                p = p + File.separator;
            thumbnailImage(srcImg, new java.io.FileOutputStream(p + prevfix + srcImg.getName()), w, h, prevfix, force);
        } catch (FileNotFoundException e) {
            log.error("the dest image is not exist.", e);
            throw new RuntimeException(e);

        }
    }

    public static void thumbnailImage(String imagePath, int w, int h, String prevfix, boolean force) {
        File srcImg = new File(imagePath);
        thumbnailImage(srcImg, w, h, prevfix, force);
    }

    public static void thumbnailImage(String imagePath, int w, int h, boolean force) {
        thumbnailImage(imagePath, w, h, DEFAULT_THUMB_PREVFIX, DEFAULT_FORCE);
    }

    public static void thumbnailImage(String imagePath, int w, int h) {
        thumbnailImage(imagePath, w, h, DEFAULT_FORCE);
    }

    public static void main(String[] args) {
        new ImageUtils().thumbnailImage("imgs/Tulips.jpg", 150, 100);
        new ImageUtils().cutImage("imgs/Tulips.jpg", "imgs", 250, 70, 300, 400);
    }

    public static void deleteAll(String filename, boolean delRoot) {
        deleteAll(new File(filename), delRoot);
    }

    public static void deleteAll(File file, boolean delRoot) {

        if (!file.exists()) {
            return;
        }
        if ("..".equals(file.getName()) || ".".equals(file.getName())) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File f : listFiles) {
                deleteAll(f, true);
            }
            if (delRoot) {
                file.delete();
            }

        }
    }

    public static byte[] thumbnailImage(byte[] imageBytes, double scale) {
        return thumbnailImage(imageBytes, scale, "jpg");
    }

    public static byte[] thumbnailImage(byte[] imageBytes, double scale, String format) {
        Graphics graphics = null;
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            int imagew = image.getWidth();
            int imageh = image.getHeight();
            int width = parseDoubleToInt(imagew * scale);
            int height = parseDoubleToInt(imageh * scale);
            Image i = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            graphics = outputImage.getGraphics();
            graphics.drawImage(i, 0, 0, null);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(outputImage, format, bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (graphics != null) {
                graphics.dispose();
            }
        }
    }

    private static int parseDoubleToInt(double sourceDouble) {
        int result = 0;
        result = (int) sourceDouble;
        return result;
    }

}
