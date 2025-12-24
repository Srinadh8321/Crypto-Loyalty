package com.example.cryptoloyalty.controllers;

import com.example.cryptoloyalty.dto.PaymentIntent;
import com.example.cryptoloyalty.services.PaymentIntentService;
import com.example.cryptoloyalty.services.QrGeneratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/payments")
public class PaymentQrController {

    private final PaymentIntentService service;

    @PostMapping
    public PaymentIntent createPayment(@RequestBody PaymentIntent request) {
        return service.create(
                request.getToAddress(),
                "0x6f6212601cce704a81b201d9fe0e6cbec1a3c194",
                request.getAmount(),
                11155111
        );
    }

    public PaymentQrController(PaymentIntentService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public PaymentIntent getPayment(@PathVariable String id) {
        PaymentIntent intent = service.get(id);
        if (intent == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return intent;
    }



    @GetMapping(
            value = "/{id}/qr",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public byte[] getPaymentQr(@PathVariable String id) throws Exception {

        PaymentIntent intent = service.get(id);
        if (intent == null) {
            throw new RuntimeException("Invalid payment id");
        }

        // This is the IMPORTANT PART
        String payUrl = "https://35b00397282a.ngrok-free.app/pay.html?paymentId=" + id;

        return QrGeneratorService.generate(payUrl);
    }
}
