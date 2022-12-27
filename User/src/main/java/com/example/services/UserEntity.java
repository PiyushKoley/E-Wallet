package com.example.services;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userName;

    private String name;

    private String email;
    private String mobileNo;
    
}
