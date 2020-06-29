package com.it.sso.service.impl;


import com.it.sso.dao.UserMapper;
import com.it.sso.pojo.User;

import com.it.sso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User queryUser(String name) {
        return userMapper.queryUser(name);
    }

    @Override
    public int insert(User user) {

        return userMapper.insert(user);
    }
}
