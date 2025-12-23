package com.example.cryptoloyalty.services;

import com.example.wallet.contracts.Erc20;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

@Service
public class ERC20Client {

    private static final String CONTRACT = "0x6f6212601cce704a81B201D9Fe0E6CBEc1A3C194";
    @Autowired
    private Web3j web3j;
    public ERC20Client() {

    }

    public TransactionReceipt transfer(
            String fromPrivateKey,
            String toAddress,
            BigInteger amountWei
    ) throws Exception {

        Credentials creds = Credentials.create(fromPrivateKey);

        Erc20 token = Erc20.load(
                CONTRACT,
                web3j,
                creds,
                new DefaultGasProvider()
        );

        return token.transfer(toAddress, amountWei).send();
    }
}
