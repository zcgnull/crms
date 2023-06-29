package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.UserAddDto;
import com.example.crms.domain.dto.UserChangeDto;
import com.example.crms.domain.dto.UserDto;
import com.example.crms.domain.dto.UserStatusDto;
import com.example.crms.domain.entity.*;
import com.example.crms.domain.vo.*;
import com.example.crms.enums.AppHttpCodeEnum;
import com.example.crms.exception.SystemException;
import com.example.crms.mapper.*;
import com.example.crms.service.DepartmentService;
import com.example.crms.service.UserService;
import com.example.crms.utils.*;
import io.swagger.models.auth.In;
import jdk.nashorn.internal.ir.CallNode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.crms.utils.EmailUtils.isValidEmail;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2023-06-12 15:01:42
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private AuthenticationManager authenticationManager;

    //返回用户信息
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ScheduleMapper scheduleMapper;


    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        //测试使用，先将用户Id设置为24
//        Integer userId = 24;
        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        UserInfoVo vo = BeanCopyUtils.copyBean(user, UserInfoVo.class);

        //通过部门Id,得到用户部门名称,并封装到vo对象中
        vo.setDepartmentName(departmentMapper.selectById(user.getDepartmentId()).getDepartmentName());

        //查询所有部门信息
        List<String> allDepartmentNames = departmentService.getAllDepartmentNames();

//        List<Department> departments = departmentMapper.selectList(null);
//
//        //将所有部门信息中的部门名称封装到temp集合中
//        List<String> temp = new ArrayList<>();
//        for (Department department: departments
//             ) {
//            temp.add(department.getDepartmentName());
//        }

        //将所有部门名称封装到vo对象中
        vo.setDepartmentNames(allDepartmentNames);


        //通过用户id，从user_role表中找到roleId
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,vo.getUserId());
        Integer roleId = userRoleMapper.selectOne(queryWrapper).getRoleId();

        //通过roleId,得到roleName
        vo.setRoleName(roleMapper.selectById(roleId).getRoleName());


        return ResponseResult.okResult(vo);
        //UserInfoVo vo = BeanCopyUtils.copyBean(user,UserInfoVo.class);

    }

    @Override
    public User selectOneByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_email", email);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public ResponseResult login(User user) {
        //根据邮箱查找用户
        if (user.getUserEmail() == null){
            return ResponseResult.errorResult(401, "邮箱不能为空");
        }
        if (!isValidEmail(user.getUserEmail())){
            return ResponseResult.errorResult(402, "邮箱格式不正确");
        }

        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserEmail(), user.getUserPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("登录失败");
        }
        //如果认证通过了。使用userID生成一个jwt  jwt存入ResponseResult返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getUserId().toString();
        String jwt = JwtUtil.createJWT(userId);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        //把完整的用户信息存入redis  userid作为key
        redisCache.setCacheObject("login:" + userId, loginUser);

        return new ResponseResult(200, "登录成功", map);

