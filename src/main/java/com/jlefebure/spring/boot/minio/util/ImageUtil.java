package com.jlefebure.spring.boot.minio.util;

import com.jlefebure.spring.boot.minio.config.WatermarkConfiguration;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class ImageUtil {
    private final WatermarkConfiguration conf;

    public ImageUtil(WatermarkConfiguration conf) {
        this.conf = conf;
    }

    public BufferedImage compress(BufferedImage image) throws IOException {
        Thumbnails.Builder<BufferedImage> imageBuilder = Thumbnails.of(new BufferedImage[]{image}).outputQuality(this.conf.getImageRatio());
        return image.getWidth() > this.conf.getImageWidth() ? imageBuilder.width(this.conf.getImageWidth()).asBufferedImage() : imageBuilder.scale(1.0D).asBufferedImage();
    }

    public BufferedImage setWatermark(BufferedImage image) throws IOException {
        return Thumbnails.of(new BufferedImage[]{image}).outputQuality(this.conf.getImageRatio()).scale(1.0D).watermark(Positions.BOTTOM_RIGHT, this.createWatermark(this.conf.getText(), image.getWidth(), image.getHeight()), this.conf.getAlpha()).asBufferedImage();
    }

    public boolean isImage(String fileName) {
        String[] imageExtension = new String[]{"jpeg", "jpg", "gif", "bmp", "png"};
        String[] var3 = imageExtension;
        int var4 = imageExtension.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String e = var3[var5];
            if (this.getFileExtention(fileName).toLowerCase().equals(e)) {
                return true;
            }
        }

        return false;
    }

    public String getFileExtention(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extension;
    }

    public InputStream getInputStream(BufferedImage image, String readImageFormat) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, readImageFormat, os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        os.close();
        return is;
    }

    public BufferedImage createWatermark(String text, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, 1);
        Graphics2D g = image.createGraphics();
        image = g.getDeviceConfiguration().createCompatibleImage(width, height, 3);
        g.dispose();
        g = image.createGraphics();
        AttributedString ats = new AttributedString(text);
        ats.addAttribute(TextAttribute.FONT, this.conf.getFont(), 0, text.length());
        AttributedCharacterIterator iter = ats.getIterator();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.rotate(Math.toRadians(-30.0D));
        g.setColor(this.conf.getColor());
        g.setFont(this.conf.getFont());
        g.setComposite(AlphaComposite.getInstance(10, this.conf.getAlpha()));
        g.setComposite(AlphaComposite.getInstance(3));
        int x = -width / 2;
        int y = -height / 2;
        int[] arr = this.getWidthAndHeight(text, this.conf.getFont());
        int markWidth = arr[0];

        for(int markHeight = arr[1]; (double)x < (double)width * 1.5D; x += markWidth + this.conf.getxMove()) {
            for(y = -height / 2; (double)y < (double)height * 1.5D; y += markHeight + this.conf.getyMove()) {
                g.drawString(text, x, y);
            }
        }

        g.dispose();
        return image;
    }

    private int[] getWidthAndHeight(String text, Font font) {
        Rectangle2D r = font.getStringBounds(text, new FontRenderContext(AffineTransform.getScaleInstance(1.0D, 1.0D), false, false));
        int unitHeight = (int)Math.floor(r.getHeight());
        int width = (int)Math.round(r.getWidth()) + 1;
        int height = unitHeight + 3;
        return new int[]{width, height};
    }
}
