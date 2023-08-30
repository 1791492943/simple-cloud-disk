package com.simpleclouddisk.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;

/**
 * 
 * @TableName file_shard
 */
@TableName(value ="file_shard")
@Data
public class FileShard implements Serializable {
    /**
     * 当前文件唯一标识
     */
    @TableId
    private Long shardId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 分片名
     */
    private String shardName;

    /**
     * 文件后缀
     */
    private String suffixName;

    /**
     * 当前分片数
     */
    private Integer currentShard;

    /**
     * 总分片数
     */
    private Integer totalShards;

    /**
     * 是否最后一个分片
     */
    private Integer last;

    /**
     * 文件md5
     */
    private String fileMd5;

    /**
     * 分片md5
     */
    private String shardMd5;

    /**
     * 分片创建时间
     */
    private Timestamp createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}