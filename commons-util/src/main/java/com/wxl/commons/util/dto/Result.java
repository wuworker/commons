package com.wxl.commons.util.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Create by wuxingle on 2024/07/27
 * 结果
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -4157626827842202900L;

    public static final int CODE_SUCCESS = 0;

    public static final String DES_SUCCESS = "成功";

    /**
     * 结果码
     */
    private Integer code;

    /**
     * 描述
     */
    private String desc;

    /**
     * 数据
     */
    private T data;

    /**
     * 当前时间
     */
    private Date time;

    public static <T> Result<T> success(T data) {
        var result = new Result<T>();
        result.setCode(CODE_SUCCESS);
        result.setDesc(DES_SUCCESS);
        result.setData(data);
        result.setTime(new Date());
        return result;
    }

    public static <T> Result<T> fail(Integer code, String desc) {
        var result = new Result<T>();
        result.setCode(code);
        result.setDesc(desc);
        result.setTime(new Date());
        return result;
    }
}
