package com.example.ratingfinder.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "user")
public class User implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer user_id;
    @Column(name = "username")
    private String username;
    @Column(name="password")
    private String password;
    @Column(name="email")
    private String email;
    @Column(name="credit_level")
    private int credit_level;



    public User(int user_id, String username, String password, String email, int credit_level) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.credit_level = credit_level;
    }

    public User() {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCredit_level() {
        return credit_level;
    }

    public void setCredit_level(int credit_level) {
        this.credit_level = credit_level;
    }

}
