package com.example.cryptoloyalty.controllers;

import com.example.cryptoloyalty.dto.TransferRequest;
import com.example.cryptoloyalty.services.CreateWalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class WalletController {

    private final CreateWalletService walletService;

    public WalletController(CreateWalletService walletService) {
        this.walletService = walletService;
        System.out.println("Created controller !");
    }

    @GetMapping("/")
    public String welcome(){
        return "Welcome to Crypto !";
    }
    @PostMapping("/v1/wallets/signup")
    public ResponseEntity createWallet(
        @RequestBody Map<String,String> body
    ) throws Exception {
        System.out.println(body);
        System.out.println(body.get("userId"));
        if(body!=null && body.get("userId")!=null)
            return walletService.createWallet(body.get("userId"));
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/transfer")
    public Map<String, String> transfer(
            @RequestBody TransferRequest req
    ) throws Exception {

        String txHash = walletService.transfer(
                req.fromUserId(),
                req.toAddress(),
                req.amountWei(),
                req.transferType()
        );

        return Map.of("txHash", txHash);
    }
}
