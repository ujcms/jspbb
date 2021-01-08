package com.jspbb.util.image;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.im4java.core.*;
import org.im4java.process.ArrayListOutputConsumer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * http://www.graphicsmagick.org/GraphicsMagick.html
 * http://www.imagemagick.org/script/command-line-options.php
 */
public class ImageMagickHandler implements ImageHandler {
    public ImageMagickHandler() {
    }

    public ImageMagickHandler(boolean userGM) {
        this.useGM = userGM;
    }

    /**
     * @see ImageHandler#crop(String, String, int, int, int, int)
     */
    @Override
    public boolean crop(String src, String dest, int x, int y, int width, int height) {
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (width <= 0) {
            throw new IllegalArgumentException("width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be > 0");
        }
        try {
            ImageInfo ii = getImageInfo(src);
            int origWidth = ii.getWidth();
            int origHeight = ii.getHeight();
            if (x + width > origWidth) {
                if (width > origWidth) {
                    width = origWidth;
                    x = 0;
                } else {
                    x = origWidth - width;
                }
            }
            if (y + height > origHeight) {
                if (height > origHeight) {
                    height = origHeight;
                    y = 0;
                } else {
                    y = origHeight - height;
                }
            }
            // 宽高与原图一致，不做处理
            if (width == origWidth && height == origHeight) {
                return false;
            }
            File destFile = new File(dest);
            FileUtils.forceMkdir(destFile.getParentFile());
            IMOperation op = new IMOperation();
            op.addImage(src);
            // 去除Exif信息
            // op.profile("*");
            op.crop(width, height, x, y);
            op.addImage(dest);
            ConvertCmd convertCmd = getConvertCmd();
            convertCmd.run(op);
            return true;
        } catch (Exception e) {
            FileUtils.deleteQuietly(new File(dest));
            throw new RuntimeException(e);
        }
    }

    /**
     * @see ImageHandler#resize(String, String, Integer, Integer)
     */
    @Override
    public boolean resize(String src, String dest, Integer width, Integer height) {
        if (width == null) {
            width = 0;
        }
        if (height == null) {
            height = 0;
        }
        if (width <= 0 && height <= 0) {
            return false;
        }
        ImageInfo ii = getImageInfo(src);
        if ((width <= 0 || ii.getWidth() <= width) && (height <= 0 || ii.getHeight() <= height)) {
            return false;
        }
        try {
            File destFile = new File(dest);
            FileUtils.forceMkdir(destFile.getParentFile());
            IMOperation op = new IMOperation();
            op.addImage(src);
            // 去除Exif信息
            // op.profile("*");
            // 按宽高生成图片（不按比例）
            // op.resize(width, height, '!');
            // 按比例只缩小不放大
            op.resize(width, height, '>');
            op.addImage(dest);
            ConvertCmd convertCmd = getConvertCmd();
            convertCmd.run(op);
            return true;
        } catch (Exception e) {
            FileUtils.deleteQuietly(new File(dest));
            throw new RuntimeException(e);
        }
    }

    /**
     * @see ImageHandler#watermark(String, String, String, Integer, Integer, Integer, Integer)
     */
    @Override
    public boolean watermark(String src, String dest, String overlay, Integer position, Integer dissolve, Integer minWidth, Integer minHeight) {
        if (position == null || position < 1 || position > 9) {
            position = 5;
        }
        if (dissolve == null || dissolve < 0 || dissolve > 100) {
            dissolve = 50;
        }
        if (minWidth == null) {
            minWidth = 0;
        }
        if (minHeight == null) {
            minHeight = 0;
        }
        Gravity gravity = Gravity.values()[position - 1];
        ImageInfo ii = getImageInfo(src);
        if (ii.getWidth() < minWidth || ii.getHeight() < minHeight) {
            return false;
        }
        watermark(src, dest, overlay, gravity, null, null, dissolve);
        return true;
    }

    /**
     * 图片水印
     *
     * @param src      原图片
     * @param dest     目标图片
     * @param overlay  水印图片
     * @param gravity  水印位置
     * @param x        偏移位置x
     * @param y        偏移位置y
     * @param dissolve 透明度
     */
    public void watermark(String src, String dest, String overlay, Gravity gravity, Integer x, Integer y, Integer dissolve) {
        try {
            File destFile = new File(dest);
            FileUtils.forceMkdir(destFile.getParentFile());
            IMOperation op = new IMOperation();
            // 水印位置。NorthWest, North, NorthEast, West, Center, East, SouthWest, South, SouthEast. 默认为左上角：NorthWest。
            if (gravity != null) {
                op.gravity(gravity.name());
            }
            if (x != null || y != null) {
                op.geometry(null, null, x, y);
            }
            // 0-100透明度。0：完全透明，100：完全不透明。默认100。
            if (dissolve != null) {
                op.dissolve(dissolve);
            }
            op.addImage(overlay);
            op.addImage(src);
            op.addImage(dest);
            CompositeCmd compositeCmd = getCompositeCmd();
            compositeCmd.run(op);
        } catch (Exception e) {
            FileUtils.deleteQuietly(new File(dest));
            throw new RuntimeException(e);
        }
    }

