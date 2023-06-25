package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.constants.MyConstants;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddMeetingDto;
import com.example.crms.domain.dto.UpdateMeetingDto;
import com.example.crms.domain.entity.*;
import com.example.crms.domain.vo.MeetingEquipmentVo;
import com.example.crms.domain.vo.MeetingUserVo;
import com.example.crms.domain.vo.MeetingVo;
import com.example.crms.domain.vo.RoomInfoVo;
import com.example.crms.mapper.*;
import com.example.crms.service.MeetingService;
import com.example.crms.utils.BeanCopyUtils;
import com.example.crms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.*;

@Service
public class MeetingServiceImpl extends ServiceImpl<MeetingMapper, Meeting> implements MeetingService {

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private MeetingUserMapper meetingUserMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private RoomFixedRoomMapper roomFixedRoomMapper;
    @Autowired
    private MeetingEquipmentMapper meetingEquipmentMapper;
    @Autowired
    private MeetingDeleteRemindMapper meetingDeleteRemindMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private MyConstants myConstants;


    /**
     * 获取所有会议
     * @return
     */
    @Override
    public ResponseResult getMeetings() {
        List<Meeting> meetings = meetingMapper.selectList(null);
        Map<String, Object> map = new HashMap<>();
        map.put("meetings", meetings);
        map.put("total", meetings.size());
        return ResponseResult.okResult(200, "获取所有会议室成功").ok(map);
    }

