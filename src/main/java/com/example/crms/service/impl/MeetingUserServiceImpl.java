package com.example.crms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.entity.MeetingUser;
import com.example.crms.mapper.MeetingUserMapper;
import com.example.crms.service.MeetingUserService;
import org.springframework.stereotype.Service;

@Service
public class MeetingUserServiceImpl extends ServiceImpl<MeetingUserMapper, MeetingUser> implements MeetingUserService {
}
