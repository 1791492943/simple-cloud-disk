package com.simpleclouddisk.domain.dto;

import com.simpleclouddisk.domain.entity.UserFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserFileDto {

    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * minio名称
     */
    private String minioName;

    /**
     * 文件上传名称
     */
    private String fileName;

    /**
     * 父级id
     */
    private String filePid;

    /**
     * 进入回收站时间
     */
    private Timestamp recoveryTime;

    /**
     * 0: 目录
     * 1: 文件
     */
    private Integer folderType;

    /**
     * 0: 正常
     * 1: 回收站
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

    public UserFileDto(UserFile userFile) {
        this.id = userFile.getId().toString();
        this.userId = userFile.getUserId().toString();
        this.fileId = userFile.getFileId().toString();
        this.minioName = userFile.getMinioName();
        this.fileName = userFile.getFileName();
        this.filePid = userFile.getFilePid().toString();
        this.recoveryTime = userFile.getRecoveryTime();
        this.folderType = userFile.getFolderType();
        this.delFlag = userFile.getDelFlag();
        this.createTime = userFile.getCreateTime();
        this.updateTime = userFile.getUpdateTime();
    }
}
