package com.example.crms.controller;

import com.example.crms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    //所有角色名称信息
    @GetMapping("/allRoleNames")
    public List<String> getAllRoleNames(){

        return roleService.getAllRoleNames();
    }
}
