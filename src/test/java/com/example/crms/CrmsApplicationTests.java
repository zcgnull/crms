package com.example.crms;

import com.example.crms.utils.EmailUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

@SpringBootTest
class CrmsApplicationTests {


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

}
