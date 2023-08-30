package com.simpleclouddisk.controller;

import com.alibaba.fastjson.JSON;
import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.domain.dto.FileSecondsPassDto;
import com.simpleclouddisk.domain.dto.FileShardDto;
import com.simpleclouddisk.exception.service.SpaceException;
import com.simpleclouddisk.service.FileService;
import com.simpleclouddisk.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    public FileService fileService;

    @Autowired
    public UserFileService userFileService;

    /**
     * 分片上传文件
     * @param file
     * @throws Exception
     */
    @PostMapping("/upload/shard")
    public Result upload(@RequestPart("file") MultipartFile file, @RequestParam("fileShard") String fileShard) throws Exception {
        fileService.upload(file,fileShard);

        FileShardDto fileShardDto = JSON.parseObject(fileShard, FileShardDto.class);
        if(fileShardDto.getLast() == 1){
            return Result.ok("文件上传成功");
        }
        return Result.ok("分片上传成功");
    }

    /**
     * 秒传
     * @param fileSecondsPassDto
     * @return
     */
    @PostMapping("/upload/seconds")
    public Result secondsPass(@RequestBody FileSecondsPassDto fileSecondsPassDto) throws SpaceException {
        userFileService.secondsPass(fileSecondsPassDto);
        return Result.ok("文件上传成功");
    }

    /**
     * 判断文件是否存在
     * @param fileMd5
     * @return
     */
    @GetMapping("/md5/{fileMd5}")
    public Result fileExist(@PathVariable String fileMd5){
        Map<String,String> map = fileService.fileExist(fileMd5);
        Result<Object> result = new Result<>();
        result.setCode(200);
        result.setData(map.get("exist"));
        result.setMsg(map.get("name"));
        return result;
    }

    /**
     * 在线预览
     * @param fileName
     * @return
     * @throws Exception
     */
    @GetMapping("/preview/{fileName}")
    public Result preview(@PathVariable String fileName) throws Exception {
        String url = fileService.preview(fileName);
        return Result.ok(url);
    }

    /**
     * 删除文件
     * @param fileName
     * @throws Exception
     */
    @DeleteMapping("/delete/{fileName}")
    public Result delete(@PathVariable String fileName) throws Exception {
        fileService.delete(fileName);
        return Result.ok("删除成功");
    }

    /**
     * 下载文件
     * @param fileName
     * @param response
     * @throws Exception
     */
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws Exception {
        fileService.download(fileName, response.getOutputStream());
    }

}