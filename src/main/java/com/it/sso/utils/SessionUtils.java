package com.it.sso.utils;

import com.it.sso.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static void setSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userId", user.getId());
    }

    public static String getValueFromSession(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute(key);
    }
}
