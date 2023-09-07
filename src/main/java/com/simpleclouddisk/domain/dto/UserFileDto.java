package com.simpleclouddisk.domain.dto;

import com.simpleclouddisk.domain.entity.UserFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

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
     * 大小
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
        BeanUtils.copyProperties(userFile,this);
        this.id =  String.valueOf(userFile.getId());
        this.userId = String.valueOf(userFile.getUserId());
        this.fileId = String.valueOf(userFile.getFileId());
        this.filePid = String.valueOf(userFile.getFilePid());
    }
}
