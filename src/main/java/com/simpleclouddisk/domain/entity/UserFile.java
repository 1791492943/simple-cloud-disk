package com.simpleclouddisk.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName user_file
 */
@TableName(value = "user_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFile implements Serializable {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文件id
     */
    private Long fileId;

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
    private Long filePid;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}