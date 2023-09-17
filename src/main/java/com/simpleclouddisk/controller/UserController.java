package com.simpleclouddisk.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.domain.dto.FileListDto;
import com.simpleclouddisk.domain.dto.UploadRecordsDto;
import com.simpleclouddisk.domain.dto.UserFileDto;
import com.simpleclouddisk.domain.dto.UserLoginDto;
import com.simpleclouddisk.domain.entity.User;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.service.UserService;
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
     * 退出登录
     * @return
     */
    @PostMapping("/logout")
    public Result logout(){
        StpUtil.logout();
        return Result.ok("退出登录");
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
     * 查看文件列表
     * @param fileListDto
     * @return
     */
    @GetMapping("/file/list")
    public Result fileList(FileListDto fileListDto){
        List<UserFileDto> list = userService.list(fileListDto);
        return Result.ok(list);
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
     * 删除分片信息
     * @param md5
     * @return
     */
    @DeleteMapping("/deleteShardByMd5/{md5}")
    public Result deleteShardByMd5(@PathVariable String md5){
        userService.deleteShardByMd5(md5);
        return Result.ok("删除成功");
    }

    /**
     * 查看未完成文件数量
     * @return
     */
    @GetMapping("/shardNum")
    public Result shardNum(){
        long num = userService.shardNum();
        return Result.ok(num);
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

    /**
     * 搜索文件
     * @param name
     * @return
     */
    @GetMapping("/search/{name}")
    public Result search(@PathVariable String name){
        List<UserFileDto> userFileDtoList = userService.search(name);
        return Result.ok(userFileDtoList);
    }

    /**
     * 移动文件
     * @param ids
     * @param pid
     * @return
     */
    @PutMapping("/move/{ids}/{pid}")
    public Result move(@PathVariable List<Long> ids, @PathVariable Long pid){
        userService.move(ids,pid);
        return Result.ok("移动成功");
    }

    /**
     * 查询文件夹
     * @param pid
     * @return
     */
    @GetMapping("/folder/{pid}")
    public Result folder(@PathVariable Long pid){
        List<UserFileDto> folder = userService.folder(pid);
        return Result.ok(folder);
    }



}
