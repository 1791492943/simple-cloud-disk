package com.simpleclouddisk.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShareVo implements Serializable {

    private Long userId;
    private List userFileId;
    private String uuid;

}
