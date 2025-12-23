package com.example.cryptoloyalty.dto;

import java.math.BigDecimal;

public record TransferRequest(
        String fromUserId,
        String toAddress,
        String transferType,
        BigDecimal amountWei
){}
