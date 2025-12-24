package com.example.cryptoloyalty.services;

import com.example.cryptoloyalty.dto.PaymentIntent;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentIntentService {

    // In-memory store (fine for now)
    private final Map<String, PaymentIntent> store = new ConcurrentHashMap<>();

    /**
     * CREATE payment intent
     */
    public PaymentIntent create(
            String toAddress,
            String tokenAddress,
            String amount,
            long chainId
    ) {
        PaymentIntent intent = new PaymentIntent();

        intent.setId(UUID.randomUUID().toString());
        intent.setToAddress(toAddress);
        intent.setTokenAddress("0x6f6212601cce704a81b201d9fe0e6cbec1a3c194");
        intent.setAmount(amount);
        intent.setChainId(11155111);

        store.put(intent.getId(), intent);
        return intent;
    }

    /**
     * GET payment intent by id
     */
    public PaymentIntent get(String id) {
        return store.get(id);
    }
}
