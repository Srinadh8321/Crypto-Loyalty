package com.example.cryptoloyalty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {

    @Bean
    public Web3j web3j() {
        return Web3j.build(
                new HttpService("https://sepolia.infura.io/v3/f6e25f79950b4c1c8d58049657ca6ac1")
        );
    }
}
