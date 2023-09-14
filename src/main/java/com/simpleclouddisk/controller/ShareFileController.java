package com.simpleclouddisk.controller;

import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.domain.dto.ShareDto;
import com.simpleclouddisk.domain.dto.ShareFileDto;
import com.simpleclouddisk.domain.entity.ShareFile;
import com.simpleclouddisk.domain.vo.ShareVo;
import com.simpleclouddisk.service.ShareFileService;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/share")
public class ShareFileController {

    @Autowired
    private ShareFileService shareFileService;

    @PostMapping("/file/{fileList}/{time}")
    public Result shareFiles(@PathVariable List<Long> fileList, @PathVariable Integer time){
        String path = shareFileService.share(fileList,time);

        return Result.ok(path);
    }

    @PostMapping("/file/parse")
    public Result parseUrl(@RequestBody ShareDto share){
        List<ShareFileDto> list = shareFileService.parseUrl(share.getUrl());
        return Result.ok(list);
    }

    @GetMapping("/image/{fileId}")
    public void image(@PathVariable Long fileId, HttpServletResponse response) throws IOException {
        shareFileService.image(fileId,response);
    }



}
