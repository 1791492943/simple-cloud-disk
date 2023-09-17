package com.simpleclouddisk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simpleclouddisk.domain.entity.ShareLink;
import com.simpleclouddisk.service.ShareLinkService;
import com.simpleclouddisk.mapper.ShareLinkMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【share_link】的数据库操作Service实现
* @createDate 2023-09-12 09:40:35
*/
@Service
public class ShareLinkServiceImpl extends ServiceImpl<ShareLinkMapper, ShareLink>
    implements ShareLinkService{

    @Override
    public long count(Long linkId) {
        ShareLink shareLink = baseMapper.selectById(linkId);
        Long visitNum = shareLink.getVisitNum();
        return visitNum;
    }

    @Override
    public Map<String, String> shareLink(Long id) {
        ShareLink shareLink = baseMapper.selectOne(new LambdaQueryWrapper<ShareLink>().eq(ShareLink::getLinkId, id).eq(ShareLink::getUserId, StpUtil.getLoginIdAsLong()));
        Map<String, String> map = new HashMap<>();
        map.put("url",shareLink.getShareLink());
        map.put("code",shareLink.getCode());
        return map;
    }
}




