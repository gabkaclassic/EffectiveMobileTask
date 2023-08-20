package org.gaba.JavaTechTask.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AWSService {

    public static final String IMAGES_TYPE = "image";
    private final AmazonS3 awsClient;

    private final Bucket imagesBucket;

    public AWSService(@Value("${aws.secret.key}") String secretKey, @Value("${aws.access.key}") String accessKey, @Value("${aws.bucket}") String bucketName) {

        var credentials = new BasicAWSCredentials(accessKey, secretKey);
        awsClient = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_WEST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        imagesBucket = awsClient.doesBucketExist(bucketName) ?
                awsClient.listBuckets().stream().filter(bucket -> bucket.getName().equals(bucketName)).findFirst().get()
                : awsClient.createBucket(bucketName);
    }


    public Mono<List<String>> saveImages(List<FilePart> files) {


        return Flux.fromStream(files.stream()).map(file -> {
            if (file != null && file.headers().getContentType() != null) {

                var metadata = new ObjectMetadata();
                metadata.setContentType(file.headers().getContentType().getType());
                var filename = file.filename() + UUID.randomUUID();

                file.content().limitRequest(1)
                        .flatMap(content -> {
                            if (content != null)
                                awsClient.putObject(imagesBucket.getName(), filename, content.asInputStream(), metadata);
                            return Mono.empty();
                        }).subscribe();
            }
            return file.filename();
        }).collectList();
    }
}
