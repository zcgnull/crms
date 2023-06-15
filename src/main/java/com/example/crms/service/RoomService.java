package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddRoomDto;
import com.example.crms.domain.dto.UpdateRoomDto;
import com.example.crms.domain.entity.Room;
import org.springframework.web.multipart.MultipartFile;

public interface RoomService extends IService<Room> {

    ResponseResult addRoom(AddRoomDto addRoomDto);

    ResponseResult deleteRoom(int roomId);

    ResponseResult updateRoom(UpdateRoomDto updateRoomDto);

    ResponseResult allRoomInfo(int pageNum);

    ResponseResult findRoomByName(String roomName);

    ResponseResult uploadPic(MultipartFile file);

    ResponseResult findRoomByStateOrLocationOrDepartment(Integer roomState, String roomLocation, Integer departmentId);
}
