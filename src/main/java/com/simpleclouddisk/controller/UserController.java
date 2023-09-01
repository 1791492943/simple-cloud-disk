package com.simpleclouddisk.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.domain.dto.FilePageDto;
import com.simpleclouddisk.domain.dto.UserLoginDto;
import com.simpleclouddisk.domain.entity.User;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.service.UserFileService;
import com.simpleclouddisk.service.UserService;
import com.simpleclouddisk.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/user")
@SaIgnore
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFileService userFileService;

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

    /**
     * 删除文件(回收站)
     * @param fileIds
     * @return
     */
    @DeleteMapping("/delete/{fileIds}")
    public Result deleteFileById(@PathVariable Long[] fileIds){
        userService.deleteFileById(fileIds);
        return Result.ok("删除成功");
    }

    /**
     * 删除文件(真)
     * @param fileIds
     * @return
     */
    @DeleteMapping("/remove/{fileIds}")
    public Result deleteFileInfoById(@PathVariable Long[] fileIds){
        userService.deleteFileInfoById(Arrays.asList(fileIds));
        return Result.ok("删除成功");
    }

    /**
     * 修改文件名
     * @param id
     * @param fileName
     * @return
     */
    @PutMapping("/rename/{id}/{fileName}")
    public Result rename( @PathVariable Long id, @PathVariable String fileName){
        userService.rename(id,fileName);
        return Result.ok("修改成功");
    }
}
