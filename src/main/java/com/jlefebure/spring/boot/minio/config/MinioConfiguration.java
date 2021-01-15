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

package com.jlefebure.spring.boot.minio.config;

import com.jlefebure.spring.boot.minio.MinioException;
import io.minio.MinioClient;
import io.minio.errors.*;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(MinioConfigurationProperties.class)
@ComponentScan("com.jlefebure.spring.boot.minio")
public class MinioConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioConfiguration.class);
    @Autowired
    private MinioConfigurationProperties minioConfigurationProperties;

    public MinioConfiguration() {
    }

    @Bean
    public MinioClient minioClient() throws InvalidEndpointException, InvalidPortException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidResponseException, MinioException {
        MinioClient minioClient = null;

        try {
            minioClient = new MinioClient(this.minioConfigurationProperties.getUrl(), this.minioConfigurationProperties.getAccessKey(), this.minioConfigurationProperties.getSecretKey(), this.minioConfigurationProperties.isSecure());
            minioClient.setTimeout(this.minioConfigurationProperties.getConnectTimeout().toMillis(), this.minioConfigurationProperties.getWriteTimeout().toMillis(), this.minioConfigurationProperties.getReadTimeout().toMillis());
        } catch (InvalidPortException | InvalidEndpointException var6) {
            LOGGER.error("Error while connecting to Minio", var6);
            throw var6;
        }

        if (this.minioConfigurationProperties.isCheckBucket()) {
            try {
                LOGGER.debug("Checking if bucket {} exists", this.minioConfigurationProperties.getBucket());
                boolean b = minioClient.bucketExists(this.minioConfigurationProperties.getBucket());
                if (!b) {
                    if (!this.minioConfigurationProperties.isCreateBucket()) {
                        throw new InvalidBucketNameException(this.minioConfigurationProperties.getBucket(), "Bucket does not exists");
                    }

                    try {
                        minioClient.makeBucket(this.minioConfigurationProperties.getBucket());
                    } catch (RegionConflictException var4) {
                        throw new MinioException("Cannot create bucket", var4);
                    }
                }
            } catch (NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException | InternalException | InvalidResponseException | MinioException | InvalidBucketNameException var5) {
                LOGGER.error("Error while checking bucket", var5);
                throw var5;
            }
        }

        return minioClient;
    }

}
