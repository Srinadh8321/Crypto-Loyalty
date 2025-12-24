package com.example.cryptoloyalty.controllers;

import com.example.cryptoloyalty.dto.MineRequest;
import com.example.cryptoloyalty.dto.QrGenRequest;
import com.example.cryptoloyalty.dto.TransferRequest;
import com.example.cryptoloyalty.services.CreateWalletService;
import com.example.cryptoloyalty.services.QrGeneratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

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

        return walletService.transfer(
                req.fromUserId(),
                req.toAddress(),
                req.amountWei(),
                req.transferType()
        );

    }

    @PostMapping("/mine")
    public Map<String, String> mine(
            @RequestBody MineRequest req
    ) throws Exception {

        TransactionReceipt receipt = walletService.mine(
                req.toAddress()
        );
        return Map.of(
                "txHash", receipt.getTransactionHash(),
                "status", String.valueOf(receipt.isStatusOK())
        );

    }

    @GetMapping(value = "/wallet/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateWalletQr(@RequestParam String toAddress) throws Exception {

        // MetaMask-compatible QR payload
        String qrText = "ethereum:" + toAddress + "@11155111";

        return QrGeneratorService.generate(qrText);
    }
    @GetMapping("/enquiry")
    public ResponseEntity walletEnquiry(@RequestParam String userId){
        return walletService.enquiryByuserId(userId);
    }
}
