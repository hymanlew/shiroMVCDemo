package hyman.utils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * <p><b>类描述：</b>图片压缩工具类 提供的方法中可以设定生成的 缩略图片的大小尺寸、压缩尺寸的比例、图片的质量等</p>
 */
@SuppressWarnings("restriction")
public class ImageUtil {

    /**
     *
     * <p><b>方法描述：</b>图片文件读取</p>
     * @param srcImgPath 源图片路径
     * @return BufferedImage
     */
    private static BufferedImage inputImage(String srcImgPath){

        BufferedImage srcImage = null;
        FileInputStream in = null;
        try {
            // 构造BufferedImage对象
            File file = new File(srcImgPath);
            in = new FileInputStream(file);
            byte[] b = new byte[5];
            in.read(b);
            srcImage = javax.imageio.ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException("读取图片文件出错！", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException("读取图片文件出错！", e);
                }
            }
        }
        return srcImage;
    }

    /**
     *
     * <p><b>方法描述：</b>将图片按照指定的图片尺寸、源图片质量压缩(默认质量为1)</p>
     * @param srcImgPath 源图片路径
     * @param outImgPath 输出的压缩图片的路径
     * @param newW 压缩后的图片宽
     * @param newH 压缩后的图片高
     */
    public static void resize(String srcImgPath, String outImgPath, int newW, int newH) {
        resize(srcImgPath, outImgPath, newW, newH, 1F);
    }

    /**
     *
     * <p><b>方法描述：</b>将图片按照指定长或者宽的最大值来压缩图片(默认质量为1)</p>
     * @param srcImgPath 源图片路径
     * @param outImgPath 输出的压缩图片的路径
     * @param maxLength 长或者宽的最大值
     */
    public static void resize(String srcImgPath, String outImgPath, int maxLength) {
        resize(srcImgPath, outImgPath, maxLength, 1F);
    }

    /**
     *
     * <p><b>方法描述：</b>将图片按照指定的图片尺寸、图片质量压缩</p>
     * @param srcImgPath 源图片路径
     * @param outImgPath 输出的压缩图片的路径
     * @param newW 压缩后的图片宽
     * @param newH 压缩后的图片高
     * @param per 图片质量 0f~1f
     */
    public static void resize(String srcImgPath, String outImgPath, int newW, int newH, float per) {
        // 得到图片
        BufferedImage src = inputImage(srcImgPath);
        int oldW = src.getWidth();
        // 得到源图宽
        int oldH = src.getHeight();
        // 得到源图长
        // 根据原图的大小生成空白画布
        BufferedImage tempImg = new BufferedImage(oldW, oldH, BufferedImage.TYPE_INT_RGB);
        // 在新的画布上生成原图的缩略图
        Graphics2D g = tempImg.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, oldW, oldH);
        g.drawImage(src, 0, 0, oldW, oldH, Color.white, null);
        g.dispose();
        BufferedImage newImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(newW, newH, Image.SCALE_SMOOTH), 0, 0, null);
        // 调用方法输出图片文件
        outImage(outImgPath, newImg, per);
    }

    /**
     *
     * <p><b>方法描述：</b>将图片按照指定的尺寸比例、图片质量压缩</p>
     * @param srcImgPath 源图片路径
     * @param outImgPath 输出的压缩图片的路径
     * @param ratio 压缩后的图片尺寸比例
     */
    public static void resize(String srcImgPath, String outImgPath, float ratio) {
        // 得到图片
        BufferedImage src = inputImage(srcImgPath);
        int oldW = src.getWidth();
        // 得到源图宽
        int oldH = src.getHeight();
        // 得到源图长
        int newW = 0;
        // 新图的宽
        int newH = 0;
        // 新图的长
        BufferedImage tempImg = new BufferedImage(oldW, oldH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImg.createGraphics();
        g.setColor(Color.white);
        // 从原图上取颜色绘制新图g.fillRect(0, 0, oldW, oldH);
        g.drawImage(src, 0, 0, oldW, oldH, Color.white, null);
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸newW = (int) Math.round(oldW * ratio);
        newH = (int) Math.round(oldH * ratio);
        BufferedImage newImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(newW, newH, Image.SCALE_SMOOTH), 0, 0, null);
    }

    /**
     *
     * <p><b>方法描述：</b>指定长或者宽的最大值来压缩图片 推荐使用此方法</p>
     * @param srcImgPath 源图片路径
     * @param outImgPath 输出的压缩图片的路径
     * @param maxLength 长或者宽的最大值
     * @param per 图片质量 0f~1f
     */
    public static void resize(String srcImgPath, String outImgPath, int maxLength, float per) {
        // 得到图片
        BufferedImage src = inputImage(srcImgPath);
        int oldW = src.getWidth();
        // 得到源图宽
        int oldH = src.getHeight();
        // 得到源图长
        int newW = 0;
        // 新图的宽
        int newH = 0;
        // 新图的长
        BufferedImage tempImg = new BufferedImage(oldW, oldH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImg.createGraphics();
        g.setColor(Color.white);
        // 从原图上取颜色绘制新图
        g.fillRect(0, 0, oldW, oldH);
        g.drawImage(src, 0, 0, oldW, oldH, Color.white, null);
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸
        if (oldW > oldH) {
            // 图片要缩放的比例
            newW = maxLength;
            newH = (int) Math.round(oldH * ((float) maxLength / oldW));
        } else {
            newW = (int) Math.round(oldW * ((float) maxLength / oldH));
            newH = maxLength;
        }
        BufferedImage newImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(newW, newH, Image.SCALE_SMOOTH), 0, 0, null);
        // 调用方法输出图片文件
        outImage(outImgPath, newImg, per);
    }

    /**
     *
     * <p><b>方法描述：</b>将图片压缩成指定宽度， 高度等比例缩放</p>
     * @param srcImgPath 源图片路径
     * @param outImgPath 输出的压缩图片的路径
     * @param width 压缩后的图片宽
     */
    public static void resizeFixedWidth(String srcImgPath, String outImgPath, int width) {
        resizeFixedWidth(srcImgPath, outImgPath, width, 1F);
    }

    /**
     *
     * <p><b>方法描述：</b>将图片压缩成指定宽度， 高度等比例缩放</p>
     * @param srcImgPath 源图片路径
     * @param outImgPath 输出的压缩图片的路径
     * @param width 压缩后的图片宽
     * @param per 压缩质量 0f~1f
     */
    public static void resizeFixedWidth(String srcImgPath, String outImgPath, int width, float per) {
        // 得到图片
        BufferedImage src = inputImage(srcImgPath);
        int oldW = src.getWidth();
        // 得到源图宽
        int oldH = src.getHeight();
        // 得到源图长
        int newW = 0;
        // 新图的宽
        int newH = 0;
        // 新图的长
        BufferedImage tempImg = new BufferedImage(oldW, oldH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImg.createGraphics();
        g.setColor(Color.white);
        // 从原图上取颜色绘制新图
        g.fillRect(0, 0, oldW, oldH);
        g.drawImage(src, 0, 0, oldW, oldH, Color.white, null);
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸
        if (oldW > oldH) {
            // 图片要缩放的比例
            newW = width;
            newH = (int) Math.round(oldH * ((float) width / oldW));
        } else {
            newW = (int) Math.round(oldW * ((float) width / oldH));
            newH = width;
        }
        BufferedImage newImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(newW, newH, Image.SCALE_SMOOTH), 0, 0, null);
        // 调用方法输出图片文件
        outImage(outImgPath, newImg, per);
    }

    /**
     *
     * <p><b>方法描述：</b>将图片压缩成指定高度， 宽度等比例缩放</p>
     * @param srcImgPath 源图片路径
     * @param outImgPath 输出的压缩图片的路径
     * @param height 压缩后的图片高
     */
    public static void resizeFixedHeight(String srcImgPath, String outImgPath, int height) {
        resizeFixedHeight(srcImgPath, outImgPath, height, 1F);
    }

    /**
     *
     * <p><b>方法描述：</b>将图片压缩成指定高度， 宽度等比例缩放</p>
     * @param srcImgPath 源图片路径
     * @param outImgPath 输出的压缩图片的路径
     * @param height 压缩后的图片高
     * @param per 压缩质量 0f~1f
     */
    public static void resizeFixedHeight(String srcImgPath, String outImgPath, int height, float per) {
        // 得到图片
        BufferedImage src = inputImage(srcImgPath);
        int oldW = src.getWidth();
        // 得到源图宽
        int oldH = src.getHeight();
        // 得到源图长
        int newW = 0;
        // 新图的宽
        int newH = 0;
        // 新图的长
        BufferedImage tempImg = new BufferedImage(oldW, oldH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImg.createGraphics();
        g.setColor(Color.white);
        // 从原图上取颜色绘制新图
        g.fillRect(0, 0, oldW, oldH);
        g.drawImage(src, 0, 0, oldW, oldH, Color.white, null);
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸
        if (oldH > oldW) {
            // 图片要缩放的比例
            newH = (int) Math.round(oldH * ((float) height / oldW));
            newW = height;
        } else {
            newH = height;
            newW = (int) Math.round(oldW * ((float) height / oldH));
        }
        BufferedImage newImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        newImg.getGraphics().drawImage(tempImg.getScaledInstance(newW, newH, Image.SCALE_SMOOTH), 0, 0, null);
        // 调用方法输出图片文件
        outImage(outImgPath, newImg, per);
    }

    /**
     *
     * <p><b>方法描述：</b>将图片文件输出到指定的路径，并可设定压缩质量</p>
     * @param outImgPath 输出图片路径
     * @param newImg 新图片
     * @param per 压缩质量 0f~1f
     */
    private static void outImage(String outImgPath, BufferedImage newImg, float per) {
        // 判断输出的文件夹路径是否存在，不存在则创建
        File file = new File(outImgPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 输出到文件流
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outImgPath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(newImg);
            // 压缩质量
            jep.setQuality(per, true);
            encoder.encode(newImg, jep);
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     *
     *
     * @param srcfile
     *
     * @param outfile
     *
     * @param x
     *
     * @param y
     *
     * @param width
     *
     * @param height
     *
     *
     * @throws IOException
     */
    /**
     *
     * <p><b>方法描述：</b>图片剪切工具方法</p>
     * @param srcfile 源图片
     * @param outfile 剪切之后的图片
     * @param x 剪切顶点(左上角点) X 坐标
     * @param y 剪切顶点(左上角点) Y 坐标
     * @param width 剪切区域宽度
     * @param height 剪切区域高度
     */
    public static void cut(File srcfile, File outfile, int x, int y, int width, int height) {
        FileInputStream is = null;
        ImageInputStream iis = null;
        try {
            // 读取图片文件
            is = new FileInputStream(srcfile);

            /*
             * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。
             * 参数：formatName - 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
             */
            String extName = srcfile.getName().substring(srcfile.getName().lastIndexOf(".") + 1, srcfile.getName().length()).toLowerCase();
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(extName);
            ImageReader reader = it.next();
            // 获取图片流
            iis = ImageIO.createImageInputStream(is);

            /*
             * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。
             * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
             */
            reader.setInput(iis, true);

            /*
             * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
             * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的
             * getDefaultReadParam 方法中返回 ImageReadParam 的实例。
             */
            ImageReadParam param = reader.getDefaultReadParam();

            /*
             * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
             * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
             */
            Rectangle rect = new Rectangle(x, y, width, height);

            // 提供一个 BufferedImage，将其用作解码像素数据的目标。
            param.setSourceRegion(rect);

            /*
             * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的
             * BufferedImage 返回。
             */
            BufferedImage bi = reader.read(0, param);

            // 保存新图片
            ImageIO.write(bi, extName, outfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (iis != null) {
                try {
                    iis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
