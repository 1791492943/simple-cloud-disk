package com.simpleclouddisk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simpleclouddisk.code.FileCode;
import com.simpleclouddisk.config.MinioConfig;
import com.simpleclouddisk.domain.dto.ShareDto;
import com.simpleclouddisk.domain.dto.ShareFileAddCountDto;
import com.simpleclouddisk.domain.dto.ShareFileDto;
import com.simpleclouddisk.domain.entity.FileInfo;
import com.simpleclouddisk.domain.entity.ShareFile;
import com.simpleclouddisk.domain.entity.ShareLink;
import com.simpleclouddisk.domain.entity.UserFile;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.mapper.FileMapper;
import com.simpleclouddisk.mapper.ShareLinkMapper;
import com.simpleclouddisk.mapper.UserFileMapper;
import com.simpleclouddisk.service.ShareFileService;
import com.simpleclouddisk.mapper.ShareFileMapper;
import com.simpleclouddisk.utils.ShareCode;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【share_file】的数据库操作Service实现
 * @createDate 2023-09-12 09:40:35
 */
@Service
public class ShareFileServiceImpl extends ServiceImpl<ShareFileMapper, ShareFile> implements ShareFileService {

    @Autowired
    private UserFileMapper userFileMapper;

    @Autowired
    private ShareLinkMapper shareLinkMapper;

    @Autowired
    private FileMapper fileMapper;

    @Override
    public Map<String, String> share(List<Long> fileList, Integer time) {
        List<UserFile> userFiles = userFileMapper.selectBatchIds(fileList);

        String s = Base64Utils.encodeToUrlSafeString(UUID.randomUUID().toString().getBytes());

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ShareLink shareLink = new ShareLink();
        shareLink.setUserId(StpUtil.getLoginIdAsLong());
        shareLink.setShareLink(s);
        shareLink.setVisitNum(0L);
        shareLink.setCode(ShareCode.getCode());
        shareLink.setCreateTime(timestamp);
        shareLink.setExpiryTime(time == 0 ? null : Timestamp.valueOf(timestamp.toLocalDateTime().plusDays(time)));

        shareLinkMapper.insert(shareLink);

        List<ShareFile> collect = userFiles.stream().map(item -> {
            ShareFile shareFile = new ShareFile(item);
            shareFile.setLinkId(shareLink.getLinkId());
            shareFile.setFileId(item.getId());
            shareFile.setCreateTime(timestamp);
            shareFile.setExpiryTime(time == 0 ? null : Timestamp.valueOf(timestamp.toLocalDateTime().plusDays(time)));
            return shareFile;
        }).collect(Collectors.toList());

        this.saveBatch(collect);

        Map<String, String> map = new HashMap<>();
        map.put("url", s);
        map.put("code", shareLink.getCode());
        return map;
    }

    @Override
    public List<ShareFileDto> parseUrl(ShareDto share) throws ServiceException {
        ShareLink shareLink = shareLinkMapper.selectOne(new LambdaQueryWrapper<ShareLink>().eq(ShareLink::getShareLink, share.getUrl()));
        if(!shareLink.getCode().equals(share.getCode())){
            throw new ServiceException("提取码错误");
        }
        List<ShareFile> list = this.list(new LambdaQueryWrapper<ShareFile>().eq(ShareFile::getLinkId, shareLink.getLinkId()));
        List<ShareFileDto> collect = list.stream().map(ShareFileDto::new).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void image(Long fileId, HttpServletResponse response) throws IOException {
        UserFile userFile = userFileMapper.selectOne(new LambdaQueryWrapper<UserFile>().eq(UserFile::getId, fileId));
        fileId = userFile.getFileId();
        FileInfo fileInfo = fileMapper.selectOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getFileId, fileId));
        String fileCover = fileInfo.getFileCover();
        File file = new File(MinioConfig.thumbnail + "/" + fileCover);
        try (FileInputStream inputStream = new FileInputStream(file); ServletOutputStream outputStream = response.getOutputStream();) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    @Override
    public boolean urlExist(String url) {
        Long count = shareLinkMapper.selectCount(new LambdaQueryWrapper<ShareLink>().eq(ShareLink::getShareLink, url));
        return count > 0;
    }

    @Override
    public List shareList() {
        long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<ShareLink> queryWrapper = new LambdaQueryWrapper<ShareLink>().eq(ShareLink::getUserId, userId);

        List<ShareLink> shareLinks = shareLinkMapper.selectList(queryWrapper);
        List<Long> linkIds = shareLinks.stream().map(item -> item.getLinkId()).collect(Collectors.toList());

        LambdaQueryWrapper<ShareFile> shareFileLambdaQueryWrapper = new LambdaQueryWrapper<ShareFile>().in(ShareFile::getLinkId, linkIds);
        List<ShareFile> shareFiles = baseMapper.selectList(shareFileLambdaQueryWrapper);

        List<ShareFileAddCountDto> collect = shareFiles.stream().map(item -> {
            ShareFileAddCountDto shareFileAddCountDto = new ShareFileAddCountDto(item);
            for (ShareLink shareLink : shareLinks) {
                if (item.getLinkId().equals(shareLink.getLinkId())) {
                    shareFileAddCountDto.setCount(shareLink.getVisitNum());
                }
            }
//            shareFileAddCountDto.setCount(shareLinkMapper.selectOne(new LambdaQueryWrapper<ShareLink>().eq(ShareLink::getLinkId, item.getLinkId())).getVisitNum());
            return shareFileAddCountDto;
        }).collect(Collectors.toList());

        collect = collect.stream().sorted((o1, o2) -> {
            // 已过期
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (o1.getExpiryTime() != null && o1.getExpiryTime().toInstant().toEpochMilli() < timestamp.toInstant().toEpochMilli()) {
                return 1;
            }

            if (o2.getExpiryTime() != null && o2.getExpiryTime().toInstant().toEpochMilli() < timestamp.toInstant().toEpochMilli()) {
                return -1;
            }

            // 未过期
            // 类型相同
            if (o1.getFolderType().equals(o2.getFolderType())) {
                // 文件夹
                if (o1.getFolderType().equals(FileCode.TYPE_FOLDER)) {
                    return (int) (o1.getCreateTime().toInstant().toEpochMilli() - o2.getCreateTime().toInstant().toEpochMilli());
                }

                return (int) (o2.getCreateTime().toInstant().toEpochMilli() - o1.getCreateTime().toInstant().toEpochMilli());
            } else {
                //类型不同
                return o1.getFolderType() - o2.getFolderType();
            }

        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public void unshare(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
    }



}




