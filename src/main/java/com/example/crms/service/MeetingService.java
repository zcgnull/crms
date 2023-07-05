package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddMeetingDto;
import com.example.crms.domain.dto.MeetingTimeDto;
import com.example.crms.domain.dto.UpdateMeetingDto;
import com.example.crms.domain.entity.Meeting;

import java.util.Date;
import java.util.List;

public interface MeetingService extends IService<Meeting> {

    ResponseResult getMeetings(Integer pageNum, Integer pageSize);
    ResponseResult getMyMeetings(Integer pageNum, Integer pageSize);
    ResponseResult getAttend(Integer pageNum, Integer pageSize);
    ResponseResult getMeeting(int meetingId);
    ResponseResult addMeeting(AddMeetingDto addMeetingDto);
    ResponseResult updateMeeting(UpdateMeetingDto updateMeetingDto);
    ResponseResult deleteMeeting(int id);
    ResponseResult findRoom(MeetingTimeDto meetingTimeDto);

    ResponseResult pageMettingList(Integer pageNum, Integer pageSize, String roomName, Integer meetingState);

    ResponseResult pageRoomMettingList(Integer roomId);
    ResponseResult findEquipment(MeetingTimeDto meetingTimeDto);
}
