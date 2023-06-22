package com.example.crms.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingUserVo {
    private int userId;
    private String userName;
    private int departmentId;
    private String departmentName;
}
