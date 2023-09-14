package com.simpleclouddisk.domain.dto;

import lombok.Data;

@Data
public class FileListDto {
    /**
     * 父级id
     */
    private Long pid;

    /**
     * 回收站
     */
    private Integer del;

    /**
     * 文件类型
     */
    private Integer category;

}
