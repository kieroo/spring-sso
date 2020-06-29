package com.it.sso.constant;

public enum SystemCodeEnum {
    SUCCESS("000000","成功"),
    ERR("000001","失败"),
    PARAM_ERR("000002","参数错误"),
    NOT_LOGIN("000003","未登录"),
    LOGIN_EXPIRE("000004","登录已过期"),
    NAME_ERR("000005","用户名错误"),
    PWD_ERR("000006","密码错误");
    private String code;
    private String msg;

    SystemCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
