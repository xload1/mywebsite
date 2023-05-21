package com.xload.mywebsite.dbconn;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class website {
    @Id
    String login;
    String password;
    String text;
    int likes;
    String liked;
}
