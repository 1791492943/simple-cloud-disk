package com.simpleclouddisk.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @TableName file_info
 */
@TableName(value = "file_info")
@Data
public class FileInfo implements Serializable {
    /**
     * 文件id
     */
    @TableId
    private Long fileId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文件md5
     */
    private String fileMd5;

    /**
     * 父级id
     */
    private Long filePid;

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
     * 文件路径
     */
    private String filePath;

    /**
     * 0: 目录
     * 1: 文件
     */
    private Integer folderType;

    /**
     * 1: 视频
     * 2: 音频
     * 3: 图片
     * 4: 文档
     * 5: 其他
     */
    private Integer fileCategory;

    /**
     * 1: 视频
     * 2: 音频
     * 3: 图片
     * 4: pdf
     * 5: doc
     * 6: excel
     * 7: txt
     * 8: code
     * 9: zip
     * 10: 其他
     */
    private Integer fileType;

    /**
     * 0: 转码中
     * 1: 转码失败
     * 2: 转码成功
     */
    private Integer status;

    /**
     *
     */
    private Date recoveryTime;

    /**
     * 0: 正常
     * 1: 回收站
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}