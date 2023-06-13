package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.service.RoomService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    RoomService roomService;

    @PostMapping("/add")
    @ApiOperation("新增会议室")
    public ResponseResult addRoom(){


        return ResponseResult.okResult(200, "会议室添加成功");
    }

}
