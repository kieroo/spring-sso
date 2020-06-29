package com.it.sso.controller;

import com.it.sso.common.BaseResponse;
import com.it.sso.constant.SystemCodeEnum;
import com.it.sso.pojo.User;
import com.it.sso.service.RedisService;
import com.it.sso.service.UserService;
import com.it.sso.utils.CookieUtils;
import com.it.sso.utils.PBKDF2Util;
import com.it.sso.utils.SessionUtils;
import com.it.sso.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    RedisService redisService;

    @Autowired
    private UserService userService;

    @Value("${secretKey}")
    private String secretKey;

    private static final long EXPIRE_TIME = 1800 * 1000;

    private static final int COOKIE_EXPIRE_TIME = 1800;

    @RequestMapping("/login")
    public BaseResponse Login(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map) {

        BaseResponse resp = new BaseResponse();
        String username = (String) map.get("username");
        String password = (String) map.get("password");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            resp.setCode(SystemCodeEnum.PARAM_ERR.getCode());
            resp.setMsg(SystemCodeEnum.PARAM_ERR.getMsg());
            return resp;
        }
        String token = CookieUtils.getCookieValue(request, "token");
        if (null == token) {
            try {
                return checkUserInfo(request, response, resp, username, password);
            } catch (Exception e) {

            }
        }
        String userId = TokenUtils.getUserInfo(token, "userId");
        String catchToken = (String) redisService.getValue(userId);
        try {
            if (null == catchToken) {
                return checkUserInfo(request, response, resp, username, password);
            }

        } catch (Exception e) {
            resp.setCode(SystemCodeEnum.ERR.getCode());
            resp.setMsg(SystemCodeEnum.ERR.getMsg());
        }
        resp.setCode(SystemCodeEnum.SUCCESS.getCode());
        resp.setMsg(SystemCodeEnum.SUCCESS.getMsg());
        return resp;
    }

    private BaseResponse checkUserInfo(HttpServletRequest request, HttpServletResponse response, BaseResponse resp, String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User user = userService.queryUser(username);
        if (Objects.isNull(user)) {
            resp.setCode(SystemCodeEnum.NAME_ERR.getCode());
            resp.setMsg(SystemCodeEnum.NAME_ERR.getMsg());
            return resp;
        }
        boolean authenticate = PBKDF2Util.authenticate(password, user.getPassword(), user.getSalt());
        if (authenticate) {
            String genToken = TokenUtils.generateToken("userId", user.getId().toString(), secretKey);
            redisService.setKeyWithExpire(user.getId().toString(), genToken, EXPIRE_TIME);
            CookieUtils.setCookie(request, response, "token", genToken, COOKIE_EXPIRE_TIME, true);
            SessionUtils.setSession(request, user);
            resp.setCode(SystemCodeEnum.SUCCESS.getCode());
            resp.setMsg(SystemCodeEnum.SUCCESS.getMsg());
            return resp;
        }
        resp.setCode(SystemCodeEnum.PWD_ERR.getCode());
        resp.setMsg(SystemCodeEnum.PWD_ERR.getMsg());
        return resp;
    }

    @RequestMapping("/loginOut")
    public BaseResponse loginOut(HttpServletRequest request, HttpServletResponse response) {

        BaseResponse resp = new BaseResponse();
        String userId = SessionUtils.getValueFromSession(request, "userId");
        if (null == userId) {
            resp.setCode(SystemCodeEnum.NOT_LOGIN.getCode());
            resp.setMsg(SystemCodeEnum.NOT_LOGIN.getMsg());
            return resp;
        }

        CookieUtils.deleteCookie(request, response, "token");
        redisService.delKey(userId);
        resp.setCode(SystemCodeEnum.SUCCESS.getCode());
        resp.setCode(SystemCodeEnum.SUCCESS.getMsg());
        return resp;

    }

    @RequestMapping("/validLogin")
    public BaseResponse validLogin(HttpServletRequest request, HttpServletResponse response) {
        BaseResponse resp = new BaseResponse();
        String token = CookieUtils.getCookieValue(request, "token", true);
        boolean b = TokenUtils.verifyToken(token, secretKey);
        if (b) {
            resp.setCode(SystemCodeEnum.SUCCESS.getCode());
            resp.setMsg(SystemCodeEnum.SUCCESS.getMsg());
            return resp;
        }
        resp.setCode(SystemCodeEnum.NOT_LOGIN.getCode());
        resp.setMsg(SystemCodeEnum.NOT_LOGIN.getMsg());
        return resp;
    }

    @RequestMapping("/register")
    public BaseResponse register(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map) {
        BaseResponse resp = new BaseResponse();
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        String passwordAgain = (String) map.get("passwordAgain");

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            resp.setCode(SystemCodeEnum.PARAM_ERR.getCode());
            resp.setMsg(SystemCodeEnum.PARAM_ERR.getMsg());
            return resp;
        }

        if (!password.equals(passwordAgain)) {
            resp.setCode(SystemCodeEnum.PARAM_ERR.getCode());
            resp.setMsg(SystemCodeEnum.PARAM_ERR.getMsg());
            return resp;
        }

        String email = (String) map.get("email");
        String mobile = (String) map.get("mobile");
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setMobile(mobile);
        String salt = PBKDF2Util.generateSalt();
        user.setSalt(salt);
        try {
            String encryptedPassword = PBKDF2Util.getEncryptedPassword(password, salt);
            user.setPassword(encryptedPassword);
            int num = userService.insert(user);
            if (num == 1) {
                resp.setCode(SystemCodeEnum.SUCCESS.getCode());
                resp.setMsg(SystemCodeEnum.SUCCESS.getMsg());
                return resp;
            }
        } catch (Exception e) {

        }
        resp.setCode(SystemCodeEnum.ERR.getCode());
        resp.setMsg(SystemCodeEnum.ERR.getMsg());
        return resp;
    }
}
