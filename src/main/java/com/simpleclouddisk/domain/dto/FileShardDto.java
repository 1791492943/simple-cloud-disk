package com.simpleclouddisk.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileShardDto {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 分片名
     */
    private String shardName;

    /**
     * 上传时名称
     */
    private String fileName;

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
     * 父级id
     */
    private Long filePid;
}
