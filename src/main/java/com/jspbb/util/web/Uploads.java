package com.jspbb.util.web;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * 上传工具类
 */
public abstract class Uploads {
    /**
     * 使用ImageReader判断图片类型
     * <p>
     * 目前浏览器支持的图片有：jpg(image/jpeg),gif(image/gif),png(image/png),bmp(image/png),svg(image/svg+xml),webp(image/webp),ico(image/x-icon)
     * <p>
     * 以后有可能会支持谷歌超微型WebP图像格式，目前IE、Edge、火狐都不支持。
     * <p>
     * JDK支持的读取格式 ImageIO.getReaderFormatNames();
     * <p>
     * JDK支持的写入格式 ImageIO.getWriterFormatNames();
     * <p>
     * JDK8支持的格式有jpg, jpeg, png, gif, bmp, wbmp
     *
     * @param filename 图片地址
     * @return 图片类型
     */
    public static String getFormatName(String filename) {
        try {
            try (ImageInputStream iis = ImageIO.createImageInputStream(new File(filename));) {
                Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
                if (it.hasNext()) {
                    return it.next().getFormatName();
                } else {
                    return null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取缩略图文件名。在原文件名上加前缀 m_ ，如 /path/abc.jpg 则为 /path/m_abc.jpg
     *
     * @param filename 图片地址。
     * @return
     */
    public static String getThumbnailName(String filename) {
        if (StringUtils.isBlank(filename)) {
            return filename;
        }
        File file = new File(filename);
        return new File(file.getParent(), "m_" + file.getName()).getAbsolutePath();
    }

    /**
     * 获得上传文件名。格式为 /yyyyMM/yyyyMMddHHmmssSSS_5Random.extension
     *
     * @param extension 文件扩展名
     * @return
     */
    public static String getFilename(String extension) {
        String filename = geteDateFormat().format(new Date()) + RandomStringUtils.random(5, '0', 'Z', true, true).toLowerCase();
        if (StringUtils.isNotBlank(extension)) {
            filename += "." + extension;
        }
        return filename;
    }

    public static SimpleDateFormat geteDateFormat() {
        return new SimpleDateFormat("/yyyyMM/yyyyMMddHHmmssSSS_");
    }
}
