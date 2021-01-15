package com.jlefebure.spring.boot.minio.enums;

public enum Image {
    WATERMARK("watermark"),
    ALL("all"),
    COMPRESSION("compression"),
    NULL("null");

    private String value;

    private Image(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isWatermark() {
        return this.getValue().equals("watermark");
    }

    public boolean isCompression() {
        return this.getValue().equals("compression");
    }

    public boolean isAll() {
        return this.getValue().equals("all");
    }
}
