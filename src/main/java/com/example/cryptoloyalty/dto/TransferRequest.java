package com.example.cryptoloyalty.dto;

import java.math.BigInteger;

public record TransferRequest(
        String fromUserId,
        String toAddress,
        BigInteger amountWei,
        String transferType
) {}
