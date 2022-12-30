package com.example.services;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="transactions")
@Entity
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String transactionId;

    private String fromUser;

    private String toUser;

    private int amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private String transactionTime;

}
