package com.yfaleev.springchatclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Scanner;

@SpringBootApplication
public class SpringChatClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringChatClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    @Scope("prototype")
    public Scanner systemInScanner() {
        return new Scanner(System.in);
    }
}
