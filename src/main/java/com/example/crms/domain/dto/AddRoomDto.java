package com.example.crms.domain.dto;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddRoomDto {

    private String roomName;
    private String roomLocation;
    private int roomCapacity;
    private String roomDescription;
    private String roomPicurl;
    private List<Integer> departments;
    private Map<Integer, Integer> equipments;
    private List<Integer> fixedRoomIds;

}
