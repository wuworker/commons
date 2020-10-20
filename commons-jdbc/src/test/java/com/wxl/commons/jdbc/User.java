package com.wxl.commons.jdbc;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Create by wuxingle on 2020/10/20
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = -5259539818526167885L;

    private Integer id;

    private String name;

    private Integer status;

    private Date createTime;

    private Long updateTime;
}
