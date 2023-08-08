package com.simpleclouddisk.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.domain.User;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.exception.service.RegisterException;
import com.simpleclouddisk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@SaIgnore
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 生成短信验证码
     * @param phone
     */
    @GetMapping("/code/{phone}")
    public void code(@PathVariable String phone) {
        userService.code(phone);
    }

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user) throws ServiceException {
        String login = userService.login(user);
        return Result.ok(login);
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) throws RegisterException {
        userService.register(user);
        return Result.ok("注册成功!");
    }

    /**
     * 修改密码
     * @param user
     * @return
     */
    @PutMapping("/update/password")
    public Result updatePassword(@RequestBody User user) throws ServiceException {
        userService.updatePassword(user);
        return Result.ok("密码设置成功!");
    }

}
