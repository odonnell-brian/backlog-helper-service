package com.brian.backloghelperservice.dagger.module;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class DdbBacklogItemDaoModule {

    @Provides
    @Singleton
    public DynamoDBMapper providerDynamoDbMapper(final Regions region) {
        final String localEndpoint = "http://host.docker.internal:8111";
        final AwsClientBuilder.EndpointConfiguration localEndpointConfig =
                new AwsClientBuilder.EndpointConfiguration(localEndpoint, region.getName());
        final AmazonDynamoDB ddbClient =
                AmazonDynamoDBClientBuilder.standard()
                        .withEndpointConfiguration(localEndpointConfig)
                        .withCredentials(new DefaultAWSCredentialsProviderChain())
                        .build();

        return new DynamoDBMapper(ddbClient, DynamoDBMapperConfig.DEFAULT);
    }

    @Provides
    public Regions provideAwsRegion() {
        return Regions.US_WEST_2;
    }

}
