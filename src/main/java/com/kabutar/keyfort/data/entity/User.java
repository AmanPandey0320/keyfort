package com.kabutar.keyfort.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import com.kabutar.keyfort.data.entity.client.Client;

@Entity
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String firstName;

    @Column(nullable = true)
    private String lastName;

    @Column(nullable = false)
    private boolean isVerified = false;

    @ManyToOne
    @JoinColumn(name = "clientId", nullable = false)
    private Client client;

    @OneToMany
    @ToString.Exclude
    private List<Credential> credentials;

    @OneToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Token> tokens;
    
    @OneToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Session> sessions;
    
}
