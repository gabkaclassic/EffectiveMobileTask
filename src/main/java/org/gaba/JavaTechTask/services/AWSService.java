package org.gaba.JavaTechTask.services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AWSService {

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
        System.out.println(imagesBucket);
    }


}
