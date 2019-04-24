package com.souja.lib.enums;

/**
 * 错误提示
 * Created by Yangdz on 2016/8/21.
 */
public enum EnumExceptions {
    NO_INTERNET(0, "网络连接失败，请检查手机网络后重试"),
    SERVER_FAILED(1, "连接服务器失败，请稍候重试"),
    SERVER_TIMEOUT(2, "连接服务器超时，请稍候重试"),
    NO_INTERNET_A(3, "网络连接失败，请稍候重试"),
    BAD_GET_AWAY(502, "抱歉，服务器开小差了..."),
    ;

    private int code;
    private String desc;

    EnumExceptions(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumExceptions getStatus(int code) {
        for (EnumExceptions status : EnumExceptions.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
