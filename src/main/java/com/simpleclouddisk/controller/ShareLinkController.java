package com.simpleclouddisk.controller;

import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.service.ShareLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/shareLink")
public class ShareLinkController {

    @Autowired
    private ShareLinkService shareLinkService;

    @GetMapping("/count/{linkId}")
    public Result Count(@PathVariable Long linkId){
        long count = shareLinkService.count(linkId);
        return Result.ok(count);
    }

    @GetMapping("/{id}")
    public Result shareLink(@PathVariable Long id){
        Map<String,String> map = shareLinkService.shareLink(id);
        return Result.ok(map);
    }

}
