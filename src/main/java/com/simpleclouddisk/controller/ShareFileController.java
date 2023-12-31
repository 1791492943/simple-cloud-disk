package com.simpleclouddisk.controller;

import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.domain.dto.ShareDto;
import com.simpleclouddisk.domain.dto.ShareFileDto;
import com.simpleclouddisk.domain.entity.ShareFile;
import com.simpleclouddisk.domain.vo.ShareVo;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.service.ShareFileService;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/share")
public class ShareFileController {

    @Autowired
    private ShareFileService shareFileService;

    @PostMapping("/file/{fileList}/{time}")
    public Result shareFiles(@PathVariable List<Long> fileList, @PathVariable Integer time){
        Map<String, String> map = shareFileService.share(fileList, time);
        return Result.ok(map);
    }

    @PostMapping("/file/parse")
    public Result parseUrl(@RequestBody ShareDto share) throws ServiceException {
        List<ShareFileDto> list = shareFileService.parseUrl(share);
        return Result.ok(list);
    }

    @PostMapping("/file/urlExist")
    public Result urlExist(@RequestBody ShareDto share){
        boolean bool = shareFileService.urlExist(share.getUrl());
        return Result.ok(bool);
    }

    @GetMapping("/image/{fileId}")
    public void image(@PathVariable Long fileId, HttpServletResponse response) throws IOException {
        shareFileService.image(fileId,response);
    }

    /**
     * 分享文件列表
     * @return
     */
    @GetMapping("/list")
    public Result shareList(){
        List list = shareFileService.shareList();
        return Result.ok(list);
    }

    @DeleteMapping("/unshare/{ids}")
    public Result unshare(@PathVariable List<Long> ids){
        shareFileService.unshare(ids);
        return Result.ok("取消分享成功");
    }

}