    /**
     * 旋转图片
     *
     * @param src
     * @param dest
     * @param degree
     * @return
     */
    public boolean rotate(String src, String dest, Double degree) {
        // 度数为null，不处理
        if (degree == null) {
            return false;
        }
        // 一周是360度，不超过这个范围
        degree = degree % 360;
        if (degree < 0) {
            degree = 360 + degree;
        }
        // 度数0，不旋转，不处理
        if (degree == 0) {
            return false;
        }
        try {
            File destFile = new File(dest);
            FileUtils.forceMkdir(destFile.getParentFile());
            IMOperation op = new IMOperation();
            op.addImage(src);
            op.rotate(degree);
            op.addImage(dest);
            ConvertCmd convertCmd = getConvertCmd();
            convertCmd.run(op);
            return true;
        } catch (Exception e) {
            FileUtils.deleteQuietly(new File(dest));
            throw new RuntimeException(e);
        }
    }

    /**
     * 上下（垂直）翻转图片
     *
     * @param src
     * @param dest
     */
    public void flip(String src, String dest) {
        try {
            File destFile = new File(dest);
            FileUtils.forceMkdir(destFile.getParentFile());
            IMOperation op = new IMOperation();
            op.addImage(src);
            // 上下翻转
            op.flip();
            op.addImage(dest);
            ConvertCmd convertCmd = getConvertCmd();
            convertCmd.run(op);
        } catch (Exception e) {
            FileUtils.deleteQuietly(new File(dest));
            throw new RuntimeException(e);
        }

    }

    /**
     * 左右（水平）翻转图片
     *
     * @param src
     * @param dest
     */
    public void flop(String src, String dest) {
        try {
            File destFile = new File(dest);
            FileUtils.forceMkdir(destFile.getParentFile());
            IMOperation op = new IMOperation();
            op.addImage(src);
            // 左右翻转
            op.flop();
            op.addImage(dest);
            ConvertCmd convertCmd = getConvertCmd();
            convertCmd.run(op);
        } catch (Exception e) {
            FileUtils.deleteQuietly(new File(dest));
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取图片信息
     *
     * @param pathname 图片路径
     * @return
     */
    public ImageInfo getImageInfo(String pathname) {
        IMOperation op = new IMOperation();
        // http://www.graphicsmagick.org/identify.html
        // 主要是宽高，其他的参数没有太大的作用，反而有可能因为ImageMagick升级，接口规则变化导致错误
        // op.format("%w,%h,%d,%f");
        op.format("%w,%h,%m");
        op.addImage(pathname);
        IdentifyCmd identifyCmd = getIdentifyCmd();
        ArrayListOutputConsumer output = new ArrayListOutputConsumer();
        identifyCmd.setOutputConsumer(output);
        try {
            identifyCmd.run(op);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> cmdOutput = output.getOutput();
        if (cmdOutput.size() != 1) {
            return null;
        }
        String line = cmdOutput.get(0);
        String[] arr = line.split(",");
        return new ImageInfo(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), arr[2]);
    }

    public ConvertCmd getConvertCmd() {
        ConvertCmd cmd = new ConvertCmd(useGM);
        if (StringUtils.isNotBlank(searchPath)) {
            cmd.setSearchPath(searchPath);
        }
        return cmd;
    }

    public CompositeCmd getCompositeCmd() {
        CompositeCmd cmd = new CompositeCmd(useGM);
        if (StringUtils.isNotBlank(searchPath)) {
            cmd.setSearchPath(searchPath);
        }
        return cmd;
    }

    public IdentifyCmd getIdentifyCmd() {
        IdentifyCmd cmd = new IdentifyCmd(useGM);
        if (StringUtils.isNotBlank(searchPath)) {
            cmd.setSearchPath(searchPath);
        }
        return cmd;
    }

    private boolean useGM = false;
    private String searchPath = null;

    public boolean isUseGM() {
        return useGM;
    }

    public void setUseGM(boolean useGM) {
        this.useGM = useGM;
    }

    public String getSearchPath() {
        return searchPath;
    }

    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

    public enum Gravity {
        /**
         * 左上
         */
        NorthWest,
        /**
         * 上
         */
        North,
        /**
         * 右上
         */
        NorthEast,
        /**
         * 左
         */
        West,
        /**
         * 中
         */
        Center,
        /**
         * 右
         */
        East,
        /**
         * 左下
         */
        SouthWest,
        /**
         * 下
         */
        South,
        /**
         * 右下
         */
        SouthEast
    }

    public static class ImageInfo {
        public ImageInfo() {
        }

        public ImageInfo(int width, int height, String formatType) {
            this.width = width;
            this.height = height;
            this.formatType = formatType;
        }

        private int width;
        private int height;
        private String formatType;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getFormatType() {
            return formatType;
        }

        public void setFormatType(String formatType) {
            this.formatType = formatType;
        }
    }
}
