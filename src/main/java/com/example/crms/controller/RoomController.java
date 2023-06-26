package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddRoomDto;
import com.example.crms.domain.dto.UpdateRoomDto;
import com.example.crms.domain.entity.Room;
import com.example.crms.service.RoomService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



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

    @DeleteMapping("/delete")
    @ApiOperation("删除会议室")
    public ResponseResult deleteRoom(int roomId){

        return roomService.deleteRoom(roomId);
    }

    @PutMapping("/update")
    @ApiOperation("更新会议室")
    public ResponseResult updateRoom(@RequestBody UpdateRoomDto updateRoomDto){

        return roomService.updateRoom(updateRoomDto);
    }

    @GetMapping("/all")
    @ApiOperation("获取所有会议室信息")
    public ResponseResult getAll(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "10") int pageSize){

        return roomService.allRoomInfo(pageNum, pageSize);
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


    @GetMapping("/info")
    @ApiOperation("获取会议室信息")
    public ResponseResult roomInfo(@RequestParam int roomId){
        Room room = roomService.getById(roomId);
        if (room != null){
            return ResponseResult.okResult(200, "获取会议室信息成功").ok(room);
        } else {
            return ResponseResult.okResult(400, "获取会议室信息失败");
        }
    }

    @GetMapping("/getFixedRoom")
    @ApiOperation("获取组成会议室的房间")
    public ResponseResult getFixedRoom(){
        return roomService.getFixedRoom();
    }

    @GetMapping("/getRoomIds")
    @ApiOperation("为前端提供房间下拉框")
    public ResponseResult getRoomIDs() {
        return roomService.getRoomIds();
    }

}
