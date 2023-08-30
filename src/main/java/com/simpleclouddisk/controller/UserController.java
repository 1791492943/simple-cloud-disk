package com.simpleclouddisk.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.domain.dto.FilePageDto;
import com.simpleclouddisk.domain.dto.UserLoginDto;
import com.simpleclouddisk.domain.entity.User;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public Result code(@PathVariable String phone) {
        userService.code(phone);
        return Result.ok("验证码发送成功");
    }

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDto user) throws ServiceException {
        String token = userService.login(user);
        return Result.ok(token);
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

    /**
     * 查看使用空间
     * @return
     */
    @GetMapping("/space")
    public Result getSpace(){
        Map<String,Long> map = userService.getSpace();
        return Result.ok(map);
    }

    /**
     * 分页查看文件列表
     * @param filePageDto
     * @return
     */
    @GetMapping("/file/page")
    public Result filePage(FilePageDto filePageDto){
        Page page = userService.page(filePageDto);
        return Result.ok(page);
    }

    @DeleteMapping("/delete/{fileIds}")
    public Result deleteFileById(@PathVariable Long[] fileIds){
        userService.deleteFileById(fileIds);
        return Result.ok("删除成功");
    }
}
