package com.weeds.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求参数
 *
 * @author weeds
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -6616503136747793076L;
    private String userAccount;
    private String userPassword;
}
