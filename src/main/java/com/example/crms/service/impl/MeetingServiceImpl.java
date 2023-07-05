package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.constants.MyConstants;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.AddMeetingDto;
import com.example.crms.domain.dto.MeetingTimeDto;
import com.example.crms.domain.dto.UpdateMeetingDto;
import com.example.crms.domain.entity.*;
import com.example.crms.domain.vo.*;
import com.example.crms.mapper.*;
import com.example.crms.service.MeetingService;
import com.example.crms.service.MeetingUserService;
import com.example.crms.utils.BeanCopyUtils;
import com.example.crms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private MeetingEquipmentMapper meetingEquipmentMapper;
    @Autowired
    private MeetingDeleteRemindMapper meetingDeleteRemindMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private MyConstants myConstants;
    @Autowired
    private MeetingUserService meetingUserService;
    @Autowired
    private MeetingUpdateRemindMapper meetingUpdateRemindMapper;
    @Autowired
    private ScheduleMapper scheduleMapper;

    /**
     * 获取所有会议
     * @return
     */
    @Override
    public ResponseResult getMeetings(Integer pageNum, Integer pageSize) {
        //分页对象，传入当前页码及每页的数量
        Page<Meeting> page = new Page(pageNum,pageSize);
        Page<Meeting> records = meetingMapper.selectPage(page, null);
        List<Meeting> meetings = records.getRecords();
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
    public ResponseResult getMyMeetings(Integer pageNum, Integer pageSize) {
        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Meeting> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Meeting::getUserId, userId);
        Page<Meeting> page = new Page(pageNum,pageSize);
        Page<Meeting> records = meetingMapper.selectPage(page, queryWrapper);
        List<Meeting> meetings = records.getRecords();
        Map<String, List<Meeting>> map = new HashMap<>();
        map.put("meetings", meetings);
        return ResponseResult.okResult(200, "获取我预定的会议").ok(map);
    }

    /**
     * 获取当前用户参加的会议
     * @return
     */
    @Override
    public ResponseResult getAttend(Integer pageNum, Integer pageSize) {
        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        List<Meeting> attendMeetingByUserId = meetingMapper.getAttendMeetingByUserId(userId, (pageNum-  1) * pageSize, pageSize);
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
        //查找会议室
        Room room = roomMapper.selectById(meeting.getRoomId());
        //整合
        MeetingVo meetingVo = BeanCopyUtils.copyBean(meeting, MeetingVo.class);
        meetingVo.setMeetingEquipments(meetingEquipments);
        meetingVo.setMeetingUsers(meetingUsers);
        meetingVo.setUserName(user.getUserName());
        meetingVo.setDepartment(department);
        meetingVo.setRoom(room);

        Map<String, MeetingVo> map = new HashMap<>();
        map.put("meeting", meetingVo);
        return ResponseResult.okResult(200, "获取会议信息").ok(map);
    }

