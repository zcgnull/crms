package com.example.crms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.entity.FixedRoom;
import com.example.crms.mapper.FixedRoomMapper;
import com.example.crms.service.FixedRoomService;
import org.springframework.stereotype.Service;

@Service
public class FixedRoomServiceImpl extends ServiceImpl<FixedRoomMapper, FixedRoom> implements FixedRoomService {
}
