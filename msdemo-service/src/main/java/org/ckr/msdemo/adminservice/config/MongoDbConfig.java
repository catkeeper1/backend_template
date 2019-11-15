package org.ckr.msdemo.adminservice.config;

import com.mongodb.*;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class MongoDbConfig {

    @Autowired
    MongoDbProperty properties;

    @Bean
    public MongoDbFactory mongoDbFactory() {


        MongoClientSettings mongoClientSetting = MongoClientSettings.builder()
                .applyToConnectionPoolSettings(builder -> {
                    builder.maxWaitTime(properties.getMaxWaitTime(), TimeUnit.MILLISECONDS);
                    builder.maxConnectionIdleTime(properties.getMaxConnectionIdleTime(), TimeUnit.MILLISECONDS);
                    builder.maxConnectionLifeTime(properties.getMaxConnectionLifeTime(), TimeUnit.MILLISECONDS);
                    builder.maxSize(properties.getMaxSize());
                    builder.minSize(properties.getMinSize());
                })
                .applyToServerSettings(builder -> {
                    builder.minHeartbeatFrequency(properties.getMinHeartbeatFrequency(), TimeUnit.MILLISECONDS);
                    builder.heartbeatFrequency(properties.getHeartbeatFrequency(), TimeUnit.MILLISECONDS);

                })
                .applyToSocketSettings(builder -> {
                    builder.connectTimeout(properties.getConnectTimeout(), TimeUnit.MILLISECONDS);

                })
                .applyToSslSettings(builder -> {
                    builder.enabled(properties.getSslEnabled());
                    builder.invalidHostNameAllowed(properties.getSslInvalidHostNameAllowed());
                })
                .applyToClusterSettings(builder -> {
                    List<ServerAddress> serverAddresses = new ArrayList<>();
                    for (String address : properties.getAddress()) {
                        String[] hostAndPort = address.split(":");
                        String host = hostAndPort[0];
                        Integer port = Integer.parseInt(hostAndPort[1]);

                        ServerAddress serverAddress = new ServerAddress(host, port);
                        serverAddresses.add(serverAddress);
                    }

                    builder.hosts(serverAddresses);

                    if(properties.getReplicatedSet() != null) {
                        builder.requiredReplicaSetName(properties.getReplicatedSet());
                    }

                    builder.serverSelectionTimeout(properties.getServerSelectionTimeout(), TimeUnit.MILLISECONDS);
                    builder.localThreshold(properties.getLocalThreshold(), TimeUnit.MILLISECONDS);

                })
                .credential(MongoCredential.createScramSha1Credential(properties.getUsername(),
                        properties.getAuthenticationDatabase(),
                        properties.getPassword().toCharArray()))
                .writeConcern(WriteConcern.W1.withJournal(true))
                .build();

        com.mongodb.client.MongoClient mongoClient = MongoClients.create(mongoClientSetting);

        MongoDbFactory mongoDbFactory = new SimpleMongoClientDbFactory(mongoClient, properties.getDatabase());

        return mongoDbFactory;
    }

    @Bean
    public MongoTransactionManager mongoDbTransactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }


    @ConfigurationProperties(prefix = "app.mongodb")
    public static class MongoDbProperty {
        @NotBlank
        private String database;
        @NotEmpty private List<String> address;
        //private String replicaSet;
        private String username;
        private String password;
        private Integer minSize = 0;
        private Integer maxSize = 100;
        //private Integer threadsAllowedToBlockForConnectionMultiplier = 5;
        private Integer serverSelectionTimeout = 30000;
        private Integer maxWaitTime = 120000;
        private Integer maxConnectionIdleTime = 0;
        private Integer maxConnectionLifeTime = 0;
        private Integer connectTimeout = 10000;
        private String replicatedSet = null;

//        private Boolean socketKeepAlive = false;
        private Boolean sslEnabled = false;
        private Boolean sslInvalidHostNameAllowed = false;

        private Integer heartbeatFrequency = 10000;
        private Integer minHeartbeatFrequency = 500;


        private Integer localThreshold = 15;
        private String authenticationDatabase;

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public List<String> getAddress() {
            return address;
        }

        public void setAddress(List<String> address) {
            this.address = address;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Integer getMinSize() {
            return minSize;
        }

        public void setMinSize(Integer minSize) {
            this.minSize = minSize;
        }

        public Integer getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(Integer maxSize) {
            this.maxSize = maxSize;
        }

        public Integer getServerSelectionTimeout() {
            return serverSelectionTimeout;
        }

        public void setServerSelectionTimeout(Integer serverSelectionTimeout) {
            this.serverSelectionTimeout = serverSelectionTimeout;
        }

        public Integer getMaxWaitTime() {
            return maxWaitTime;
        }

        public void setMaxWaitTime(Integer maxWaitTime) {
            this.maxWaitTime = maxWaitTime;
        }

        public Integer getMaxConnectionIdleTime() {
            return maxConnectionIdleTime;
        }

        public void setMaxConnectionIdleTime(Integer maxConnectionIdleTime) {
            this.maxConnectionIdleTime = maxConnectionIdleTime;
        }

        public Integer getMaxConnectionLifeTime() {
            return maxConnectionLifeTime;
        }

        public void setMaxConnectionLifeTime(Integer maxConnectionLifeTime) {
            this.maxConnectionLifeTime = maxConnectionLifeTime;
        }

        public Integer getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public String getReplicatedSet() {
            return replicatedSet;
        }

        public void setReplicatedSet(String replicatedSet) {
            this.replicatedSet = replicatedSet;
        }

        public Boolean getSslEnabled() {
            return sslEnabled;
        }

        public void setSslEnabled(Boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
        }

        public Boolean getSslInvalidHostNameAllowed() {
            return sslInvalidHostNameAllowed;
        }

        public void setSslInvalidHostNameAllowed(Boolean sslInvalidHostNameAllowed) {
            this.sslInvalidHostNameAllowed = sslInvalidHostNameAllowed;
        }

        public Integer getHeartbeatFrequency() {
            return heartbeatFrequency;
        }

        public void setHeartbeatFrequency(Integer heartbeatFrequency) {
            this.heartbeatFrequency = heartbeatFrequency;
        }

        public Integer getMinHeartbeatFrequency() {
            return minHeartbeatFrequency;
        }

        public void setMinHeartbeatFrequency(Integer minHeartbeatFrequency) {
            this.minHeartbeatFrequency = minHeartbeatFrequency;
        }

        public Integer getLocalThreshold() {
            return localThreshold;
        }

        public void setLocalThreshold(Integer localThreshold) {
            this.localThreshold = localThreshold;
        }

        public String getAuthenticationDatabase() {
            return authenticationDatabase;
        }

        public void setAuthenticationDatabase(String authenticationDatabase) {
            this.authenticationDatabase = authenticationDatabase;
        }
    }
}
