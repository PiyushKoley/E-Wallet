package com.example.services;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {
    TransactionEntity findByTransactionId(String transactionId);
}
