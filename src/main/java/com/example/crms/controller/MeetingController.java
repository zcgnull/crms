package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddMeetingDto;
import com.example.crms.domain.dto.MeetingTimeDto;
import com.example.crms.domain.dto.UpdateMeetingDto;
import com.example.crms.service.MeetingService;
import com.example.crms.service.MeetingUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;


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

    @PutMapping("update")
    @ApiOperation("更新会议信息")
    public ResponseResult updateMeeting(@RequestBody UpdateMeetingDto updateMeetingDto){
        return meetingService.updateMeeting(updateMeetingDto);
    }

    @PostMapping("/findRoom")
    @ApiOperation("根据开始结束时间查找未占用的会议室")
    public ResponseResult findRoom(@RequestBody MeetingTimeDto meetingTimeDto){

        return meetingService.findRoom(meetingTimeDto);
    }

    @PostMapping("/findEquipment")
    @ApiOperation("根据开始结束时间查找未占用的可选设备")
    public ResponseResult findEquipment(@RequestBody MeetingTimeDto meetingTimeDto){

        return meetingService.findEquipment(meetingTimeDto);
    }

    @GetMapping("/info")
    @ApiOperation("获取会议详细信息")
    public ResponseResult getMeeting(@RequestParam int meetingId){
        return meetingService.getMeeting(meetingId);
    }
    /**
     *  分页查询会议室会议信息
     * @param pageNum
     * @param pageSize
     * @param roomName
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getMeetingList(Integer pageNum, Integer pageSize, String roomName, Integer status){
        return meetingService.pageMettingList(pageNum,pageSize,roomName,status);
    }


    /**
     *  根据日期查询会议室会议信息
     * @param someday
     * @return
     */
    @GetMapping("/Roomlist")
    public ResponseResult getRoomMeetingList(String someday){
        return meetingService.pageRoomMettingList(someday);
    }

    @GetMapping("/all")
    @ApiOperation("获取所有会议信息")
    public ResponseResult getMeetings(@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "10")  Integer pageSize){
        return meetingService.getMeetings(pageNum, pageSize);
    }

    @GetMapping("/myMeetings")
    @ApiOperation("获取我的预定的会议信息")
    public ResponseResult getMyMeetings(@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "10")  Integer pageSize){
        return meetingService.getMyMeetings(pageNum, pageSize);
    }

    @GetMapping("/myAttend")
    @ApiOperation("获取我的需要参加的会议信息")
    public ResponseResult getMyAttend(@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "10") Integer pageSize){
        return meetingService.getAttend(pageNum, pageSize);
    }
}
