package com.example.crms.constants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "my")
public class MyConstants {
    private int maxLeadTime;
    private int maxDurationTime;
}
