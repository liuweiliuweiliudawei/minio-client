/*
 * Copyright Jordan LEFEBURE © 2019.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.jlefebure.spring.boot.minio;


import com.jlefebure.spring.boot.minio.config.MinioConfigurationProperties;
import com.jlefebure.spring.boot.minio.config.WatermarkConfiguration;
import com.jlefebure.spring.boot.minio.enums.Image;
import com.jlefebure.spring.boot.minio.util.ImageUtil;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.ServerSideEncryption;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Service;
import org.xmlpull.v1.XmlPullParserException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * Service class to interact with Minio bucket. This class is register as a bean and use the properties defined in {@link MinioConfigurationProperties}.
 * All methods return an {@link com.jlefebure.spring.boot.minio.MinioException} which wrap the Minio SDK exception.
 * The bucket name is provided with the one defined in the configuration properties.
 *
 * @author Jordan LEFEBURE
 *
 *
 * This service adapetd with minio sdk 7.0.x
 * @author Mostafa Jalambadani
 */
@Service
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfigurationProperties configurationProperties;
    private final WatermarkConfiguration conf;

    public MinioService(MinioClient minioClient, MinioConfigurationProperties configurationProperties, WatermarkConfiguration conf) {
        this.minioClient = minioClient;
        this.configurationProperties = configurationProperties;
        this.conf = conf;
    }

    public List<Item> list() {
        Iterable<Result<Item>> myObjects = this.minioClient.listObjects(this.configurationProperties.getBucket(), "", false);
        return this.getItems(myObjects);
    }

    public List<Item> fullList() throws MinioException {
        try {
            Iterable<Result<Item>> myObjects = this.minioClient.listObjects(this.configurationProperties.getBucket());
            return this.getItems(myObjects);
        } catch (XmlPullParserException var2) {
            throw new MinioException("Error while fetching files in Minio", var2);
        }
    }

    public List<Item> list(Path path) {
        Iterable<Result<Item>> myObjects = this.minioClient.listObjects(this.configurationProperties.getBucket(), path.toString(), false);
        return this.getItems(myObjects);
    }

    public List<Item> getFullList(Path path) throws MinioException {
        try {
            Iterable<Result<Item>> myObjects = this.minioClient.listObjects(this.configurationProperties.getBucket(), path.toString());
            return this.getItems(myObjects);
        } catch (XmlPullParserException var3) {
            throw new MinioException("Error while fetching files in Minio", var3);
        }
    }

    private List<Item> getItems(Iterable<Result<Item>> myObjects) {
        return (List)StreamSupport.stream(myObjects.spliterator(), true).map((itemResult) -> {
            try {
                return (Item)itemResult.get();
            } catch (NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException | InternalException | InvalidBucketNameException var2) {
                throw new MinioFetchException("Error while parsing list of objects", var2);
            }
        }).collect(Collectors.toList());
    }

    public InputStream get(Path path) throws MinioException {
        try {
            return this.minioClient.getObject(this.configurationProperties.getBucket(), path.toString());
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException | InvalidResponseException | XmlPullParserException var3) {
            throw new MinioException("Error while fetching files in Minio", var3);
        }
    }

    public ObjectStat getMetadata(Path path) throws MinioException {
        try {
            return this.minioClient.statObject(this.configurationProperties.getBucket(), path.toString());
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException | InvalidResponseException | XmlPullParserException var3) {
            throw new MinioException("Error while fetching files in Minio", var3);
        }
    }

    public Map<Path, ObjectStat> getMetadata(Iterable<Path> paths) {
        return (Map)StreamSupport.stream(paths.spliterator(), false).map((path) -> {
            try {
                return new AbstractMap.SimpleEntry(path, this.minioClient.statObject(this.configurationProperties.getBucket(), path.toString()));
            } catch (NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException | InternalException | InvalidResponseException | InvalidArgumentException | InvalidBucketNameException var3) {
                throw new MinioFetchException("Error while parsing list of objects", var3);
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void getAndSave(Path source, String fileName) throws MinioException {
        try {
            this.minioClient.getObject(this.configurationProperties.getBucket(), source.toString(), fileName);
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException | InvalidResponseException | XmlPullParserException var4) {
            throw new MinioException("Error while fetching files in Minio", var4);
        }
    }

    public void upload(Path source, InputStream file, ContentType contentType) throws MinioException {
        this.upload(source, file, (ContentType)contentType, (Map)null);
    }

    public void upload(Path source, InputStream file, ContentType contentType, Map<String, String> headers) throws MinioException {
        this.upload(source, file, contentType.getMimeType(), (Map)null, (Image)null);
    }

    public void upload(Path source, InputStream file, String contentType) throws MinioException {
        this.upload(source, file, contentType, (Map)null, (Image)null);
    }

    public void upload(Path source, InputStream file, String contentType, Map<String, String> headers) throws MinioException {
        this.upload(source, file, contentType, headers, (Image)null);
    }

    public void upload(Path source, InputStream file, String contentType, Image image) throws MinioException {
        this.upload(source, file, contentType, (Map)null, image);
    }

    public void upload(Path source, InputStream file, String contentType, Map<String, String> headers, Image image) throws MinioException {
        try {
            ImageUtil util = new ImageUtil(this.conf);
            if (util.isImage(source.getFileName().toString())) {
                BufferedImage bufferedImage = ImageIO.read(file);
                if (image != null) {
                    if (image.isAll()) {
                        bufferedImage = util.setWatermark(util.compress(bufferedImage));
                    }

                    if (image.isWatermark()) {
                        bufferedImage = util.setWatermark(bufferedImage);
                    }

                    if (image.isCompression()) {
                        bufferedImage = util.compress(bufferedImage);
                    }

                    file = util.getInputStream(bufferedImage, util.getFileExtention(source.getFileName().toString()));
                }
            }

            this.minioClient.putObject(this.configurationProperties.getBucket(), source.toString(), file, (long)file.available(), headers, (ServerSideEncryption)null, contentType);
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException | InvalidResponseException | XmlPullParserException var8) {
            throw new MinioException("Error while fetching files in Minio", var8);
        }
    }

    public void remove(Path source) throws MinioException {
        try {
            this.minioClient.removeObject(this.configurationProperties.getBucket(), source.toString());
        } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException | InvalidResponseException | XmlPullParserException var3) {
            throw new MinioException("Error while fetching files in Minio", var3);
        }
    }

    public String getURL(Path source, Integer expires) throws MinioException {
        try {
            return this.minioClient.presignedGetObject(this.configurationProperties.getBucket(), source.toString(), expires);
        } catch (NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException | InternalException | InvalidExpiresRangeException | InvalidResponseException | InvalidBucketNameException var4) {
            throw new MinioException("Error while fetching files in Minio", var4);
        }
    }

    public String getURL(String objectName) throws MinioException {
        try {
            return this.minioClient.getObjectUrl(this.configurationProperties.getBucket(), objectName);
        } catch (NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException | InternalException | InvalidResponseException | InvalidBucketNameException var3) {
            throw new MinioException("Error while fetching files in Minio", var3);
        }
    }
}
