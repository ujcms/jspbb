package com.jspbb.util.image;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ThumbnailatorHandler implements ImageHandler {
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
            BufferedImage buff = ImageIO.read(new File(src));
            int origWidth = buff.getWidth();
            int origHeight = buff.getHeight();
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
            Thumbnails.of(buff).sourceRegion(x, y, width, height).scale(1).toFile(new File(dest));
            return true;
        } catch (IOException e) {
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
        try {
            BufferedImage buff = ImageIO.read(new File(src));
            if ((width <= 0 || buff.getWidth() <= width) && (height <= 0 || buff.getHeight() <= height)) {
                return false;
            }
            Thumbnails.of(buff).size(width, height).toFile(dest);
            return true;
        } catch (IOException e) {
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
        Position pos;
        switch (position) {
            case 1:
                pos = Positions.TOP_LEFT;
                break;
            case 2:
                pos = Positions.TOP_CENTER;
                break;
            case 3:
                pos = Positions.TOP_RIGHT;
                break;
            case 4:
                pos = Positions.CENTER_LEFT;
                break;
            case 5:
                pos = Positions.CENTER;
                break;
            case 6:
                pos = Positions.CENTER_RIGHT;
                break;
            case 7:
                pos = Positions.BOTTOM_LEFT;
                break;
            case 8:
                pos = Positions.BOTTOM_CENTER;
                break;
            case 9:
                pos = Positions.BOTTOM_RIGHT;
                break;
            default:
                pos = Positions.CENTER;
        }
        float opacity = ((float) dissolve) / 100f;
        try {
            BufferedImage buff = ImageIO.read(new File(src));
            if (buff.getWidth() < minWidth || buff.getHeight() < minHeight) {
                return false;
            }
            Thumbnails.of(buff).watermark(pos, ImageIO.read(new File(overlay)), opacity).toFile(dest);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
