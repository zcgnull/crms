package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.MeetingChoiceDTO;
import com.example.crms.domain.entity.*;
import com.example.crms.domain.vo.MeetingUserInviteVO;
import com.example.crms.domain.vo.MeetingUserVo;
import com.example.crms.domain.vo.PageVo;
import com.example.crms.mapper.*;
import com.example.crms.service.MeetingUserService;
import com.example.crms.service.UserService;
import com.example.crms.utils.BeanCopyUtils;
import com.example.crms.utils.SecurityUtils;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class MeetingUserServiceImpl extends ServiceImpl<MeetingUserMapper, MeetingUser> implements MeetingUserService {

    @Autowired
    private MeetingUserMapper meetingUserMapper;
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseResult pageInviteList(Integer pageNum, Integer pageSize, String userName,String meetingName) {

        //根据自己Id从meetingUser表中查到对应List集合
        Integer userId = SecurityUtils.getUserId();

        //根据条件限制会议

        LambdaQueryWrapper<User> userLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper1.like(StringUtils.hasText(userName),User::getUserName,userName);
        List<User> users = userMapper.selectList(userLambdaQueryWrapper1);

        ArrayList<Integer> userIds = new ArrayList<>();
        for (User user:users
             ) {
            userIds.add(user.getUserId());
        }

        if (userIds.size() == 0) {
            return ResponseResult.okResult(200,"无数据");
        }
        LambdaQueryWrapper<Meeting> meetingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        meetingLambdaQueryWrapper.like(StringUtils.hasText(meetingName), Meeting::getMeetingName,meetingName);
        meetingLambdaQueryWrapper.in(userIds.size() > 0,Meeting::getUserId,userIds);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String format = df.format(new Date());

        meetingLambdaQueryWrapper.ge(Meeting::getMeetingEndtime, format);


        List<Meeting> meetings = meetingMapper.selectList(meetingLambdaQueryWrapper);
        ArrayList<Integer> meetingIds = new ArrayList<>();
        for (Meeting meeting:meetings
        ) {
            meetingIds.add(meeting.getMeetingId());
        }

        if (meetingIds.size() == 0) {
            return ResponseResult.okResult(200,"无数据");
        }
        LambdaQueryWrapper<MeetingUser> meetingUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        meetingUserLambdaQueryWrapper.eq(MeetingUser::getUserId, userId);

        meetingUserLambdaQueryWrapper.in(meetingIds.size()>0,MeetingUser::getMeetingId,meetingIds);


        Page page = new Page(pageNum, pageSize);
        Page page1 = page(page, meetingUserLambdaQueryWrapper);
        List<MeetingUser> meetingUsers = page1.getRecords();
//        List<MeetingUser> meetingUsers = meetingUserMapper.selectList(meetingUserLambdaQueryWrapper);
        if (meetingUsers == null) {
            return ResponseResult.okResult();
        }
        //从List集合中获取各自的会议信息，封装到Vo中
        List<MeetingUserInviteVO> meetingUserInviteVOs = BeanCopyUtils.copyBeanList(meetingUsers, MeetingUserInviteVO.class);

        for (MeetingUserInviteVO meetingUserVo :meetingUserInviteVOs
             ) {
            //会议信息封装
            Meeting meeting = meetingMapper.selectById(meetingUserVo.getMeetingId());
            meetingUserVo.setMeetingName(meeting.getMeetingName());
            meetingUserVo.setMeetingStarttime(meeting.getMeetingStarttime());
            meetingUserVo.setMeetingEndtime(meeting.getMeetingEndtime());
            meetingUserVo.setMeetingState(meeting.getMeetingState());
            meetingUserVo.setRoomId(meeting.getRoomId());

            //查找会议预定人名称
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getUserId,meeting.getUserId());
            User user1 = userMapper.selectOne(userLambdaQueryWrapper);
            meetingUserVo.setUserName(user1.getUserName());

            //roomName封装
            Room room = roomMapper.selectById(meetingUserVo.getRoomId());
            meetingUserVo.setRoomName(room.getRoomName());

        }
        meetingUserInviteVOs.sort(Comparator.comparing(MeetingUserInviteVO::getMeetingStarttime));

        PageVo pageVo = new PageVo();
        pageVo.setTotal(page1.getTotal());
        pageVo.setRows(meetingUserInviteVOs);
        //返回Vo
        return ResponseResult.okResult(pageVo);
    }

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public ResponseResult choiceInvite(MeetingChoiceDTO meetingChoiceDTO){
        //获取当前用户id并封装
        Integer userId = SecurityUtils.getUserId();
        meetingChoiceDTO.setUserId(userId);

        //获取当前时间并封装
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        meetingChoiceDTO.setReplyTime(ts);

        //类型转换，并且保存更改到meetingUser表中
        MeetingUser meetingUser = BeanCopyUtils.copyBean(meetingChoiceDTO, MeetingUser.class);
        updateById(meetingUser);

        //接受会议，需要将对应时间段改成开会状态
        if (meetingChoiceDTO.getUserReply() == 1) {
            //等到会议的开始和终止时间
            Meeting meeting = meetingMapper.selectById(meetingChoiceDTO.getMeetingId());
            //查询时间段内是否有状态
            List<Schedule> schedules = scheduleMapper.selectExistStatus(userId, meeting.getMeetingStarttime(), meeting.getMeetingEndtime());
            //创建状态对象，并赋值
            Schedule schedule = new Schedule();
            schedule.setScheduleName("开会");
            schedule.setScheduleStarttime(meeting.getMeetingStarttime());
            schedule.setScheduleEndtime(meeting.getMeetingEndtime());
            schedule.setMeetingId(meetingChoiceDTO.getMeetingId());
            schedule.setScheduleStatus(0);
            schedule.setUserId(userId);
            //如果没有状态，直接插入数据库
            if (schedules.size() == 0) {
                scheduleMapper.insert(schedule);
            }
            //如果有状态，先将其它状态删除，再将开会状态插入数据库
            else {
                for (Schedule schedule1:schedules
                     ) {
                    scheduleMapper.deleteById(schedule1.getScheduleId());
                }
                scheduleMapper.insert(schedule);
            }
        }
            return ResponseResult.okResult();
    }

    @Autowired
    private MeetingUpdateRemindMapper meetingUpdateRemindMapper;

    @Override
    public ResponseResult pageUpdateList(Integer pageNum, Integer pageSize,String userName,String meetingName) {
        //根据自己Id从meetingUser表中查到对应List集合
        Integer userId = SecurityUtils.getUserId();

        LambdaQueryWrapper<MeetingUpdateRemind> meetingUpdateRemindLambdaQueryWrapper = new LambdaQueryWrapper<>();
        meetingUpdateRemindLambdaQueryWrapper.eq(MeetingUpdateRemind::getUserId,userId);
        meetingUpdateRemindLambdaQueryWrapper.like(StringUtils.hasText(userName),MeetingUpdateRemind::getUserName,userName);
        meetingUpdateRemindLambdaQueryWrapper.like(StringUtils.hasText(meetingName),MeetingUpdateRemind::getMeetingName,meetingName);
        meetingUpdateRemindLambdaQueryWrapper.orderByDesc(MeetingUpdateRemind::getMeetingStarttime);
//        meetingUpdateRemindLambdaQueryWrapper.in(meetingIds.size()>0,MeetingUpdateRemind::getMeetingId,meetingIds);
//        LambdaQueryWrapper<MeetingUpdateRemind> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(MeetingUpdateRemind::getUserId, userId);

        Page page = new Page(pageNum, pageSize);
        Page page1 = meetingUpdateRemindMapper.selectPage(page, meetingUpdateRemindLambdaQueryWrapper);



        PageVo pageVo = new PageVo();
        pageVo.setTotal(page1.getTotal());
        pageVo.setRows(page1.getRecords());
        //返回Vo
        //向您发起邀请的名称为“meetingName”，开始时间为“”，结束时间为“”的会议发生修改，点击查看修改后的会议信息
        return ResponseResult.okResult(pageVo);
    }

    @Autowired
    private MeetingDeleteRemindMapper meetingDeleteRemindMapper;
    @Override
    public ResponseResult pageDeleteList(Integer pageNum, Integer pageSize,String userName,String meetingName) {
        //根据自己Id从meetingUser表中查到对应List集合
        Integer userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<MeetingDeleteRemind> meetingDeleteRemindLambdaQueryWrapper = new LambdaQueryWrapper<>();


        meetingDeleteRemindLambdaQueryWrapper.eq(MeetingDeleteRemind::getUserId,userId);
        meetingDeleteRemindLambdaQueryWrapper.like(StringUtils.hasText(userName),MeetingDeleteRemind::getUserName,userName);
        meetingDeleteRemindLambdaQueryWrapper.like(StringUtils.hasText(meetingName),MeetingDeleteRemind::getMeetingName,meetingName);
        meetingDeleteRemindLambdaQueryWrapper.orderByDesc(MeetingDeleteRemind::getMeetingStarttime);

        Page page = new Page(pageNum, pageSize);
        Page page1 = meetingDeleteRemindMapper.selectPage(page, meetingDeleteRemindLambdaQueryWrapper);


        PageVo pageVo = new PageVo();
        pageVo.setTotal(page1.getTotal());
        pageVo.setRows(page1.getRecords());
        //返回Vo
        //向您发起邀请的名称为“meetingName”，开始时间为“”，结束时间为“”的会议已经取消
        return ResponseResult.okResult(pageVo);
    }

}
