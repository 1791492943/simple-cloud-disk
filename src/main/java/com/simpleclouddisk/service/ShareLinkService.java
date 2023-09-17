package com.simpleclouddisk.service;

import com.simpleclouddisk.domain.entity.ShareLink;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author Administrator
* @description 针对表【share_link】的数据库操作Service
* @createDate 2023-09-12 09:40:35
*/
public interface ShareLinkService extends IService<ShareLink> {

    long count(Long linkId);

    Map<String, String> shareLink(Long id);
}
