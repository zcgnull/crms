package com.example.crms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddRoomDto;
import com.example.crms.domain.dto.UpdateRoomDto;
import com.example.crms.domain.entity.Room;
import com.example.crms.service.FixedRoomService;
import com.example.crms.service.RomFixedRoomService;
import com.example.crms.service.RoomService;
import com.example.crms.utils.BeanCopyUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    RoomService roomService;

    @PostMapping("/add")
    @ApiOperation("新增会议室")
    public ResponseResult addRoom(@RequestBody AddRoomDto addRoomDto){

        return roomService.addRoom(addRoomDto);
    }

    /**
     * 接收上传的图片
     * @param file
     * @return
     */
    @PostMapping("/uploadPic")
    public ResponseResult uploadPic(MultipartFile file){

         return roomService.uploadPic(file);
    }

    @GetMapping("/delete")
    @ApiOperation("删除会议室")
    public ResponseResult deleteRoom(int roomId){

        return roomService.deleteRoom(roomId);
    }

    @PostMapping("/update")
    @ApiOperation("更新会议室")
    public ResponseResult updateRoom(@RequestBody UpdateRoomDto updateRoomDto){

        return roomService.updateRoom(updateRoomDto);
    }

    @GetMapping("/all")
    @ApiOperation("获取所有会议室信息")
    public ResponseResult getAll(@RequestParam(defaultValue = "1") int pageNum){

        return roomService.allRoomInfo(pageNum);
    }

    @GetMapping("/find")
    @ApiOperation("根据条件搜索会议室")
    public ResponseResult findRoom(@RequestParam(required=false) String roomName, @RequestParam(required=false) Integer roomState, @RequestParam(required=false) String roomLocation, @RequestParam(required=false) Integer departmentId){
        //优先名称查询
        if (roomName != null){
            return roomService.findRoomByName(roomName);
        } else {
            return roomService.findRoomByStateOrLocationOrDepartment(roomState, roomLocation, departmentId);
        }
    }
}
