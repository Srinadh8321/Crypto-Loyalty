package com.example.cryptoloyalty.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
@Data
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_id")
    private String userId;
    @Column(name="wallet_address",nullable = false, unique = true)
    private String walletAddress;
    private String chain;
    private  String network;
    @Column(name = "encrypted_private_key",nullable = false)
    private String encryptedPrivateKey;
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
