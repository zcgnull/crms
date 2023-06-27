package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (MeetingUpdateRemind)表实体类
 *
 * @author makejava
 * @since 2023-06-27 13:00:32
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("meeting_update_remind")
public class MeetingUpdateRemind{

    @TableId
    private Integer id;
    
    private Integer meetingId;
    
    private Integer userId;

    public MeetingUpdateRemind(Integer meetingId, Integer userId) {
        this.meetingId = meetingId;
        this.userId = userId;
    }
}

