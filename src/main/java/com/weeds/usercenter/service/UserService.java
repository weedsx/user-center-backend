package com.weeds.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weeds.usercenter.model.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户的Service
 *
 * @author weeds
 * @description 针对表【user(用户)】的数据库操作Service
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   账号
     * @param password      密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String password, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 账号
     * @param password    密码
     * @return 信息脱敏的用户
     */
    User userLogin(String userAccount, String password, HttpServletRequest request);

    /**
     * 获取脱敏用户
     *
     * @param originUser 原用户
     * @return 脱敏用户
     */
    User getSafeUser(User originUser);

    /**
     * 退出登录，删除session
     * @param request 请求
     * @return 1 | 0
     */
    int userLogout(HttpServletRequest request);
}