    /**
     * 获取当前用户预定的所有会议
     * @return
     */
    @Override
    public ResponseResult getMyMeetings() {
        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Meeting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Meeting::getUserId, userId);
        List<Meeting> meetings = meetingMapper.selectList(queryWrapper);
        Map<String, List<Meeting>> map = new HashMap<>();
        map.put("meetings", meetings);
        return ResponseResult.okResult(200, "获取我参加的会议").ok(map);
    }

    /**
     * 获取当前用户参加的会议
     * @return
     */
    @Override
    public ResponseResult getAttend() {
        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        List<Meeting> attendMeetingByUserId = meetingMapper.getAttendMeetingByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("meetings", attendMeetingByUserId);
        map.put("total", attendMeetingByUserId.size());
        return ResponseResult.okResult(200,"获取需要参加的会议").ok(map);
    }


    /**
     * 获取会议室详细信息
     * @param meetingId
     * @return
     */
    @Override
    public ResponseResult getMeeting(int meetingId) {

        //获取此会议
        Meeting meeting = meetingMapper.selectById(meetingId);
        //获取此会议创建者信息
        User user = userMapper.selectById(meeting.getUserId());
        Department department = departmentMapper.selectById(user.getDepartmentId());
        //获取可选设备信息
        List<MeetingEquipmentVo> meetingEquipments = meetingMapper.getMeetingEquipmentByMeetingId(meeting.getMeetingId());
        //获取会议人员
        List<MeetingUserVo> meetingUsers = meetingMapper.getMeetingUserByMeetingId(meeting.getMeetingId());
        //整合
        MeetingVo meetingVo = BeanCopyUtils.copyBean(meeting, MeetingVo.class);
        meetingVo.setMeetingEquipments(meetingEquipments);
        meetingVo.setMeetingUsers(meetingUsers);
        meetingVo.setUserName(user.getUserName());
        meetingVo.setDepartment(department);

        Map<String, MeetingVo> map = new HashMap<>();
        map.put("meeting", meetingVo);
        return ResponseResult.okResult(200, "获取会议信息").ok(map);
    }




    /**
     * 创建会议
     * @param addMeetingDto
     * @return
     */
    @Override
    public ResponseResult addMeeting(AddMeetingDto addMeetingDto) {
        Timestamp startTime = addMeetingDto.getMeetingStarttime();
        Timestamp endTime = addMeetingDto.getMeetingEndtime();
        //获取当前用户
        User userNow = SecurityUtils.getUser();
        //判断当前是否能添加次会议，主要是看创建过程中是否有其他人创建
        //查看当前会议室和与当前会议室互斥会议室的未开始会议
        List<Meeting> meetings = meetingMapper.getMettingsByRoomId(addMeetingDto.getRoomId());
        //查看时间是否冲突
        boolean flag = false;
        for (Meeting meeting : meetings) {
            if (startTime.after(meeting.getMeetingEndtime()) || endTime.before(meeting.getMeetingStarttime())){

            } else {
                flag = true;
                break;
            }
        }

        if (!flag){
            Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(TransactionStatus transactionStatus) {
                    try {
                        //添加到meenting表
                        Meeting meeting = BeanCopyUtils.copyBean(addMeetingDto, Meeting.class);
                        meeting.setMeetingState(0);
                        meeting.setUserId(userNow.getUserId());
                        meetingMapper.insert(meeting);
                        //添加到meeting_user表，整个部门的人添加，单个人添加
                        for (Integer userid : addMeetingDto.getUsers()) {
                            meetingUserMapper.insert(new MeetingUser(meeting.getMeetingId(), userid));
                        }
                        //添加可选设备到meeting_equipment
                        Map<Integer, Integer> equipments = addMeetingDto.getEquipments();
                        Set<Map.Entry<Integer, Integer>> entries = equipments.entrySet();
                        for (Map.Entry<Integer, Integer> entry : entries) {
                            meetingEquipmentMapper.insert(new MeetingEquipment(0, meeting.getMeetingId(), entry.getKey(), entry.getValue()));
                        }
                        //更改equipment表中的设备个数

                        //会议通知表操作

                        return true;
                    } catch (Exception e) {
                        System.out.println("    deleteMeeting错误为：" + e);
                        //回滚
                        transactionStatus.setRollbackOnly();
                        return false;
                    }
                }
            });
            if (result){
                return ResponseResult.okResult(200, "会议创建成功");
            } else {
                return ResponseResult.errorResult(400, "会议创建失败");
            }
        }
        return ResponseResult.errorResult(400, "此会议室刚刚已被占用");
    }

    @Override
    public ResponseResult updateMeeting(UpdateMeetingDto updateMeetingDto) {

        //获取此会议
        Meeting meeting = meetingMapper.selectById(updateMeetingDto.getMeetingId());
        //获取可选设备信息
        List<MeetingEquipmentVo> meetingEquipments = meetingMapper.getMeetingEquipmentByMeetingId(meeting.getMeetingId());
        //获取会议人员
        List<MeetingUserVo> meetingUsers = meetingMapper.getMeetingUserByMeetingId(meeting.getMeetingId());

        //与修改后对比
//        for (MeetingEquipmentVo meetingEquipment : meetingEquipments) {
//            for (Map.Entry<Integer, Integer> integerIntegerEntry : updateMeetingDto.getEquipments().entrySet()) {
//                integerIntegerEntry.getKey()
//            }
//        }

        //用户操作
        List<Integer> usersOld = new ArrayList<>();
        for (MeetingUserVo meetingUser : meetingUsers) {
            usersOld.add(meetingUser.getUserId());
        }
        List<Integer> usersNew = updateMeetingDto.getUsers();
        //找到old中不在new中的元素
        List<Integer> diff1 = new ArrayList<>(usersOld);
        diff1.removeAll(usersNew);
        //删除这些user

        //发送不需要参加的通知

        //找到new中不在old中的元素
        List<Integer> diff2 = new ArrayList<>(usersNew);
        diff2.removeAll(usersOld);
        //新增这些user

        //发送邀请会议通知


        return ResponseResult.okResult(200, "会议修改成功").ok(updateMeetingDto);
    }

    /**
     * 删除会议
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteMeeting(int id) {
        Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try {
                    //根据id删除meeting
                    meetingMapper.deleteById(id);
                    //取消会议提醒

                    //进行取消提醒

                    //根据id删除meeting_user
                    LambdaQueryWrapper<MeetingUser> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(MeetingUser::getMeetingId, id);
                    meetingUserMapper.delete(queryWrapper);
                    //删除可选设备
                    LambdaQueryWrapper<MeetingEquipment> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(MeetingEquipment::getMeetingId, id);
                    meetingEquipmentMapper.delete(queryWrapper1);

                    return true;
                } catch (Exception e) {
                    System.out.println("    deleteMeeting错误为：" + e);
                    //回滚
                    transactionStatus.setRollbackOnly();
                    return false;
                }
            }
        });
        if (result){
            return ResponseResult.okResult(200, "会议删除成功");
        } else {
            return ResponseResult.errorResult(400, "会议删除失败");
        }
    }



    /**
     * 根据时间查找符合条件的会议室
     * @param addMeetingDto
     * @return 返回所有会议室信息，包括是否可选状态和会议室状态
     */
    @Override
    public ResponseResult findRoom(AddMeetingDto addMeetingDto) {
        //获取当前用户
        User userNow = SecurityUtils.getUser();

        Timestamp startTime = addMeetingDto.getMeetingStarttime();
        Timestamp endTime = addMeetingDto.getMeetingEndtime();
        //找到所有未开始会议
        LambdaQueryWrapper<Meeting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Meeting::getMeetingState, 0);
        List<Meeting> meetings = meetingMapper.selectList(wrapper);
        //找到所有会议室
        List<Room> rooms = roomMapper.selectList(null);

        Map<Integer, RoomInfoVo> roomInfoVoMap = new HashMap<>();
        for (Room room : rooms) {
            //找到会议室相应的使用部门权限
            List<Department> departments = roomMapper.getDepartmentsByRoomId(room.getRoomId());
            RoomInfoVo roomInfoVo = BeanCopyUtils.copyBean(room, RoomInfoVo.class);
            roomInfoVo.setDepartments(departments);
            //查看使用权限
            boolean flag = false;
            for (Department department : departments) {
                if (department.getDepartmentId() == userNow.getDepartmentId()){
                    flag = true;
                    break;
                }
            }
            if (!flag){
                //用户无权使用此会议室
                roomInfoVo.setState(4);
            }
            roomInfoVoMap.put(room.getRoomId(), roomInfoVo);
        }
        //对比时间
        for (Meeting meeting : meetings) {

            if (startTime.after(meeting.getMeetingEndtime()) || endTime.before(meeting.getMeetingStarttime())){
//                if (roomInfoVoMap.get(meeting.getRoomId()).getState() == 0){
//                    //此会议室可以加入
//                    roomInfoVoMap.get(meeting.getRoomId()).setState(1);
//                }
            } else {
                //此会议室已经被使用
                if (roomInfoVoMap.get(meeting.getRoomId()).getState() != 4){
                    roomInfoVoMap.get(meeting.getRoomId()).setState(2);
                }
                roomInfoVoMap.get(meeting.getRoomId()).getMeetings().add(meeting);
                //查看与此间会议室互斥的会议室
                int roomId = roomInfoVoMap.get(meeting.getRoomId()).getRoomId();
                LambdaQueryWrapper<RoomFixedRoom> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(RoomFixedRoom::getRoomId, roomId);
                List<RoomFixedRoom> roomFixedRoomList = roomFixedRoomMapper.selectList(queryWrapper);
                for (RoomFixedRoom roomFixedRoom : roomFixedRoomList) {
                    //查找使用此房间的会议室
                    queryWrapper.clear();
                    queryWrapper.eq(RoomFixedRoom::getFixedRoomId, roomFixedRoom.getFixedRoomId());
                    List<RoomFixedRoom> roomFixedRoomList1 = roomFixedRoomMapper.selectList(queryWrapper);
                    //更改这些会议室使用状态，并加入其互斥列表
                    for (RoomFixedRoom roomFixedRoom1 : roomFixedRoomList1) {
                        if (roomInfoVoMap.get(roomFixedRoom1.getRoomId()).getState() == 0){
                            roomInfoVoMap.get(roomFixedRoom1.getRoomId()).setState(3);
                            roomInfoVoMap.get(roomFixedRoom1.getRoomId()).getMutexRooms().add(roomInfoVoMap.get(meeting.getRoomId()).getRoomName());
                        }
                    }
                }
            }
        }

        List<RoomInfoVo> roomInfoVoList = new ArrayList<>();
        Iterator<Map.Entry<Integer, RoomInfoVo>> entries = roomInfoVoMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, RoomInfoVo> entry = entries.next();
            roomInfoVoList.add(entry.getValue());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("roomInfo", roomInfoVoList);
        map.put("total", roomInfoVoList.size());
        return ResponseResult.okResult().ok(map);
    }
}
