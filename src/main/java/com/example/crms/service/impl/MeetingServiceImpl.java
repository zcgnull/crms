package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.constants.MyConstants;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddMeetingDto;
import com.example.crms.domain.entity.*;
import com.example.crms.domain.vo.RoomInfoVo;
import com.example.crms.mapper.*;
import com.example.crms.service.FixedRoomService;
import com.example.crms.service.MeetingService;
import com.example.crms.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
    private RoomEquipmentMapper roomEquipmentMapper;
    @Autowired
    private MeetingDeleteRemindMapper meetingDeleteRemindMapper;
    @Autowired
    private MyConstants myConstants;

    @Override
    public ResponseResult getMeetings() {

        return null;
    }

    /**
     * 创建会议
     * @param addMeetingDto
     * @return
     */
    @Override
    public ResponseResult addMeeting(AddMeetingDto addMeetingDto) {

        //验证是否能添加会议，会议室互斥，时间互斥

        Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try {
                    //添加到meenting表
                    Meeting meeting = BeanCopyUtils.copyBean(addMeetingDto, Meeting.class);
                    meeting.setMeetingState(0);
                    meetingMapper.insert(meeting);
                    //添加到meeting_user表，整个部门的人添加，单个人添加
                    for (Integer userid : addMeetingDto.getUsers()) {
                        meetingUserMapper.insert(new MeetingUser(meeting.getMeetingId(), userid));
                    }
                    //添加可选设备到room_equipment
                    Map<Integer, Integer> equipments = addMeetingDto.getEquipments();
                    Set<Map.Entry<Integer, Integer>> entries = equipments.entrySet();
                    for (Map.Entry<Integer, Integer> entry : entries) {
                        roomEquipmentMapper.insert(new RoomEquipment(0, meeting.getRoomId(), entry.getKey(), entry.getValue()));
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

    @Override
    public ResponseResult updateMeeting() {
        return null;
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
            roomInfoVoMap.put(room.getRoomId(), BeanCopyUtils.copyBean(room, RoomInfoVo.class));
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
                roomInfoVoMap.get(meeting.getRoomId()).setState(2);
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


        return ResponseResult.okResult().ok(roomInfoVoList);
    }
}
