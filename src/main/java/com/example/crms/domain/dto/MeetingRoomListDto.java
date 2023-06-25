package com.example.crms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomListDto {
    private String meetingName;
    private Timestamp meetingStarttime;
    private Timestamp meetingEndtime;
}
