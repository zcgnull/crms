package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.MeetingChoiceDTO;
import com.example.crms.domain.entity.Meeting;
import com.example.crms.service.MeetingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/choice")
    public ResponseResult choiceInvite(@RequestBody MeetingChoiceDTO meetingChoiceDTO){
        return meetingUserService.choiceInvite(meetingChoiceDTO);
    }


}
