package com.example.cryptoloyalty.repositories;

import com.example.cryptoloyalty.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, String> {

    Optional<WalletEntity> findByUserId(String fromUserId);
    Optional<WalletEntity> findByWalletAddress(String toWalletAddress);
}
