package com.example.crms;

import com.example.crms.utils.EmailUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CrmsApplicationTests {


    @Test
    void contextLoads() {
        System.out.println(EmailUtils.isValidEmail(null));
    }

}