//        QueryWrapper<User> queryWrapper = new QueryWrapper();
//        queryWrapper.eq("user_email", user.getUserEmail());
//        User user1 = userMapper.selectOne(queryWrapper);
//        if (user1 != null){
//            if (user1.getUserPassword().equals(user.getUserPassword())){
//                String token = JWTUtils.generateToken(String.valueOf(user1.getUserId()));
//                return ResponseResult.okResult(200, "登录成功").ok(token);
//            }
//        }
//
//        return ResponseResult.okResult(400, "邮箱或密码错误");
    }

    @Override
    public ResponseResult logout() {
        //获取SecurityContextHolder中的用户id
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        int userId = loginUser.getUser().getUserId();
        //删除redis中的值
        redisCache.deleteObject("login:" + userId);
        return ResponseResult.okResult(200, "注销成功");
    }


    /**
     * 注册新用户事务
     * @param newUser
     * @return
     */
    @Override
    public synchronized boolean registerUser(User newUser) {
        transactionTemplate.setTimeout(4000);
        return transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try {
                    //加密密码
                    String encode = passwordEncoder.encode(newUser.getUserPassword());
                    newUser.setUserPassword(encode);
                    userMapper.insert(newUser);
                    //注册时，默认角色为普通员工
                    UserRole userRole = new UserRole();
                    userRole.setUserId(newUser.getUserId());
                    userRole.setRoleId(3);
                    userRoleMapper.insert(userRole);
                    //.......   业务代码
                    return true;
                } catch (Exception e) {
                    System.out.println("错误为：  " + e);
                    //回滚
                    transactionStatus.setRollbackOnly();
                    return false;
                }
            }
        });
    }

    /**
     * 通过id更改密码
     * @param user
     * @return
     */
    @Override
    public boolean changePassword(User user) {

        int i = userMapper.updateById(user);
        if (i == 1){
            return true;
        } else {
            return false;
        }
    }

    class MyException extends Exception {
        public MyException(String message) {
            super(message);
        }
    }

    //修改用户信息
    @Override
    public ResponseResult updateUserInfo(UserDto userDto) {

        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        //测试使用，先将用户Id设置为24
//        Integer userId = 24;

        userDto.setUserId(userId);
        //根据前端传来的部门名称，得到部门ID
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getDepartmentName,userDto.getDepartmentName());
        Department department = departmentMapper.selectOne(queryWrapper);

        userDto.setDepartmentId(department.getDepartmentId());

        User user = BeanCopyUtils.copyBean(userDto, User.class);

        updateById(user);

        return ResponseResult.okResult();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    //修改密码
    @Override
    public ResponseResult changePassword(String newPassword) {
        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        //测试使用，先将用户Id设置为24
//        Integer userId = 24;
        //根据用户id查询用户信息
        User user = getById(userId);

        String encodePassword = passwordEncoder.encode(newPassword);

        user.setUserPassword(encodePassword);

        updateById(user);
        return ResponseResult.okResult();
    }

    //用户信息展示
    @Override
    public ResponseResult selectUserPage(UserDto userDto, Integer pageNum, Integer pageSize) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();

        //姓名模糊查询
        queryWrapper.like(StringUtils.hasText(userDto.getUserName()),User::getUserName,userDto.getUserName());

        Integer departmentIdTemp = -1;
        if (userDto.getDepartmentName() != null && userDto.getDepartmentName() != "") {

            LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            departmentLambdaQueryWrapper.eq(Department::getDepartmentName,userDto.getDepartmentName());
            Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
            departmentIdTemp = department.getDepartmentId();
        }

        //部门查询
        queryWrapper.eq(StringUtils.hasText(userDto.getDepartmentName()),User::getDepartmentId,departmentIdTemp);

        Page<User> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,queryWrapper);

        List<User> users = page.getRecords();

        List<UserVo> userVoList = users.stream()
                .map(u -> BeanCopyUtils.copyBean(u, UserVo.class))
                .collect(Collectors.toList());

        for (UserVo userVo: userVoList
             ) {
            Department department = departmentMapper.selectById(userVo.getDepartmentId());
            userVo.setDepartmentName(department.getDepartmentName());

            //通过用户id，从user_role表中找到roleId
            LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userRoleLambdaQueryWrapper.eq(UserRole::getUserId,userVo.getUserId());
            Integer roleId = userRoleMapper.selectOne(userRoleLambdaQueryWrapper).getRoleId();

            //通过roleId,得到roleName
            userVo.setRoleName(roleMapper.selectById(roleId).getRoleName());

        }

        PageVo pageVo = new PageVo();
        pageVo.setTotal(page.getTotal());
        pageVo.setRows(userVoList);

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public boolean checkEmailUnique(UserAddDto userAddDto) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getUserEmail,userAddDto.getUserEmail()))==0;
    }

    @Override
    @Transactional
    public ResponseResult addUser(UserAddDto userAddDto) {
//        //密码加密处理
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //根据部门名称查询部门Id,并将其封装到userAddDto中
        LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        departmentLambdaQueryWrapper.eq(Department::getDepartmentName,userAddDto.getDepartmentName());
        Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
        userAddDto.setDepartmentId(department.getDepartmentId());

        //保存到user表中
        User user = BeanCopyUtils.copyBean(userAddDto, User.class);
        save(user);

        //根据user的邮箱，查询其Id
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserEmail,user.getUserEmail());
        Integer userId = getOne(userLambdaQueryWrapper).getUserId();

        //根据userAddDto中的角色名称，得到角色Id
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getRoleName,userAddDto.getRoleName());
        Integer roleId = roleMapper.selectOne(roleLambdaQueryWrapper).getRoleId();

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);

