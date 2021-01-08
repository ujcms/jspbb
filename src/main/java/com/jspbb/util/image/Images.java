package com.jspbb.util.image;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * 图片工具类
 *
 * @author liufang
 */
public class Images {
    private static final Logger logger = LoggerFactory.getLogger(Images.class);
    // 目前浏览器支持的图片有：jpg(image/jpeg),gif(image/gif),png(image/png),bmp(image/png),svg(image/svg+xml),webp(image/webp),ico(image/x-icon)
    // 以后有可能会支持谷歌超微型WebP图像格式，目前IE、Edge、火狐都不支持。
    // JDK支持的读取格式 ImageIO.getReaderFormatNames();
    // JDK支持的写入格式 ImageIO.getWriterFormatNames();
    // JDK8支持的格式有jpg, jpeg, png, gif, bmp, wbmp
    /**
     * 图片扩展名
     */
    public static final String[] IMAGE_EXTENSIONS = new String[]{"jpeg", "jpg", "png", "gif", "bmp"};

    /**
     * 是否是图片扩展名
     *
     * @param extension
     * @return
     */
    public static final boolean isImageExtension(String extension) {
        if (StringUtils.isBlank(extension)) {
            return false;
        }
        for (String imageExtension : IMAGE_EXTENSIONS) {
            if (StringUtils.equalsIgnoreCase(imageExtension, extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取图片格式
     * <p>
     * Windows10 JDK8 ImageReader 支持的格式有：
     * <p>
     * JPG, jpg, JPEG, jpeg, PNG, png, GIF, gif, BMP, bmp, WBMP, wbmp
     *
     * @param file 图片文件
     * @return 返回小写字母的图片格式，jpeg和jpg统一返回jpg。如果不支持该格式或文件不存在，则返回null。
     */
    public static String getFormatName(@NotNull File file) {
        // 文件不存在则返回 null
        if (!file.exists()) {
            return null;
        }
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
            if (!it.hasNext()) {
                return null;
            }
            String formatName = it.next().getFormatName().toLowerCase();
            // jpeg和jpg是同一个东西。图片格式通常用作后缀名，所以返回更常用的jpg。
            if ("jpeg".equals(formatName)) {
                formatName = "jpg";
            }
            return formatName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
