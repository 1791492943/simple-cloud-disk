package com.simpleclouddisk.domain.dto;

import lombok.Data;

@Data
public class UploadRecordsDto {

    /**
     * 文件md5
     */
    private String fileMd5;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 总分片数
     */
    private Integer totalShards;

    /**
     * 已上传的分片数
     */
    private Integer count;

}
