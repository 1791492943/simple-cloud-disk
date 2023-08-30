package com.simpleclouddisk.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.*;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName file_info
 */
@TableName(value = "file_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileInfo implements Serializable {
    /**
     * 文件id
     */
    @TableId
    private Long fileId;

    /**
     * 文件md5
     */
    private String fileMd5;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件封面 (缩略图)
     */
    private String fileCover;

    /**
     * 1: 视频
     * 2: 音频
     * 3: 图片
     * 4: 文档
     * 5: 其他
     */
    private Integer fileCategory;

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