//        if(user.getRoleIds()!=null&&user.getRoleIds().length>0){
//            insertUserRole(user);
//        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserInfoAdmin(Integer userId) {


        User user = getById(userId);

        UserInfoAdminVo userInfoAdminVo = BeanCopyUtils.copyBean(user, UserInfoAdminVo.class);

        //通过用户id，从user_role表中找到roleId
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(UserRole::getUserId,userId);
        Integer roleId = userRoleMapper.selectOne(userRoleLambdaQueryWrapper).getRoleId();

        //通过roleId,得到roleName
        userInfoAdminVo.setRoleName(roleMapper.selectById(roleId).getRoleName());

        //通过部门Id,得到用户部门名称,并封装到vo对象中
        userInfoAdminVo.setDepartmentName(departmentMapper.selectById(user.getDepartmentId()).getDepartmentName());

        return ResponseResult.okResult(userInfoAdminVo);
    }

    @Override
    @Transactional
    public void updateUser(UserChangeDto userChangeDto) {

        User user = BeanCopyUtils.copyBean(userChangeDto, User.class);


        //根据前端传来的部门名称，得到部门ID
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getDepartmentName,userChangeDto.getDepartmentName());
        Department department = departmentMapper.selectOne(queryWrapper);

        user.setDepartmentId(department.getDepartmentId());

        updateById(user);

        //根据userAddDto中的角色名称，得到角色Id
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getRoleName,userChangeDto.getRoleName());
        Integer roleId = roleMapper.selectOne(roleLambdaQueryWrapper).getRoleId();

        LambdaUpdateWrapper<UserRole> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        lambdaUpdateWrapper.eq(UserRole::getUserId,userChangeDto.getUserId());

        lambdaUpdateWrapper.set(UserRole::getRoleId,roleId);

        userRoleMapper.update(null,lambdaUpdateWrapper);

    }

    /**
     * 根据用户id获取权限数组
     * @param userId
     * @return
     */
    @Override
    public List<String> getPermissions(int userId) {
        List<String> permissions = new ArrayList<>();
        User user = userMapper.selectById(userId);
        if (user != null){
            LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserRole::getUserId, user.getUserId());
            List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);
            for (UserRole userRole : userRoles) {
                Role role = roleMapper.selectById(userRole.getRoleId());
                permissions.add(String.valueOf(role.getRolePermission()));
            }
        }
        return permissions;
    }

    //新增用户状态
    @Override
    public ResponseResult addStatus(Schedule schedule) {
        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        //测试使用，先将用户Id设置为24
//        Integer userId = 24;

        schedule.setUserId(userId);

        String scheduleName = schedule.getScheduleName();
        if (scheduleName.equals("休假")||schedule.equals("开会")) {
            schedule.setScheduleStatus(0);
        }
        else schedule.setScheduleStatus(1);

//        LambdaQueryWrapper<Schedule> scheduleLambdaQueryWrapper = new LambdaQueryWrapper<>();

//        scheduleLambdaQueryWrapper.between(Schedule::getScheduleStarttime,schedule.getScheduleStarttime(),schedule.getScheduleEndtime()).or().
//                between(Schedule::getScheduleEndtime,schedule.getScheduleStarttime(),schedule.getScheduleEndtime());

//        List<Schedule> schedules = scheduleMapper.selectList(scheduleLambdaQueryWrapper);

        List<Schedule> schedules = scheduleMapper.selectExistStatus(schedule.getUserId(),
                schedule.getScheduleStarttime(), schedule.getScheduleEndtime());


        if (schedules.size() == 0) {
            scheduleMapper.insert(schedule);
            return ResponseResult.okResult();
        }

        else {
            return ResponseResult.errorResult(AppHttpCodeEnum.Status_EXIST);
//            throw new SystemException(AppHttpCodeEnum.Status_EXIST);
        }
    }

    //用户状态列表分页查询
    @Override
    public ResponseResult getStatus(Integer pageNum, Integer pageSize) {
        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        //测试使用，先将用户Id设置为24
//        Integer userId = 24;

//        LambdaQueryWrapper<Schedule> scheduleLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        scheduleLambdaQueryWrapper.eq(Schedule::getUserId,userId);

        LambdaQueryWrapper<Schedule> scheduleLambdaQueryWrapper = new LambdaQueryWrapper<>();

        scheduleLambdaQueryWrapper.eq(Schedule::getUserId,userId);

        Page page = new Page(pageNum, pageSize);

        scheduleLambdaQueryWrapper.eq(Schedule::getUserId, userId);
        scheduleLambdaQueryWrapper.orderByAsc(Schedule::getScheduleStarttime);
//        LocalDateTime now = LocalDateTime.now();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String format = df.format(new Date());

        scheduleLambdaQueryWrapper.ge(Schedule::getScheduleStarttime, format);

        Page page1 = scheduleMapper.selectPage(page, scheduleLambdaQueryWrapper);


        PageVo pageVo = new PageVo();
        pageVo.setTotal(page1.getTotal());
        pageVo.setRows(page1.getRecords());

//        List<Schedule> schedules = page.getRecords();

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult statusEdit(Schedule schedule) {

        String scheduleName = schedule.getScheduleName();
        if (scheduleName.equals("休假")||schedule.equals("开会")) {
            schedule.setScheduleStatus(0);
        }
        else schedule.setScheduleStatus(1);

        Schedule schedule1 = scheduleMapper.selectById(schedule.getScheduleId());

        //如果没改变时间，则不用再进行时间段的判定
        if (schedule1.getScheduleStarttime().equals(schedule.getScheduleStarttime()) && schedule1.getScheduleEndtime().equals(schedule.getScheduleEndtime())) {
            scheduleMapper.updateById(schedule);
            return ResponseResult.okResult();
        }

        List<Schedule> schedules = scheduleMapper.selectExistStatus(schedule.getUserId(),
                schedule.getScheduleStarttime(), schedule.getScheduleEndtime());

        if (schedules.size() == 0) {
            scheduleMapper.updateById(schedule);
            return ResponseResult.okResult();
        }

        else {
            return ResponseResult.errorResult(AppHttpCodeEnum.Status_EXIST);
//            throw new SystemException(AppHttpCodeEnum.Status_EXIST);
        }

    }

    @Override
    public ResponseResult statusUser(Schedule schedule) {


        List userReducibleVos = new ArrayList<>();

        //得到所有用户
        List<User> users = userMapper.selectList(null);

        ArrayList<Integer> userIds = new ArrayList<>();

        //得到所有用户Id
        for (User user: users
             ) {
            userIds.add(user.getUserId());
        }

        //将不可约的用户排除掉
        //首先得到不可约用户的Id集合
        List<Schedule> schedules = scheduleMapper.selectUserStatus(schedule.getScheduleStarttime(), schedule.getScheduleEndtime());

        ArrayList<Integer> exUserIds = new ArrayList<>();
        if (schedules.size() != 0) {
            for (Schedule schedule1: schedules
            ) {
                exUserIds.add(schedule1.getUserId());
            }

            //根据Id获取对应的姓名
//            List<User> users1 = userMapper.selectBatchIds(exUserIds);
//
//            ArrayList<String> exUserNames = new ArrayList<>();
//            for (User user: users1
//            ) {
//                exUserNames.add(user.getUserName());
//            }

            Collection subtract = CollectionUtils.subtract(userIds, exUserIds);

            for (Object user:subtract
                 ) {
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getUserId, user);
                User user1 = userMapper.selectOne(userLambdaQueryWrapper);
                userReducibleVos.add(user1);
            }

            List list = BeanCopyUtils.copyBeanList(userReducibleVos, UserReducibleVo.class);

            return ResponseResult.okResult(list);

        }
        //所有用户都可约
        else {
            userReducibleVos = BeanCopyUtils.copyBeanList(users, UserReducibleVo.class);
            return ResponseResult.okResult(userReducibleVos);
        }

    }

    //根据部门查找对应用户
    @Override
    public ResponseResult statusUserAndDepartment(Schedule schedule) {


//        List userReducibleVos = new ArrayList<>();

        //得到所有用户
        List<User> users = userMapper.selectList(null);

        ArrayList<Integer> userIds = new ArrayList<>();

        //得到所有用户Id
        for (User user: users
        ) {
            userIds.add(user.getUserId());
        }

        //得到所有不可约的用户Id
        List<Schedule> schedules = scheduleMapper.selectUserStatus(schedule.getScheduleStarttime(), schedule.getScheduleEndtime());
        ArrayList<Integer> exUserIds = new ArrayList<>();
        if (schedules.size() != 0) {
            for (Schedule schedule1 : schedules
            ) {
                exUserIds.add(schedule1.getUserId());
            }
        }

        //所有可约的用户Id
        Collection subtract = CollectionUtils.subtract(userIds, exUserIds);

        List<User> allUsers = userMapper.selectBatchIds(subtract);


        //得到所有用户，包含其对应的部门名称
        List<UserDto> userDtos = BeanCopyUtils.copyBeanList(allUsers, UserDto.class);
        for (UserDto userDto:userDtos
             ) {
            Department department = departmentMapper.selectById(userDto.getDepartmentId());
            userDto.setDepartmentName(department.getDepartmentName());

        }

        //创建部门与用户的List对象
        List<UserStatusDto> userStatusDtos = new ArrayList<>();
        
        List<Department> departments = departmentMapper.selectList(null);
        //给List对象初始化
        for (Department department:departments
             ) {
            UserStatusDto userStatusDto = new UserStatusDto();
            userStatusDto.setDepartmentName(department.getDepartmentName());
            userStatusDtos.add(userStatusDto);
        }
        //给List对象中的用户赋值
        for (UserStatusDto userStatusDto:userStatusDtos
             ) {
            List<UserDto> userDtos1 = new ArrayList<>();
            for (UserDto userDto:userDtos
                 ) {
                if (userDto.getDepartmentName().equals(userStatusDto.getDepartmentName())) {
                    userDtos1.add(userDto);
                }
            }
            userStatusDto.setUserDtoList(userDtos1);
        }

        return ResponseResult.okResult(userStatusDtos);


    }

    @Override
    public ResponseResult UserOfMeeting(Schedule schedule) {

        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();

        List<Schedule> schedules = scheduleMapper.selectUserStatusMeeting(userId, schedule.getScheduleStarttime(), schedule.getScheduleEndtime());

        if (schedules.size() != 0) {
            return ResponseResult.errorResult(550,"您在该时间段处于开会或休假状态，不可预约会议");
        }

        return ResponseResult.okResult(200,"您可以预约会议");
    }
}

