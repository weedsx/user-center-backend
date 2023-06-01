package com.weeds.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weeds.usercenter.common.BaseResponse;
import com.weeds.usercenter.common.ErrorCode;
import com.weeds.usercenter.common.ResponseUtil;
import com.weeds.usercenter.exception.BusinessException;
import com.weeds.usercenter.model.entity.User;
import com.weeds.usercenter.model.request.UserLoginRequest;
import com.weeds.usercenter.model.request.UserRegisterRequest;
import com.weeds.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.weeds.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.weeds.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author weeds
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest) {
        if (registerRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResponseUtil.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(
            @RequestBody UserLoginRequest loginRequest,
            HttpServletRequest request) {
        if (loginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = loginRequest.getUserAccount();
        String password = loginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, password, request);
//        System.out.println(user);
        return ResponseUtil.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int flag = userService.userLogout(request);
        return ResponseUtil.success(flag);
    }

    /**
     * 根据用户请求的session获取用户的登录态
     *
     * @param request 请求
     * @return 返回的用户
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User sessionUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (sessionUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User user = userService.getById(sessionUser.getId());
        // todo: 校验用户是否合法
        User safeUser = userService.getSafeUser(user);
        return ResponseUtil.success(safeUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String userName, HttpServletRequest request) {
        // 鉴权，只有管理员才能查询
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_ACCESS);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            wrapper.like("username", userName);
        }
        List<User> userList = userService.list(wrapper);
        // 用户脱敏
        List<User> list = userList.stream().map(user -> userService.getSafeUser(user))
                .collect(Collectors.toList());
        return ResponseUtil.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long userId, HttpServletRequest request) {
        // 鉴权，只有管理员才能删除
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_ACCESS);
        }
        if (userId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean removed = userService.removeById(userId);
        return ResponseUtil.success(removed);
    }

    /**
     * 查看是否为管理员
     *
     * @param request 请求
     * @return 是否为管理员
     */
    private static boolean isAdmin(HttpServletRequest request) {
        // 鉴权，看是不是管理员
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            return false;
        }
        return user.getUserRole() == ADMIN_ROLE;
    }

}
