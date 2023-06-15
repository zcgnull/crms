package com.example.crms.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
//分页输出格式
public class PageVo {
    private List rows;
    private Long total;
}