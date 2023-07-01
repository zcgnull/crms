package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.MeetingChoiceDTO;
import com.example.crms.domain.entity.Meeting;
import com.example.crms.service.MeetingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meetingUser")
public class MeetingUserController {

    @Autowired
    private MeetingUserService meetingUserService;
    /**
     *  分页查询邀请信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/invite")
    public ResponseResult getInviteList(Integer pageNum, Integer pageSize){
        return meetingUserService.pageInviteList(pageNum,pageSize);
    }

    /**
     *  用户对邀请信息的选择
     * @param meetingChoiceDTO
     * @return
     */
    @PostMapping("/choice")
    public ResponseResult choiceInvite(@RequestBody MeetingChoiceDTO meetingChoiceDTO){
        return meetingUserService.choiceInvite(meetingChoiceDTO);
    }

    /**
     *  分页查询会议修改信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/update")
    public ResponseResult getUpdateList(Integer pageNum, Integer pageSize){
        return meetingUserService.pageUpdateList(pageNum,pageSize);
    }

    /**
     *  分页查询会议取消信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/delete")
    public ResponseResult getDeleteList(Integer pageNum, Integer pageSize){
        return meetingUserService.pageDeleteList(pageNum,pageSize);
    }

}
