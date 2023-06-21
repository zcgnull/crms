package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddMeetingDto;
import com.example.crms.service.MeetingService;
import com.example.crms.service.MeetingUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;
    @Autowired
    private MeetingUserService meetingUserService;

    @PostMapping("/add")
    @ApiOperation("创建会议")
    public ResponseResult addMeeting(@RequestBody AddMeetingDto addMeetingDto){


        return meetingService.addMeeting(addMeetingDto);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除会议")
    public ResponseResult deleteMeeting(@RequestParam int id){
        return meetingService.deleteMeeting(id);
    }

    @PostMapping("/findRoom")
    @ApiOperation("根据开始结束时间查找未占用的会议室")
    public ResponseResult findRoom(@RequestBody AddMeetingDto addMeetingDto){

        return meetingService.findRoom(addMeetingDto);
    }




}
