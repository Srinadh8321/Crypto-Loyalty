package com.example.cryptoloyalty.services;

import com.example.wallet.contracts.OCCoin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;


@Service
public class OCCoinClient {

    private static final String CONTRACT = "0x6f6212601cce704a81B201D9Fe0E6CBEc1A3C194";

    @Autowired
    private Web3j web3j;

    public TransactionReceipt mine(String userPrivateKey) throws Exception {
        Credentials creds = Credentials.create(userPrivateKey);

        OCCoin token = OCCoin.load(
                CONTRACT,
                web3j,
                creds,
                new DefaultGasProvider()
        );

        return token.mine().send();
    }

    public TransactionReceipt adminMint(
            String ownerPrivateKey,
            String to,
            BigInteger amountWei
    ) throws Exception {

        Credentials owner = Credentials.create(ownerPrivateKey);

        OCCoin token = OCCoin.load(
                CONTRACT,
                web3j,
                owner,
                new DefaultGasProvider()
        );

        return token.adminMint(to, amountWei).send();
    }
}
