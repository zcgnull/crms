package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.Role;
import com.example.crms.service.RoleService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    /**
     *  分页查询角色列表
     * @param pageNum
     * @param pageSize
     * @param roleName
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getRoleList(Integer pageNum, Integer pageSize, String roleName){
        return roleService.pageRoleList(pageNum,pageSize,roleName);
    }


    /**
     *  添加角色
     * @param role
     * @return
     */
    @PostMapping("/addRole")
    public ResponseResult addRole(@RequestBody Role role){
        return roleService.addRole(role);

    }

    /**
     * 修改角色：更新角色
     */
    @PutMapping
    public ResponseResult edit(@RequestBody Role role)
    {
        return roleService.updateRole(role);
    }

    /**
     * 删除角色
     * @param roleId
     */
    @DeleteMapping("/roleId")
    public ResponseResult remove(Integer roleId) {
        return roleService.deleteRoleById(roleId);
    }
}
