package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddRoomDto;
import com.example.crms.domain.dto.UpdateRoomDto;
import com.example.crms.domain.entity.*;
import com.example.crms.domain.vo.RoomVo;
import com.example.crms.mapper.*;
import com.example.crms.service.RoomService;
import com.example.crms.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private RoomFixedRoomMapper roomFixedRoomMapper;
    @Autowired
    FixedRoomMapper fixedRoomMapper;
    @Autowired
    RoomDepartmentMapper roomDepartmentMapper;
    @Autowired
    DepartmentMapper departmentMapper;


    /**
     * 添加会议室
     * @param addRoomDto
     * @return
     */
    @Override
    public ResponseResult addRoom(AddRoomDto addRoomDto) {
        System.out.println(addRoomDto.toString());
        if (addRoomDto.getDepartments().size() == 0){
            return ResponseResult.errorResult(400, "会议室添加失败，请添加部门");
        }
        if (addRoomDto.getFixedRoomIds().size() == 0){
            return ResponseResult.errorResult(400, "会议室添加失败，请添加固定房间");
        }
        Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try {
                    //room表添加新记录
                    Room room = BeanCopyUtils.copyBean(addRoomDto, Room.class);
                    room.setRoomState(1);
                    Date date = new Date();
                    Timestamp creattime = new Timestamp(date.getTime());
                    room.setRoomCreattime(creattime);
                    int insert = roomMapper.insert(room);


                    List<Integer> fixedRoomIds = addRoomDto.getFixedRoomIds();
                    for (Integer fixedRoomId : fixedRoomIds) {
                        //room_fixed_room表添加新纪录
                        RoomFixedRoom roomFixedRoom = new RoomFixedRoom();
                        roomFixedRoom.setRoomId(room.getRoomId());
                        roomFixedRoom.setFixedRoomId(fixedRoomId);
                        roomFixedRoomMapper.insert(roomFixedRoom);
                        //更改fixed_room表中的state
                        UpdateWrapper wrapper = new UpdateWrapper();
                        wrapper.eq("fixed_room_id", fixedRoomId);
                        wrapper.setSql("fixed_room_state = fixed_room_state + 1");
                        fixedRoomMapper.update(null, wrapper);
                    }
                    //room_department表添加新纪录
                    List<Integer> departments = addRoomDto.getDepartments();
                    for (Integer departmentId : departments) {
                        RoomDepartment roomDepartment = new RoomDepartment();
                        roomDepartment.setRoomId(room.getRoomId());
                        roomDepartment.setDepartmentId(departmentId);
                        roomDepartmentMapper.insert(roomDepartment);
                    }
                    return true;
                } catch (Exception e) {
                    System.out.println("    addRoom错误为：" + e);
                    //回滚
                    transactionStatus.setRollbackOnly();
                    return false;
                }
            }
        });
        if (result){
            return ResponseResult.okResult(200, "会议室添加成功");
        } else {
            return ResponseResult.errorResult(400, "会议室添加失败");
        }
    }

    public ResponseResult uploadPic(MultipartFile file){
        //取出文件原始名称
        String originalFilename = "";
        try{
            originalFilename = file.getOriginalFilename();
        }catch (Exception e){
            System.out.println(e);
            originalFilename = "noname";
        }
        //为了防止文件名称重复导致覆盖，给每个文件定义一个唯一的名称
        String newFileName = UUID.randomUUID().toString().replace("-","") + originalFilename;
        //获取程序运行目录
        String dirPath = System.getProperty("user.dir");
        //拼接文件存储路径，最终存储到项目的upload目录下
        String path = "/" + "roomPic" + "/"+ newFileName;
        File destFile = new File(dirPath + path);
        //如果upload目录不存在则创建
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        try {
            //将前端传来的文件存储到目标路径
            file.transferTo(destFile);
            // 将相对路径返回给前端，用于显示图片
            // /upload/xxxxxxx.jpg
            System.out.println(dirPath + path);
            //修改图片url地址
            return ResponseResult.okResult(200, "图片保存成功").ok(path);
        } catch (IOException e) {
            return ResponseResult.errorResult(400, "图片保存失败");
        }
    }

    /**
     * 删除会议室
     * @param roomId
     * @return
     */
    public ResponseResult deleteRoom(int roomId){
        //查询此会议室状态，是否可以删除
        Room room = roomMapper.selectById(roomId);
        if (room != null){
            //判断是否可以删除
            if (room.getRoomState() == 2){
                return ResponseResult.errorResult(400, "此会议室不可以删除");
            } else {
                Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean doInTransaction(TransactionStatus transactionStatus) {
                        try {
                            //删除roomPic
                            String dirPath = System.getProperty("user.dir");
                            File destFile = new File(dirPath + room.getRoomPicurl());
                            if (destFile.delete()) {
                                System.out.println(dirPath + room.getRoomPicurl() + "文件删除成功！");
                            } else {
                                System.out.println(dirPath + room.getRoomPicurl() + "文件删除失败！");
                            }
                            //删除room表
                            roomMapper.deleteById(roomId);
                            //找到room_fixed_room表中此会议室对应的fixed_room
                            QueryWrapper queryWrapper = new QueryWrapper();
                            queryWrapper.eq("room_id", roomId);
                            List<RoomFixedRoom> list = roomFixedRoomMapper.selectList(queryWrapper);
                            //更改fixed_room表中的装态
                            for (RoomFixedRoom roomFixedRoom : list) {
                                UpdateWrapper wrapper = new UpdateWrapper();
                                wrapper.eq("fixed_room_id", roomFixedRoom.getFixedRoomId());
                                wrapper.setSql("fixed_room_state = fixed_room_state - 1");
                                fixedRoomMapper.update(null, wrapper);
                            }
                            //删除room_fixed_room表中的记录
                            queryWrapper.clear();
                            queryWrapper.eq("room_id", roomId);
                            roomFixedRoomMapper.delete(queryWrapper);
                            //删除room_department表中记录
                            roomDepartmentMapper.delete(queryWrapper);

                            return true;
                        } catch (Exception e) {
                            System.out.println("    deleteRoom错误为：" + e);
                            //回滚
                            transactionStatus.setRollbackOnly();
                            return false;
                        }
                    }
                });
                if (result){
                    return ResponseResult.okResult(200, "会议室删除成功");
                } else {
                    return ResponseResult.errorResult(400, "会议室删除失败");
                }
            }
        } else {
            return ResponseResult.errorResult(400, "没有此会议室");
        }
    }

    /**
     * 修改会议室信息
     * @param updateRoomDto
     * @return
     */
    public ResponseResult updateRoom(UpdateRoomDto updateRoomDto){
        //查询此会议室状态，是否可以修改
        Room room = roomMapper.selectById(updateRoomDto.getRoomId());
        if (room != null){
            //判断是否可以修改
            if (room.getRoomState() == 3){
                return ResponseResult.errorResult(400, "此会议室不可以修改");
            } else {
                Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                    @Override
                    public Boolean doInTransaction(TransactionStatus transactionStatus) {
                        try {
                            //图片是否需要需改
                            if(!room.getRoomPicurl().equals(updateRoomDto.getRoomPicurl())){
                                //删除roomPic
                                String dirPath = System.getProperty("user.dir");
                                File destFile = new File(dirPath + room.getRoomPicurl());
                                if (destFile.delete()) {
                                    System.out.println(dirPath + room.getRoomPicurl() + "文件删除成功！");
                                } else {
                                    System.out.println(dirPath + room.getRoomPicurl() + "文件删除失败！");
                                }
                            }
                            //修改room表
                            Room updateRoom = BeanCopyUtils.copyBean(updateRoomDto, Room.class);
                            roomMapper.updateById(updateRoom);
                            //修改room_fixed_room和fixed_room中的状态
                            //找到room_fixed_room表中此会议室对应的fixed_room
                            QueryWrapper queryWrapper = new QueryWrapper();
                            queryWrapper.eq("room_id", room.getRoomId());
                            List<RoomFixedRoom> list = roomFixedRoomMapper.selectList(queryWrapper);
                            //更改fixed_room表中的装态
                            for (RoomFixedRoom roomFixedRoom : list) {
                                UpdateWrapper wrapper = new UpdateWrapper();
                                wrapper.eq("fixed_room_id", roomFixedRoom.getFixedRoomId());
                                wrapper.setSql("fixed_room_state = fixed_room_state - 1");
                                fixedRoomMapper.update(null, wrapper);
                            }
                            //删除room_fixed_room表中的记录
                            queryWrapper.clear();
                            queryWrapper.eq("room_id", room.getRoomId());
                            roomFixedRoomMapper.delete(queryWrapper);
                            List<Integer> fixedRoomIds = updateRoomDto.getFixedRoomIds();
                            for (Integer fixedRoomId : fixedRoomIds) {
                                //room_fixed_room表添加新纪录
                                RoomFixedRoom roomFixedRoom = new RoomFixedRoom();
                                roomFixedRoom.setRoomId(room.getRoomId());
                                roomFixedRoom.setFixedRoomId(fixedRoomId);
                                roomFixedRoomMapper.insert(roomFixedRoom);
                                //更改fixed_room表中的state
                                UpdateWrapper wrapper = new UpdateWrapper();
                                wrapper.eq("fixed_room_id", fixedRoomId);
                                wrapper.setSql("fixed_room_state = fixed_room_state + 1");
                                fixedRoomMapper.update(null, wrapper);
                            }
                            //修改room_department
                            //删除room_department表中记录
                            roomDepartmentMapper.delete(queryWrapper);
                            //room_department表添加新纪录
                            List<Integer> departments = updateRoomDto.getDepartments();
                            for (Integer departmentId : departments) {
                                RoomDepartment roomDepartment = new RoomDepartment();
                                roomDepartment.setRoomId(room.getRoomId());
                                roomDepartment.setDepartmentId(departmentId);
                                roomDepartmentMapper.insert(roomDepartment);
                            }
                            return true;
                        } catch (Exception e) {
                            System.out.println("    updateRoom错误为：" + e);
                            //回滚
                            transactionStatus.setRollbackOnly();
                            return false;
                        }
                    }
                });
                if (result){
                    return ResponseResult.okResult(200, "会议室修改成功");
                } else {
                    return ResponseResult.errorResult(400, "会议室修改失败");
                }
            }
        } else {
            return ResponseResult.errorResult(400, "没有此会议室");
        }
    }

    /**
     * 获取所有会议室信息
     * @return
     */
    @Override
    public ResponseResult allRoomInfo(int pageNum, int pageSize) {

        List<RoomVo> roomVoList = new ArrayList<>();
        //分页对象，传入当前页码及每页的数量
        Page<Room> page = new Page(pageNum,pageSize);

        Page<Room> records = roomMapper.selectPage(page, null);
        List<Room> roomRecords = records.getRecords();

        for (Room room : roomRecords) {
            RoomVo roomVo = getRoomOtherInfo(room);
            roomVoList.add(roomVo);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("rooms", roomVoList);
        map.put("total", roomVoList.size());

        return ResponseResult.errorResult(200, "所有会议室信息").ok(map);
    }


    /**
     * 根据名称查询会议室
     * @param roomName
     * @return
     */
    public ResponseResult findRoomByName(String roomName){

        QueryWrapper<Room> queryWrapper = new QueryWrapper();
        queryWrapper.eq("room_name", roomName);
        Room room = roomMapper.selectOne(queryWrapper);
        if (room != null){
            RoomVo roomVo = getRoomOtherInfo(room);
            Map<String, Object> map = new HashMap<>();
            map.put("rooms", roomVo);
            map.put("total", 1);

            return ResponseResult.errorResult(200, "查找会议室").ok(map);
        } else {
            return ResponseResult.okResult(400, "没有此会议室");
        }

    }

    /**
     * 根据条件查询会议室
     * @param roomState  一种
     * @param roomLocation  一种
     * @param departmentId  一种
     * @return
     */
    public ResponseResult findRoomByStateOrLocationOrDepartment(Integer roomState, String roomLocation, Integer departmentId){

        QueryWrapper queryWrapper = new QueryWrapper();
        if (roomState != null){
            queryWrapper.eq("room_state", roomState);
        } else if (roomLocation != null) {
            queryWrapper.eq("room_location", roomLocation);
        }
        List<Room> roomList = roomMapper.selectList(queryWrapper);
        List<Room> roomList1 = new ArrayList<>();
        if (departmentId != null){
            for (Room room : roomList) {
                queryWrapper.clear();
                queryWrapper.eq("room_id", room.getRoomId());
                List<RoomDepartment> roomDepartmentList  = roomDepartmentMapper.selectList(queryWrapper);
                for (RoomDepartment roomDepartment : roomDepartmentList) {
                    if (roomDepartment.getDepartmentId() == departmentId){
                        roomList1.add(room);
                        break;
                    }
                }
            }
        } else {
            roomList1 = roomList;
        }
        List<RoomVo> roomVoList = new ArrayList<>();
        for (Room room : roomList1) {
            RoomVo roomVo = getRoomOtherInfo(room);
            roomVoList.add(roomVo);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("rooms", roomVoList);
        map.put("total", roomVoList.size());

        return ResponseResult.errorResult(200, "查询会议室").ok(map);

    }

    /**
     * 获取固定房间号
     * @return
     */
    @Override
    public ResponseResult getFixedRoom() {

        List<FixedRoom> fixedRoomList = fixedRoomMapper.selectList(null);
        Map<String, Object> map = new HashMap<>();
        map.put("rooms", fixedRoomList);
        map.put("total", fixedRoomList.size());

        return ResponseResult.okResult().ok(map);
    }


    /**
     * 获取会议室的全部信息
     * @param room
     * @return
     */
    private RoomVo getRoomOtherInfo(Room room) {
        RoomVo roomVo = BeanCopyUtils.copyBean(room, RoomVo.class);
        //查找fixed—room
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("room_id", roomVo.getRoomId());
        List<RoomFixedRoom> roomFixedRoomList = roomFixedRoomMapper.selectList(queryWrapper);
        List<FixedRoom> fixedRoomList = new ArrayList<>();
        for (RoomFixedRoom roomFixedRoom : roomFixedRoomList) {
            fixedRoomList.add(fixedRoomMapper.selectById(roomFixedRoom.getFixedRoomId()));
        }
        roomVo.setFixedRooms(fixedRoomList);
        //查找department
        List<RoomDepartment> roomDepartmentList = roomDepartmentMapper.selectList(queryWrapper);
        List<Department> departmentList = new ArrayList<>();
        for (RoomDepartment roomDepartment : roomDepartmentList) {
            departmentList.add(departmentMapper.selectById(roomDepartment.getDepartmentId()));
        }
        roomVo.setDepartments(departmentList);
        //查询equipment
        return roomVo;
    }



}
