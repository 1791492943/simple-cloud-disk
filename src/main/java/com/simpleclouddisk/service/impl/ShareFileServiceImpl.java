package com.simpleclouddisk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simpleclouddisk.config.MinioConfig;
import com.simpleclouddisk.domain.dto.ShareFileDto;
import com.simpleclouddisk.domain.dto.UserFileDto;
import com.simpleclouddisk.domain.entity.FileInfo;
import com.simpleclouddisk.domain.entity.ShareFile;
import com.simpleclouddisk.domain.entity.ShareLink;
import com.simpleclouddisk.domain.entity.UserFile;
import com.simpleclouddisk.mapper.FileMapper;
import com.simpleclouddisk.mapper.ShareLinkMapper;
import com.simpleclouddisk.mapper.UserFileMapper;
import com.simpleclouddisk.service.ShareFileService;
import com.simpleclouddisk.mapper.ShareFileMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【share_file】的数据库操作Service实现
 * @createDate 2023-09-12 09:40:35
 */
@Service
public class ShareFileServiceImpl extends ServiceImpl<ShareFileMapper, ShareFile>
        implements ShareFileService {

    @Autowired
    private UserFileMapper userFileMapper;

    @Autowired
    private ShareLinkMapper shareLinkMapper;

    @Autowired
    private FileMapper fileMapper;

    @Override
    public String share(List<Long> fileList, Integer time) {
        List<UserFile> userFiles = userFileMapper.selectBatchIds(fileList);

        String s = Base64Utils.encodeToUrlSafeString(UUID.randomUUID().toString().getBytes());

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ShareLink shareLink = new ShareLink();
        shareLink.setUserId(StpUtil.getLoginIdAsLong());
        shareLink.setShareLink(s);
        shareLink.setVisitNum(0L);
        shareLink.setCreateTime(timestamp);
        shareLink.setExpiryTime(time == 0 ? null : Timestamp.valueOf(timestamp.toLocalDateTime().plusDays(time)) );

        shareLinkMapper.insert(shareLink);

        List<ShareFile> collect = userFiles.stream()
                .map(item -> {
                    ShareFile shareFile = new ShareFile(item);
                    shareFile.setLinkId(shareLink.getLinkId());
                    shareFile.setFileId(item.getId());
                    return shareFile;
                })
                .collect(Collectors.toList());

        this.saveBatch(collect);

        return s;
    }

    @Override
    public List<ShareFileDto> parseUrl(String url) {
        ShareLink shareLink = shareLinkMapper.selectOne(new LambdaQueryWrapper<ShareLink>().eq(ShareLink::getShareLink, url));
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
        try (FileInputStream inputStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream();
        ) {
            IOUtils.copy(inputStream, outputStream);
        }
    }
}




