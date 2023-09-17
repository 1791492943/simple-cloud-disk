package com.simpleclouddisk.domain.dto;

import lombok.Data;

@Data
public class FileListDto {
    /**
     * 父级id
     */
    private Long pid;

    /**
     * 页面
     */
    private Integer page;

    /**
     * 文件类型
     */
    private Integer category;

}
