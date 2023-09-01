package com.simpleclouddisk.domain.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class FilePageDto {
    /**
     * 父级id
     */
    private Integer pid;
    /**
     * 当前页
     */
    @NotNull(message = "当前页不能为空")
    private Integer pageNum;
    /**
     * 显示条数
     */
    @NotNull(message = "每页显示条数不能为空")
    @Min(value = 1)
    @Max(value = 100)
    private Integer pageSize;

    private Integer del;

}
