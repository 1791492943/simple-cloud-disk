package com.simpleclouddisk.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.domain.dto.FilePageDto;
import com.simpleclouddisk.domain.dto.UploadRecordsDto;
import com.simpleclouddisk.domain.dto.UserLoginDto;
import com.simpleclouddisk.domain.entity.User;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.service.UserFileService;
import com.simpleclouddisk.service.UserService;
import com.simpleclouddisk.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
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
     * 获取当前登陆人id
     * @return
     */
    @GetMapping("/id")
    public Result getUserId(){
        return Result.ok(StpUtil.getLoginIdAsString());
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

    /**
     * 未完成的上传任务
     * @return
     */
    @GetMapping("/uploadInfo")
    public Result uploadInfo(){
        List<UploadRecordsDto> list = userService.uploadInfo();
        return Result.ok(list);
    }

    /**
     * 还原文件列表
     * @param fileIds
     * @return
     */
    @PutMapping("/restore/{fileIds}")
    public Result restore(@PathVariable Long[] fileIds){
        userService.restore(fileIds);
        return Result.ok("还原成功");
    }

    /**
     * 新建文件夹
     * @param pid
     * @param folderName
     * @return
     */
    @PostMapping("/newFolder/{pid}/{folderName}")
    public Result newFolder(@PathVariable Long pid, @PathVariable String folderName){
        userService.newFolder(pid,folderName);
        return Result.ok("新建文件夹成功");
    }
}
