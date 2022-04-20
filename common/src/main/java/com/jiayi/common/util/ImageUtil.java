package com.jiayi.common.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Pattern;


/**
 * 图片工具类
 *
 * @author wyq
 * @date 2020-09-04
 */
@Slf4j
public final class ImageUtil {

    /**
     * 上传图片大小 10M
     */
    public static long MAX_FILE_SIZE = 10485760;
    private BufferedImage image = null;

    /**
     * 构造方法
     */
    private ImageUtil() {
    }

    /**
     * 缩放图片 生成新的图片
     *
     * @param srcFile    源文件路径
     * @param dstFile    目标文件路径
     * @param width      宽度
     * @param height     高度
     * @param formatName 格式化名称
     * @return boolean
     */
    public static boolean zoomImage(String srcFile, String dstFile, int width, int height, String formatName) {
        try {
            ImageUtil zoom = new ImageUtil();
            zoom.load(new File(srcFile));
            zoom.zoomTo(width, height);
            zoom.save(dstFile, formatName);
        } catch (IOException ex) {
            log.error("ImageUtils.zoomImage 缩放错误:{}" + ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 加载图片
     *
     * @param file 文件
     * @return ImageUtils
     * @throws IOException IOException
     */
    public static ImageUtil fromImageFile(File file) throws IOException {
        ImageUtil utils = new ImageUtil();
        utils.load(file);
        return utils;
    }

    /**
     * 加载图片
     *
     * @param fileName 文件名称字符串
     * @return ImageUtils
     * @throws IOException IOException
     */
    public static ImageUtil load(String fileName) throws IOException {
        File file = new File(fileName);
        return fromImageFile(file);
    }

    /**
     * 下载图片
     *
     * @param urlString 下载路径
     * @param tempFile  临时文件
     * @throws Exception Exception
     */
    public static void download(String urlString, File tempFile) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        // 输入流
        InputStream is = con.getInputStream();
        FileOutputStream os = null;
        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        int len;// 读取到的数据长度
        try {
            // 输出的文件流
            os = new FileOutputStream(tempFile);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (IOException e) {
            log.error("----->ImageUtil.download:{}", e);
            throw e;
        } finally {
            // 完毕，关闭所有链接
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                log.error("ImageUtil.download  关闭流错误:{}", ex);
            }
        }
    }

    /**
     * 根据文件名称获取文件后缀
     *
     * @param filename
     * @return
     * @author: kui.liu
     */
    public static String getExtensionName(String filename) {
        if (filename == null || filename.length() == 0) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }

    /**
     * 转换文件为Byte[]
     *
     * @param file 文件
     * @return byte
     */
    public static byte[] getFileToByte(File file) {
        byte[] by = new byte[(int) file.length()];
        InputStream is = null;
        ByteArrayOutputStream bytestream = null;
        try {
            is = new FileInputStream(file);
            bytestream = new ByteArrayOutputStream();
            byte[] bb = new byte[2048];
            int ch;
            ch = is.read(bb);
            while (ch != -1) {
                bytestream.write(bb, 0, ch);
                ch = is.read(bb);
            }
            by = bytestream.toByteArray();
        } catch (Exception ex) {
            log.error("ImageUtils.zoomImage 转换异常 :{}", ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bytestream != null) {
                    bytestream.close();
                }
            } catch (Exception ex) {
                log.error("ImageUtils.zoomImage 关闭流错误 :{}", ex);
            }
        }
        return by;
    }

    /**
     * 验证是否图片格式，扩展名是否.jpg .jpeg  .png
     *
     * @return
     */
    public static Boolean checkIsImage(String imageName) {
        return Pattern.matches("^.+\\.(jpg|jpeg|png)$", imageName.toLowerCase());
    }

    /**
     * 修改文件名
     *
     * @param sourceFile String
     * @param targetFile String
     * @return boolean
     */
    public static boolean editFileName(String sourceFile, String targetFile) {
        File sFile = new File(sourceFile);

        if (!sFile.exists()) {
            log.warn("要修改的文件不存在");
            return false;
        }

        File tFile = new File(targetFile);
        if (!sFile.renameTo(tFile)) {
            return false;
        }
        return true;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            //文件存在时
            if (oldfile.exists()) {
                //读入原文件
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    //字节数 文件大小
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }

                inStream.close();
                fs.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public static void main(String[] args) {
        try {
            String fname = "证件照片";
            System.out.println(URLEncoder.encode(fname, "GBK").replace("%", "").toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片
     *
     * @return BufferedImage
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * 加载图片
     *
     * @param imageFile 图片文件
     * @throws IOException IOException
     */
    private void load(File imageFile) throws IOException {
        image = ImageIO.read(imageFile);
    }

    /**
     * 获取图片宽度
     *
     * @return int
     */
    public int getImageWidth() {
        return image.getWidth();
    }

    /**
     * 获取图片高度
     *
     * @return int
     */
    public int getImageHeight() {
        return image.getHeight();
    }

    /**
     * 图片裁剪.
     *
     * @param x         x
     * @param y         y
     * @param tarWidth  tarWidth
     * @param tarHeight tarHeight
     * @throws FileNotFoundException FileNotFoundException
     */
    public void cutTo(int x, int y, int tarWidth, int tarHeight) throws FileNotFoundException {
        if (image == null) {
            throw new FileNotFoundException(
                    "图片文件没有被加载，请重新执行加载操作！");
        }
        // 得到源图宽
        int iSrcWidth = getImageWidth();
        // 得到源图长
        int iSrcHeight = getImageHeight();

        //如果原图高度小于目标图片高度则使用目标图片高度
        if (iSrcWidth < tarWidth + x) {
            tarWidth = iSrcWidth - x;
        }
        if (iSrcHeight < tarHeight + y) {
            tarHeight = iSrcHeight - y;
        }
        if (tarWidth < 1 || tarHeight < 1) {
            throw new RuntimeException("截图尺寸过小");
        }
        // 剪裁
        this.image = image.getSubimage(x, y, tarWidth, tarHeight);

    }

    /**
     * 图片缩放 不生成新的图片
     *
     * @param tarWidth  缩放宽度
     * @param tarHeight 缩放高度
     */
    private void zoomTo(int tarWidth, int tarHeight) {
        // 缩放图像
        BufferedImage tarImage = new BufferedImage(tarWidth, tarHeight, BufferedImage.TYPE_INT_RGB);
        Image image = this.image.getScaledInstance(tarWidth, tarHeight, Image.SCALE_SMOOTH);
        Graphics g = tarImage.getGraphics();
        // 绘制目标图
        g.drawImage(image, 0, 0, null);
        g.dispose();
        this.image = tarImage;
    }

    /**
     * 保存
     *
     * @param fileName   文件名称
     * @param formatName 格式名称
     * @throws IOException IOException
     */
    public void save(String fileName, String formatName) throws IOException {
        // 写文件
        FileOutputStream out = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // 输出到bos
            ImageIO.write(this.image, formatName, bos);
            out = new FileOutputStream(fileName);
            // 写文件
            out.write(bos.toByteArray());
        } catch (IOException e) {
            log.error("----->ImageUtil.save :{}", e);
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                log.error("ImageUtil.save 关闭流错误 :{}", ex);
            }
        }
    }

    /**
     * 保存
     *
     * @param newFile    新文件
     * @param formatName 格式名称
     * @throws IOException IOException
     */
    public void save(File newFile, String formatName) throws IOException {
        // 写文件
        FileOutputStream out = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // 输出到bos
            ImageIO.write(this.image, formatName, bos);
            out = new FileOutputStream(newFile);
            // 写文件
            out.write(bos.toByteArray());
        } catch (IOException e) {
            log.error("----->ImageUtil.save " + e.getMessage());
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                log.error("ImageUtil.save  关闭流错误:{}" + ex.getMessage());
            }
        }
    }

    /**
     * 删除临时文件 - 单个
     *
     * @param filePath 文件绝对路径
     */
    private void removeTmpFile(String filePath) {
        if (Strings.isNullOrEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}
