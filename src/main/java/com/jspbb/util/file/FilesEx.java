package com.jspbb.util.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件工具类
 *
 * @author liufang
 */
public abstract class FilesEx {
    public static String getSize(Long length) {
        if (length == null) {
            return "0 KB";
        }
        if (length <= 1024) {
            return "1 KB";
        }
        long lengthKB = length / 1024;
        // 小于900是为了避免出现999KB 和 0.98M 哪个大的问题
        if (lengthKB < 900) {
            return lengthKB + " KB";
        }
        DecimalFormat format0 = new DecimalFormat("0");
        DecimalFormat format1 = new DecimalFormat("0.#");
        DecimalFormat format2 = new DecimalFormat("0.##");
        BigDecimal lengthMB = new BigDecimal(length).divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_DOWN);
        if (lengthMB.compareTo(new BigDecimal(10)) < 0) {
            return format2.format(lengthMB) + " MB";
        } else if (lengthMB.compareTo(new BigDecimal(100)) < 0) {
            return format1.format(lengthMB) + " MB";
        } else if (lengthMB.compareTo(new BigDecimal(900)) < 0) {
            return format0.format(lengthMB) + " MB";
        }
        BigDecimal lengthGB = new BigDecimal(length).divide(new BigDecimal(1024 * 1024 * 1024), 2, RoundingMode.HALF_DOWN);
        if (lengthGB.compareTo(new BigDecimal(10)) < 0) {
            return format2.format(lengthGB) + " GB";
        } else if (lengthGB.compareTo(new BigDecimal(100)) < 0) {
            return format1.format(lengthGB) + " GB";
        } else if (lengthGB.compareTo(new BigDecimal(900)) < 0) {
            return format0.format(lengthGB) + " GB";
        }
        BigDecimal lengthTB = new BigDecimal(length).divide(new BigDecimal(1024 * 1024 * 1024), 2, RoundingMode.HALF_DOWN);
        if (lengthTB.compareTo(new BigDecimal(10)) < 0) {
            return format2.format(lengthTB) + " TB";
        } else if (lengthTB.compareTo(new BigDecimal(100)) < 0) {
            return format1.format(lengthTB) + " TB";
        } else {
            return format0.format(lengthTB) + " TB";
        }
    }

    public static String randomName(String extension) {
        StringBuilder name = new StringBuilder();
        name.append(System.currentTimeMillis());
        String random = RandomStringUtils.random(8, '0', 'Z', true, true).toLowerCase();
        name.append(random);
        if (StringUtils.isNotBlank(extension)) {
            name.append(".");
            name.append(extension);
        }
        return name.toString();
    }

    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final String UID = UUID.randomUUID().toString().replace('-', '_');

    private static String getUniqueId() {
        final int limit = 2000000000;
        int current = COUNTER.getAndIncrement();
        String id = Integer.toString(current);
        if (current < limit) {
            id = ("000000000" + id).substring(id.length());
        }
        return id;
    }

    /**
     * 获取临时文件，扩展名为.tmp
     *
     * @return
     */
    public static File getTempFile() {
        return getTempFile(null);
    }

    /**
     * 获取临时文件。并创建必须的父文件夹。
     *
     * @param ext 为null则默认为.tmp；如不需要扩展名可传空串""。
     * @return
     */
    public static File getTempFile(String ext) {
        if (ext == null) {
            ext = "tmp";
        }
        String suffix = StringUtils.isNotBlank(ext) ? "." + ext : "";
        String tempFileName = UID + getUniqueId() + suffix;
        File tempFile = new File(FileUtils.getTempDirectoryPath(), tempFileName);
        tempFile.getParentFile().mkdirs();
        return tempFile;
    }

    /**
     * Iterates over a base name and returns the first non-existent file.<br />
     * This method extracts a file's base name, iterates over it until the first non-existent appearance with
     * <code>basename(n).ext</code>. Where n is a positive integer starting from one.
     *
     * @param file base file
     * @return first non-existent file
     */
    public static File getUniqueFile(final File file) {
        if (!file.exists()) {
            return file;
        }
        File tmpFile = new File(file.getAbsolutePath());
        File parentDir = tmpFile.getParentFile();
        int count = 1;
        String extension = FilenameUtils.getExtension(tmpFile.getName());
        String baseName = FilenameUtils.getBaseName(tmpFile.getName());
        String suffix = StringUtils.isNotBlank(extension) ? "." + extension : "";
        do {
            tmpFile = new File(parentDir, baseName + "(" + count++ + ")" + suffix);
        } while (tmpFile.exists());
        return tmpFile;
    }

    /**
     * 还可以参考 {@link FileUtils#copyURLToFile(URL, File)} {@link FileUtils#copyURLToFile(URL, File, int, int)}
     *
     * @param url         要获取的URL地址
     * @param contentType url 响应的 Content Type 需包含该字符串。为 null 则不限制。
     * @return 通过 url 获取的文件
     * @throws IOException IO 异常
     */
    public static File getFileFromUrl(URL url, String contentType) throws IOException {
        File temp = getTempFile();
        getFileFromUrl(url, temp, contentType);
        if (temp.exists()) return temp;
        return null;
    }

    /**
     * 还可以参考 {@link FileUtils#copyURLToFile(URL, File)} {@link FileUtils#copyURLToFile(URL, File, int, int)}
     *
     * @param url         要获取的URL地址
     * @param file        要保存的文件
     * @param contentType url 响应的 Content Type 需包含该字符串。为 null 则不限制。
     * @throws IOException IO 异常
     */
    public static void getFileFromUrl(URL url, File file, String contentType) throws IOException {
        // 只支持 http 和 https 协议
        String protocol = url.getProtocol();
        if (!"http".equals(protocol) && !"https".equals(protocol)) return;
        // 只允许 默认、80、443 端口
        int port = url.getPort();
        if (port != -1 && port != 80 && port != 443) return;
        // 不访问本机
        String host = url.getHost();
        if (InetAddress.getByName(host).isSiteLocalAddress()) return;

        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() != 200) return;
        // ContentType 需包含自定字符串
        if (StringUtils.isNotBlank(contentType) && !conn.getContentType().contains(contentType)) return;
        try (InputStream input = conn.getInputStream();
             OutputStream output = new FileOutputStream(file);) {
            IOUtils.copyLarge(input, output);
        }
    }
}