//                    int meetingId = meeting.getMeetingId();
//                    List<MeetingUser> meetingUsers = new ArrayList<>();
//                    for (Integer user: addMeetingDto.getUsers()
//                         ) {
//                        MeetingUser meetingUser = new MeetingUser(meetingId,user,0);
//                        meetingUsers.add(meetingUser);
//                    }
//                    meetingUserService.saveOrUpdateBatch(meetingUsers);



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
        //获取所有设备
        List<Equipment> equipments = equipmentMapper.selectList(null);
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
            //判断可选设备是否可以加入
            if (addMeetingDto.getEquipments().size() > 0){
                //找到所有未开始会议
                LambdaQueryWrapper<Meeting> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Meeting::getMeetingState, 0);
                List<Meeting> meetings2 = meetingMapper.selectList(wrapper);

                //对比时间
                for (Meeting meeting : meetings2) {

                    if (startTime.after(meeting.getMeetingEndtime()) || endTime.before(meeting.getMeetingStarttime())){
//                if (roomInfoVoMap.get(meeting.getRoomId()).getState() == 0){
//                    //此会议室可以加入
//                    roomInfoVoMap.get(meeting.getRoomId()).setState(1);
//                }
                    } else {
                        //找到此会议绑定的可选设备
                        LambdaQueryWrapper<MeetingEquipment> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(MeetingEquipment::getMeetingId, meeting.getMeetingId());
                        List<MeetingEquipment> meetingEquipments = meetingEquipmentMapper.selectList(queryWrapper);
                        //双重for 减操作  待优化
                        for (int i = 0; i < equipments.size(); i++) {
                            for (MeetingEquipment meetingEquipment : meetingEquipments) {
                                //有则减
                                if (meetingEquipment.getEquipmentId() == equipments.get(i).getEquipmentId()){
                                    equipments.get(i).setEquipmentNum(equipments.get(i).getEquipmentNum() - meetingEquipment.getEquipmentNum());
                                }
                            }
                        }
                    }
                }
                //对比是否可以获取equipment
                for (Equipment equipment : equipments) {
                    for (Map.Entry<Integer, Integer> integerIntegerEntry : addMeetingDto.getEquipments().entrySet()) {
                        if (equipment.getEquipmentId() == integerIntegerEntry.getKey()){
                            if (equipment.getEquipmentNum() - integerIntegerEntry.getValue() < 0){
                                return ResponseResult.errorResult(400, "就在刚刚" + equipment.getEquipmentName() + "数量不够了");
                            }
                        }
                    }
                }
            }
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

    /**
     * 更新会议信息
     * @param updateMeetingDto
     * @return
     */
    @Override
    public ResponseResult updateMeeting(UpdateMeetingDto updateMeetingDto) {
        Timestamp startTime = updateMeetingDto.getMeetingStarttime();
        Timestamp endTime = updateMeetingDto.getMeetingEndtime();
        //获取此会议
        Meeting meeting = meetingMapper.selectById(updateMeetingDto.getMeetingId());
        //获取可选设备信息
        List<MeetingEquipmentVo> meetingEquipments = meetingMapper.getMeetingEquipmentByMeetingId(meeting.getMeetingId());
        //获取会议人员
        List<MeetingUserVo> meetingUsers = meetingMapper.getMeetingUserByMeetingId(meeting.getMeetingId());
        //获取所有设备
        List<Equipment> equipments = equipmentMapper.selectList(null);

        //判断当前是否能添加次会议，主要是看创建过程中是否有其他人创建
        //查看当前会议室和与当前会议室互斥会议室的未开始会议
        List<Meeting> meetings = meetingMapper.getMettingsByRoomId(updateMeetingDto.getRoomId());
        //查看时间是否冲突
        boolean flag = false;
        for (Meeting meeting1 : meetings) {
            if (startTime.after(meeting1.getMeetingEndtime()) || endTime.before(meeting1.getMeetingStarttime())){

            } else {
                //除去当前会议
                if(meeting1.getMeetingId() != meeting.getMeetingId()){
                    flag = true;
                    break;
                }
            }
        }

        if (!flag){
            //判断可选设备是否可以加入
            if (updateMeetingDto.getEquipments().size() > 0){
                //找到所有未开始会议
                LambdaQueryWrapper<Meeting> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Meeting::getMeetingState, 0);
                List<Meeting> meetings2 = meetingMapper.selectList(wrapper);
                //对比时间
                for (Meeting meeting2 : meetings2) {

                    if (startTime.after(meeting2.getMeetingEndtime()) || endTime.before(meeting2.getMeetingStarttime())){
//                if (roomInfoVoMap.get(meeting.getRoomId()).getState() == 0){
//                    //此会议室可以加入
//                    roomInfoVoMap.get(meeting.getRoomId()).setState(1);
//                }
                    } else {
                        //找到此会议绑定的可选设备
                        LambdaQueryWrapper<MeetingEquipment> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(MeetingEquipment::getMeetingId, meeting2.getMeetingId());
                        List<MeetingEquipment> meetingEquipments1 = meetingEquipmentMapper.selectList(queryWrapper);

                        //双重for 减操作  待优化
                        for (int i = 0; i < equipments.size(); i++) {
                            for (MeetingEquipment meetingEquipment : meetingEquipments1) {
                                //有则减
                                if (meetingEquipment.getEquipmentId() == equipments.get(i).getEquipmentId()){
                                    equipments.get(i).setEquipmentNum(equipments.get(i).getEquipmentNum() - meetingEquipment.getEquipmentNum());
                                }
                            }
                        }
                    }
                }
            }

            //设备与修改后对比
            for (Map.Entry<Integer, Integer> integerIntegerEntry : updateMeetingDto.getEquipments().entrySet()) {
                boolean flag2 = true;
                for (MeetingEquipmentVo meetingEquipment : meetingEquipments) {
                    if (meetingEquipment.getEquipmentId() == integerIntegerEntry.getKey()){
                        //原先有的设备
                        flag2 = false;
                        if (meetingEquipment.getEquipmentNum() < integerIntegerEntry.getValue()){
                            //设备增加个数，判断是否可以继续加入
                            for (Equipment equipment : equipments) {
                                if (equipment.getEquipmentId() == meetingEquipment.getEquipmentId()){
                                    if (equipment.getEquipmentNum() > (integerIntegerEntry.getValue() - meetingEquipment.getEquipmentNum())){
                                        //可以加入
                                    } else {
                                        return ResponseResult.errorResult(400, equipment.getEquipmentName() + "设备数量不足");
                                    }
                                }
                            }
                        }
                    }
                }
                if (flag2) {
                    //新增新设备，判断是否可以继续加入
                    for (Equipment equipment : equipments) {
                        if (equipment.getEquipmentId() == integerIntegerEntry.getKey()){
                            if (equipment.getEquipmentNum() > integerIntegerEntry.getValue()){
                                //可以加入
                            } else {
                                return ResponseResult.errorResult(400, equipment.getEquipmentName() + "设备数量不足");
                            }
                        }
                    }
                }
            }
            Boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(TransactionStatus transactionStatus) {
                    try {
                        //更新meeting表
                        Meeting meetingNew = BeanCopyUtils.copyBean(updateMeetingDto, Meeting.class);
                        meetingMapper.updateById(meetingNew);

                        //更新设备表  删除旧的，添加新的
                        LambdaQueryWrapper<MeetingEquipment> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(MeetingEquipment::getMeetingId, meeting.getMeetingId());
                        meetingEquipmentMapper.delete(queryWrapper);

                        //添加可选设备到meeting_equipment
                        Map<Integer, Integer> equipments = updateMeetingDto.getEquipments();
                        Set<Map.Entry<Integer, Integer>> entries = equipments.entrySet();
                        for (Map.Entry<Integer, Integer> entry : entries) {
                            meetingEquipmentMapper.insert(new MeetingEquipment(0, meeting.getMeetingId(), entry.getKey(), entry.getValue()));
                        }

                        //用户操作
                        List<Integer> usersOld = new ArrayList<>();
                        for (MeetingUserVo meetingUser : meetingUsers) {
                            usersOld.add(meetingUser.getUserId());
                        }
                        List<Integer> usersNew = updateMeetingDto.getUsers();
                        //找到old中不在new中的元素
                        List<Integer> diff1 = new ArrayList<>(usersOld);
                        diff1.removeAll(usersNew);

                        //对于不再参加的用户，发送会议取消提醒，并且删除日程表中对应的开会状态
                        //删除这些user,并向meeting_delete_remind表中插入数据
                        LambdaQueryWrapper<MeetingUser> queryWrapper1 = new LambdaQueryWrapper<>();
                        for (Integer integer : diff1) {
                            queryWrapper1.clear();
                            queryWrapper1.eq(MeetingUser::getMeetingId, meeting.getMeetingId());
                            queryWrapper1.eq(MeetingUser::getUserId, integer);
                            meetingUserMapper.delete(queryWrapper1);
                            //向meeting_delete_remind表中插入数据
                            MeetingDeleteRemind meetingDeleteRemind = new MeetingDeleteRemind();
                            //给对象赋值，并插入到meeting_delete_remind表中
                            meetingDeleteRemind.setMeetingName(meeting.getMeetingName());
                            meetingDeleteRemind.setMeetingStarttime(meeting.getMeetingStarttime());
                            meetingDeleteRemind.setMeetingEndtime(meeting.getMeetingEndtime());
                            meetingDeleteRemind.setUserId(integer);
                            //根据会议预定人的ID，查询其姓名
                            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                            userLambdaQueryWrapper.eq(User::getUserId,meeting.getUserId());
                            User user1 = userMapper.selectOne(userLambdaQueryWrapper);

                            meetingDeleteRemind.setUserName(user1.getUserName());

                            meetingDeleteRemindMapper.insert(meetingDeleteRemind);

                            //删除日程表中对应的开会状态
                            LambdaQueryWrapper<Schedule> scheduleLambdaQueryWrapper = new LambdaQueryWrapper<>();
                            scheduleLambdaQueryWrapper.eq(Schedule::getUserId,integer).eq(Schedule::getMeetingId,meeting.getMeetingId());
                            scheduleMapper.delete(scheduleLambdaQueryWrapper);
                        }
                        //对于不再参加的用户，发送会议取消邮件提醒

                        //找出会议修改前后均在的用户，发送会议修改的提醒,并且修改日程表中对应的开会时间
                        List<Integer> between = new ArrayList<>(usersOld);
                        List<Integer> newUsers = new ArrayList<>(usersNew);
                        between.retainAll(newUsers);
                        if (between.size() > 0 ){
                            for (Integer user:between
                                 ) {
                                MeetingUpdateRemind meetingUpdateRemind = new MeetingUpdateRemind();

                                //给对象赋值，并插入到meeting_update_remind表中
                                meetingUpdateRemind.setMeetingId(meeting.getMeetingId());
                                meetingUpdateRemind.setMeetingName(meeting.getMeetingName());
                                meetingUpdateRemind.setMeetingStarttime(meeting.getMeetingStarttime());
                                meetingUpdateRemind.setMeetingEndtime(meeting.getMeetingEndtime());
                                meetingUpdateRemind.setUserId(user);
                                //根据会议预定人的ID，查询其姓名
                                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                                userLambdaQueryWrapper.eq(User::getUserId,meeting.getUserId());
                                User user1 = userMapper.selectOne(userLambdaQueryWrapper);

                                meetingUpdateRemind.setUserName(user1.getUserName());

                                meetingUpdateRemindMapper.insert(meetingUpdateRemind);

                                //查看该员工是否已经参加会议
                                LambdaQueryWrapper<MeetingUser> meetingUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
                                meetingUserLambdaQueryWrapper.eq(MeetingUser::getUserId,user).eq(MeetingUser::getMeetingId,meeting.getMeetingId()).eq(MeetingUser::getUserReply,1);
                                MeetingUser meetingUser = meetingUserMapper.selectOne(meetingUserLambdaQueryWrapper);

                                if (meetingUser != null) {
                                    //如果会议时间发生修改，先将原开会状态删除，再查找该时间段是否存在其他状态，最后新增开会状态
                                    if (!(meeting.getMeetingStarttime() == updateMeetingDto.getMeetingStarttime() && meeting.getMeetingEndtime() == updateMeetingDto.getMeetingEndtime())) {

                                        //先将原开会状态删除
                                        LambdaQueryWrapper<Schedule> scheduleLambdaQueryWrapper = new LambdaQueryWrapper<>();
                                        scheduleLambdaQueryWrapper.eq(Schedule::getUserId,user).eq(Schedule::getMeetingId,meeting.getMeetingId());
                                        scheduleMapper.delete(scheduleLambdaQueryWrapper);

                                        //查询修改后的时间段内是否有其他状态
                                        List<Schedule> schedules = scheduleMapper.selectExistStatus(user, updateMeetingDto.getMeetingStarttime(), updateMeetingDto.getMeetingEndtime());
                                        //如果有其他状态，先将其他状态删除
                                        if (schedules.size() > 0) {
                                            for (Schedule schedule1 : schedules
                                            ) {
                                                scheduleMapper.deleteById(schedule1.getScheduleId());
                                            }
                                        }

//                                        LambdaUpdateWrapper<Schedule> scheduleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//                                        scheduleLambdaUpdateWrapper.eq(Schedule::getUserId, user).eq(Schedule::getMeetingId, meeting.getMeetingId());
//                                        scheduleLambdaUpdateWrapper.set(Schedule::getScheduleStarttime, updateMeetingDto.getMeetingStarttime());
//                                        scheduleLambdaUpdateWrapper.set(Schedule::getScheduleEndtime, updateMeetingDto.getMeetingEndtime());
//                                        scheduleMapper.update(null, scheduleLambdaUpdateWrapper);

                                        //插入新的开会状态表
                                        //创建状态对象，并赋值
                                        Schedule schedule = new Schedule();
                                        schedule.setScheduleName("开会");
                                        schedule.setScheduleStarttime(updateMeetingDto.getMeetingStarttime());
                                        schedule.setScheduleEndtime(updateMeetingDto.getMeetingEndtime());
                                        schedule.setMeetingId(meeting.getMeetingId());
                                        schedule.setScheduleStatus(0);
                                        schedule.setUserId(user);
                                        scheduleMapper.insert(schedule);

                                    }
                                }
                            }
                        }

                        //发送会议修改的邮件提醒

                        //找到new中不在old中的元素
                        List<Integer> diff2 = new ArrayList<>(usersNew);
                        diff2.removeAll(usersOld);
                        //新增这些user
                        for (Integer integer : diff2) {
                            meetingUserMapper.insert(new MeetingUser(0, meeting.getMeetingId(), integer, 0, null, null)) ;
                        }


                        return true;
                    } catch (Exception e) {
                        System.out.println("    updateMeeting错误为：" + e);
                        //回滚
                        transactionStatus.setRollbackOnly();
                        return false;
                    }
                }
            });
            if (result){
                //判断新旧特殊需求是否改变，改变则进行通知

                //给仍参加会议的用户发送会议修改提醒

                return ResponseResult.okResult(200, "会议修改成功");
            } else {
                return ResponseResult.errorResult(400, "会议修改失败");
            }

        } else {
            return ResponseResult.errorResult(400, "修改时被占用，请重新修改");
        }

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
                    Meeting meeting = meetingMapper.selectById(id);

                    //根据会议预定人的ID，查询其姓名，以供后续使用
                    LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    userLambdaQueryWrapper.eq(User::getUserId,meeting.getUserId());
                    User user1 = userMapper.selectOne(userLambdaQueryWrapper);

                    //根据id删除meeting
                    meetingMapper.deleteById(id);
                    //取消会议提醒
                    //对会议所有邀请人员发送会议取消的通知

                    LambdaQueryWrapper<MeetingUser> meetingUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    meetingUserLambdaQueryWrapper.eq(MeetingUser::getMeetingId, id);
                    List<MeetingUser> meetingUsers = meetingUserMapper.selectList(meetingUserLambdaQueryWrapper);
                    for (MeetingUser meetingUser:meetingUsers
                         ) {
                        MeetingDeleteRemind meetingDeleteRemind = new MeetingDeleteRemind();
                        //给对象赋值，并插入到meeting_delete_remind表中
                        meetingDeleteRemind.setMeetingName(meeting.getMeetingName());
                        meetingDeleteRemind.setMeetingStarttime(meeting.getMeetingStarttime());
                        meetingDeleteRemind.setMeetingEndtime(meeting.getMeetingEndtime());
                        meetingDeleteRemind.setUserId(meetingUser.getUserId());
                        meetingDeleteRemind.setUserName(user1.getUserName());

                        meetingDeleteRemindMapper.insert(meetingDeleteRemind);

                        //删除已参会人员对应时间段的开会状态
                        //判断该用户是否已经参会
                        //已参会
                        if (meetingUser.getUserReply() == 1){
                            //删除日程表中对应的开会状态
                            LambdaQueryWrapper<Schedule> scheduleLambdaQueryWrapper = new LambdaQueryWrapper<>();
                            scheduleLambdaQueryWrapper.eq(Schedule::getUserId,meetingUser.getUserId()).eq(Schedule::getMeetingId,meeting.getMeetingId());
                            scheduleMapper.delete(scheduleLambdaQueryWrapper);
                        }
                    }
                    //进行会议取消邮件提醒

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
     * @param meetingTimeDto
     * @return 返回所有会议室信息，包括是否可选状态和会议室状态
     */
    @Override
    public ResponseResult findRoom(MeetingTimeDto meetingTimeDto) {
        //获取当前用户
        User userNow = SecurityUtils.getUser();

        Timestamp startTime = meetingTimeDto.getMeetingStarttime();
        Timestamp endTime = meetingTimeDto.getMeetingEndtime();
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

    /**
     * 通过时间获取现在可用的设备
     * @param meetingTimeDto
     * @return
     */
    @Override
    public ResponseResult findEquipment(MeetingTimeDto meetingTimeDto) {
        Timestamp startTime = meetingTimeDto.getMeetingStarttime();
        Timestamp endTime = meetingTimeDto.getMeetingEndtime();

        //找到所有未开始会议
        LambdaQueryWrapper<Meeting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Meeting::getMeetingState, 0);
        List<Meeting> meetings = meetingMapper.selectList(wrapper);
        //获取所有设备
        List<Equipment> equipments = equipmentMapper.selectList(null);

        //对比时间
        for (Meeting meeting : meetings) {

            if (startTime.after(meeting.getMeetingEndtime()) || endTime.before(meeting.getMeetingStarttime())){
//                if (roomInfoVoMap.get(meeting.getRoomId()).getState() == 0){
//                    //此会议室可以加入
//                    roomInfoVoMap.get(meeting.getRoomId()).setState(1);
//                }
            } else {
                //找到此会议绑定的可选设备
                LambdaQueryWrapper<MeetingEquipment> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(MeetingEquipment::getMeetingId, meeting.getMeetingId());
                List<MeetingEquipment> meetingEquipments = meetingEquipmentMapper.selectList(queryWrapper);
                //双重for 减操作  待优化
                for (int i = 0; i < equipments.size(); i++) {
                    for (MeetingEquipment meetingEquipment : meetingEquipments) {
                        //有则减
                        if (meetingEquipment.getEquipmentId() == equipments.get(i).getEquipmentId()){
                            equipments.get(i).setEquipmentNum(equipments.get(i).getEquipmentNum() - meetingEquipment.getEquipmentNum());
                        }
                    }
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("equipments", equipments);
        map.put("total", equipments.size());
        return ResponseResult.okResult(200, "设备数量获取").ok(map);
    }


    @Override
    public ResponseResult pageMettingList(Integer pageNum, Integer pageSize, String roomName, Integer status) {

        LambdaQueryWrapper<Meeting> queryWrapper = new LambdaQueryWrapper();


        int roomId = -1;
        if (StringUtils.hasText(roomName)) {

            LambdaQueryWrapper<Room> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Room::getRoomName,roomName);
            Room room = roomMapper.selectOne(lambdaQueryWrapper);
            roomId = room.getRoomId();
        }

        //部门查询
        queryWrapper.eq(StringUtils.hasText(roomName),Meeting::getRoomId,roomId);

        //为0则为未开始，1为历史会议
        queryWrapper.eq(Meeting::getMeetingState, status);

        //根据会议室Id升序排列
        queryWrapper.orderByAsc(Meeting::getRoomId);
        //其次根据会议开始时间升序排列
        queryWrapper.orderByAsc(Meeting::getMeetingStarttime);

        //根据时间判断会议信息是否是历史信息
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//        String format = df.format(new Date());
//        if (status == 1) {
//
//            queryWrapper.ge(Meeting::getMeetingEndtime, format);
//        }
//
//        else {
//            queryWrapper.lt(Meeting::getMeetingEndtime, format);
//        }

        Page page = new Page(pageNum, pageSize);
        Page page1 = page(page, queryWrapper);

        PageVo pageVo = new PageVo();
        pageVo.setTotal(page1.getTotal());

        List<Meeting> meetings = page1.getRecords();


        List<MeetingListVo> meetingListVos = BeanCopyUtils.copyBeanList(meetings, MeetingListVo.class);


        for (MeetingListVo meeting: meetingListVos
             ) {

            //将RoomId转换为RoomName
            Room room = roomMapper.selectById(meeting.getRoomId());
            meeting.setRoomName(room.getRoomName());

            //将UserId转换为userName
            User user = userMapper.selectById(meeting.getUserId());
            meeting.setUserName(user.getUserName());

        }


        pageVo.setRows(meetingListVos);

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult pageRoomMettingList(Integer roomId) {

//        String somedayPlusOne = "";
//        try {
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = dateFormat.parse(someday);
//            Calendar calendar = new GregorianCalendar();
//            calendar.setTime(date);
//            calendar.add(Calendar.DATE, 1);
//            date = calendar.getTime();
//            somedayPlusOne = dateFormat.format(date);
//            System.out.println("加一天后的日期：" + somedayPlusOne);
//        }
//        catch (Exception exception) {
//            exception.printStackTrace();
//        }

        LambdaQueryWrapper<Meeting> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Meeting::getRoomId, roomId);
        //根据会议室Id升序排列
//        queryWrapper.orderByAsc(Meeting::getRoomId);
        //其次根据会议开始时间升序排列
        queryWrapper.orderByAsc(Meeting::getMeetingStarttime);
        List<Meeting> meetings1 = meetingMapper.selectList(queryWrapper);
        List<AllMeetingVo> allMeetingVos = BeanCopyUtils.copyBeanList(meetings1, AllMeetingVo.class);



        //根据时间判断会议信息是否是历史信息
//        queryWrapper.ge(Meeting::getMeetingStarttime, someday);
//        queryWrapper.lt(Meeting::getMeetingEndtime, somedayPlusOne);
//            queryWrapper.lt(Meeting::getMeetingEndtime, someday + )


//        List<Meeting> meetings = meetingMapper.selectList(queryWrapper);
//
//        List<MeetingListVo> meetingListVos = BeanCopyUtils.copyBeanList(meetings, MeetingListVo.class);
//
//
//        for (MeetingListVo meeting: meetingListVos
//        ) {
//
//            //将RoomId转换为RoomName
//            Room room = roomMapper.selectById(meeting.getRoomId());
//            meeting.setRoomName(room.getRoomName());
//
//            //将UserId转换为userName
//            User user = userMapper.selectById(meeting.getUserId());
//            meeting.setUserName(user.getUserName());
//
//        }


        return ResponseResult.okResult(allMeetingVos);
    }


}
