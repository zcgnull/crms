package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddMeetingDto;
import com.example.crms.domain.entity.Meeting;

import java.util.Date;
import java.util.List;

public interface MeetingService extends IService<Meeting> {

    ResponseResult getMeetings();
    ResponseResult addMeeting(AddMeetingDto addMeetingDto);
    ResponseResult updateMeeting();
    ResponseResult deleteMeeting(int id);
    ResponseResult findRoom(AddMeetingDto addMeetingDto);

    ResponseResult pageMettingList(Integer pageNum, Integer pageSize, String roomName, Integer status);

    ResponseResult pageRoomMettingList(String someday);
}
