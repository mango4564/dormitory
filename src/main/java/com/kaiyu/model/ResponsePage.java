package com.kaiyu.model;

import lombok.Data;

import java.util.List;

/**
 * @author mxxxl
 * @date 2021/6/23
 */
@Data
public class ResponsePage {

    private List<?> data;
    private Long total;
    private Integer pageSize;

    public Integer getTotalPages() {
        if (total == null || pageSize == null || pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }
}
