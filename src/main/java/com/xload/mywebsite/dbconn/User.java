package com.xload.mywebsite.dbconn;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "website")
public class User {
    @Id
    private String login;
    private String password;
    private String text;
    private int likes;
    private String liked;

    public User(String username, String password) {
        this.login = username;
        this.password = password;
    }
}
