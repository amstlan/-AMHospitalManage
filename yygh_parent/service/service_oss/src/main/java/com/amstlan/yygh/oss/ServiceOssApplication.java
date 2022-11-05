package com.amstlan.yygh.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

//取消数据源自动配置
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
                                    MongoAutoConfiguration.class,
                                    MongoDataAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.amstlan"})
public class ServiceOssApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApplication.class, args);
    }
}
