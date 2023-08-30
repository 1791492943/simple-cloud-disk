package com.simpleclouddisk.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;

/**
 * 用户信息
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId
    private Long userId;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 最后登录时间
     */
    private Timestamp lastTime;

    /**
     * 状态 0: 启用 1: 禁用
     */
    private Integer status;

    /**
     * 使用空间
     */
    private Long useSpace;

    /**
     * 总空间
     */
    private Long totalSpace;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}