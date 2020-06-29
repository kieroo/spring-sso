package com.it.sso.service;

import com.it.sso.pojo.User;

public interface UserService {
    public User queryUser(String name);

    int insert(User user);
}
