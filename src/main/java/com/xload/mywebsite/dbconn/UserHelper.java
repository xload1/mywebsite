package com.xload.mywebsite.dbconn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserHelper {
    UserRepository userRepository;

    @Autowired
    public UserHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> getUsers(){
        return userRepository.findAll();
    }
    public void addUser(User user){
        userRepository.save(user);
    }
    public User getUser(String login){
        return userRepository.findById(login).orElse(null);
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
}
