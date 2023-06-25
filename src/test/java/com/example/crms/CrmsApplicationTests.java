package com.example.crms;

import com.example.crms.domain.entity.Meeting;
import com.example.crms.domain.entity.User;
import com.example.crms.domain.vo.MeetingEquipmentVo;
import com.example.crms.domain.vo.MeetingUserVo;
import com.example.crms.mapper.MeetingMapper;
import com.example.crms.utils.EmailUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@SpringBootTest
class CrmsApplicationTests {


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    void contextLoads() {
        Date date = new Date();
        java.sql.Date sql_date = new java.sql.Date(date.getTime()); //转换成java.sql.Date
        Timestamp t = new Timestamp(date.getTime());
        System.out.println(date.getTime());
        System.out.println(sql_date);
        System.out.println(t);
    }

    @Test
    void test1(){
        String dirPath = System.getProperty("user.dir");
        File destFile = new File(dirPath + "/roomPic/5c973f8ec43b43c08bf9264d3d256cd8犯人1783号.jpg");
        if (destFile.delete()) {
            System.out.println("文件删除成功！");
        } else {
            System.out.println("文件删除失败！");
        }
    }

    @Test
    void test2(){
//        $2a$10$npv5JSeFR6/wLz8BBMmSBOMb8byg2eyfK4/vvoBk3RKtTLBhIhcpy

//        System.out.println(passwordEncoder.
//                matches("1234",
//                        "$2a$10$npv5JSeFR6/wLz8BBMmSBOMb8byg2eyfK4/vvoBk3RKtTLBhIhcpy"));
        String encode = passwordEncoder.encode("qwe");
//        String encode2 = passwordEncoder.encode("1234");
        System.out.println(encode);
//        System.out.println(encode2);
    }

    @Autowired
    private MeetingMapper meetingMapper;

    @Test
    void test3(){
        List<MeetingEquipmentVo> meetingEquipmentByRoomId = meetingMapper.getMeetingEquipmentByMeetingId(10);
//        for (MeetingEquipmentVo meetingEquipmentVo : meetingEquipmentByRoomId) {
//            System.out.println(meetingEquipmentVo.toString());
//        }
//        System.out.println(meetingEquipmentByRoomId.get(0));
        List<MeetingUserVo> meetingUserByMeetingId = meetingMapper.getMeetingUserByMeetingId(1);
        for (MeetingUserVo meetingUserVo : meetingUserByMeetingId) {
            System.out.println(meetingUserVo.toString());
        }
    }
}
