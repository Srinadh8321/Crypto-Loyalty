package com.example.cryptoloyalty.dto;


import lombok.Data;

@Data
public class PaymentIntent {
    private String id;
    private String to;
    private String token;
    private String amount;
    private String toAddress;
    private String tokenAddress="0x6f6212601cce704a81b201d9fe0e6cbec1a3c194";
    // human-readable, e.g. "100"
    private int chainId;

    // getters & setters
}
