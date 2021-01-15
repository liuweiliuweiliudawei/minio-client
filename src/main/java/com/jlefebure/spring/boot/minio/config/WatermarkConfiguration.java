package com.jlefebure.spring.boot.minio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.awt.Color;
import java.awt.Font;

@Configuration
@ConfigurationProperties("spring.minio.watermark")
public class WatermarkConfiguration {
    private float imageRatio = 0.1F;
    private int imageWidth = 800;
    private float alpha = 0.3F;
    private int fontSize = 36;
    private String fontName = "PingFang SC Regular";
    private Font font;
    private int colorRed;
    private int colorGreen;
    private int colorBlue;
    private Color color;
    private String text;
    private int xMove;
    private int yMove;

    public WatermarkConfiguration() {
        this.font = new Font(this.fontName, 0, this.fontSize);
        this.colorRed = 111;
        this.colorGreen = 111;
        this.colorBlue = 111;
        this.color = new Color(this.colorRed, this.colorGreen, this.colorBlue);
        this.text = "智慧式-伴置车";
        this.xMove = 80;
        this.yMove = 80;
    }

    public float getImageRatio() {
        return this.imageRatio;
    }

    public void setImageRatio(float imageRatio) {
        this.imageRatio = imageRatio;
    }

    public int getImageWidth() {
        return this.imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Font getFont() {
        return this.font;
    }

    public int getColorRed() {
        return this.colorRed;
    }

    public void setColorRed(int colorRed) {
        this.colorRed = colorRed;
    }

    public int getColorGreen() {
        return this.colorGreen;
    }

    public void setColorGreen(int colorGreen) {
        this.colorGreen = colorGreen;
    }

    public int getColorBlue() {
        return this.colorBlue;
    }

    public void setColorBlue(int colorBlue) {
        this.colorBlue = colorBlue;
    }

    public Color getColor() {
        return this.color;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getxMove() {
        return this.xMove;
    }

    public void setxMove(int xMove) {
        this.xMove = xMove;
    }

    public int getyMove() {
        return this.yMove;
    }

    public void setyMove(int yMove) {
        this.yMove = yMove;
    }
}
