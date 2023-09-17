package com.simpleclouddisk.domain.dto;

import com.simpleclouddisk.domain.entity.ShareFile;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;

@Data
public class ShareFileAddCountDto {

    /**
     * 文件被分享的id
     */
    private String shareId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 父id
     */
    private String filePid;

    /**
     * 链接id
     */
    private String linkId;

    /**
     * minio名称
     */
    private String minioName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 1: 视频
     * 2: 音频
     * 3: 图片
     * 4: 文档
     * 5: 其他
     */
    private Integer fileCategory;

    /**
     * 0: 目录
     * 1: 文件
     */
    private Integer folderType;

    /**
     * 分享时间
     */
    private Timestamp createTime;

    /**
     * 到期时间
     */
    private Timestamp expiryTime;

    private Long count;

    public ShareFileAddCountDto(ShareFile shareFile) {
        BeanUtils.copyProperties(shareFile, this);
        this.shareId = shareFile.getShareId().toString();
        this.userId = shareFile.getUserId().toString();
        this.fileId = shareFile.getFileId().toString();
        this.filePid = shareFile.getFilePid().toString();
        this.linkId = shareFile.getLinkId().toString();
    }
}
