package com.example.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WalletRepository extends JpaRepository<WalletEntity, Integer> {

    WalletEntity findByUserName(String userName);


//    @Modifying
//    @Query("select WalletEntity w from wallets set w.amount = w.amount + :amount where w.userName = :userName")
//    long updateWallet(String userName, int amount);
}
