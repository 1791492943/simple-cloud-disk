package com.simpleclouddisk.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName share_link
 */
@TableName(value ="share_link")
@Data
public class ShareLink implements Serializable {
    /**
     * 链接id
     */
    @TableId
    private Long linkId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 链接
     */
    private String shareLink;

    /**
     * 访问次数
     */
    private Long visitNum;

    /**
     * 分享时间
     */
    private Date createTime;

    /**
     * 到期时间
     */
    private Date expiryTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}