package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.MeetingChoiceDTO;
import com.example.crms.domain.entity.MeetingUser;

import java.text.ParseException;

public interface MeetingUserService extends IService<MeetingUser> {

    ResponseResult pageInviteList(Integer pageNum, Integer pageSize,String userName,String meetingName);

    ResponseResult choiceInvite(MeetingChoiceDTO meetingChoiceDTO);

    ResponseResult pageUpdateList(Integer pageNum, Integer pageSize,String userName,String meetingName);

    ResponseResult pageDeleteList(Integer pageNum, Integer pageSize,String userName,String meetingName);
}
