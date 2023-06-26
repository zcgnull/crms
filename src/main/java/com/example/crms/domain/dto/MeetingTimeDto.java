package com.example.crms.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingTimeDto {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp meetingStarttime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp meetingEndtime;
}
