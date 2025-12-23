package com.example.cryptoloyalty.services;
import com.example.cryptoloyalty.entity.WalletEntity;
import com.example.cryptoloyalty.repositories.WalletRepository;
import com.example.cryptoloyalty.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CreateWalletService {

    private final WalletRepository repo;
    private final KeyEncryptionService crypto;
    private final ERC20Client erc20;
    private String privateKeyHex;
    private final Logger logger= LoggerFactory.getLogger(CreateWalletService.class);

    public CreateWalletService(
            WalletRepository repo,
            KeyEncryptionService crypto,
            ERC20Client erc20
    ) {
        this.repo = repo;
        this.crypto = crypto;
        this.erc20 = erc20;
    }

    // CREATE WALLET
    public ResponseEntity createWallet(String userId) throws Exception {
       logger.info("userId : {}",userId);
        Credentials creds= Credentials.create(getPrivateKeyHex());
        WalletEntity wallet = new WalletEntity();
        wallet.setUserId(userId);
        wallet.setWalletAddress(creds.getAddress());
        logger.info("Address ::{}",creds.getAddress());
        wallet.setStatus("Active");
        wallet.setEncryptedPrivateKey(getPrivateKeyHex());
        wallet.setCreatedAt(LocalDateTime.now());
        wallet=repo.save(wallet);
        Map<String, String> response= new HashMap<>();
        response.put("userId",userId);
        response.put("walletAddress", wallet.getWalletAddress());
        response.put("status", Constants.CREATED);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    // TRANSFER TOKENS
    public String transfer(
            String fromUserId,
            String toAddress,
            BigInteger amountWei
    ) throws Exception {

        WalletEntity wallet =
                repo.findByUserId(fromUserId).orElseThrow();

        String privateKey =
                crypto.decrypt(wallet.getEncryptedPrivateKey());

        TransactionReceipt receipt =
                erc20.transfer(privateKey, toAddress, amountWei);

        return receipt.getTransactionHash();
    }
    
    public String getPrivateKeyHex() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        if(Constants.priavteKey==null) {
            ECKeyPair masterKey = Keys.createEcKeyPair();
            String masterPrivateKeyHex = masterKey.getPrivateKey().toString(16);
            System.out.println(masterPrivateKeyHex);
            Constants.priavteKey =masterPrivateKeyHex;
            logger.info("private Key: {}",masterPrivateKeyHex);
        }
        return Constants.priavteKey;
    }
}
