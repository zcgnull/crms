<template>
    <el-menu style="min-height: 100%; overflow: hidden" :default-active="$route.path" class="el-menu-vertical-demo"
        background-color="#373D41" text-color="#fff" active-text-color="#ffd04b" router>
        <el-menu-item index="/homepage">
            <i class="el-icon-menu"></i>
            <span slot="title">首页</span>
        </el-menu-item>
        <el-menu-item index="/order">
            <i class="el-icon-message-solid"></i>
            <span slot="title">预约会议</span>
        </el-menu-item>
        <el-menu-item index="/myorder">
            <i class="el-icon-edit"></i>
            <span slot="title">我的预约</span>
        </el-menu-item>
        <el-menu-item index="/myattend">
            <i class="el-icon-loading"></i>
            <span slot="title">待参加</span>
        </el-menu-item>
        <el-submenu index="0">
            <template slot="title">
                <i class="el-icon-bell"></i>
                <span>消息提醒</span>
            </template>
            <el-menu-item index="/invite">会议邀请</el-menu-item>
            <el-menu-item index="/cancel">会议取消</el-menu-item>
            <el-menu-item index="/alter">会议修改</el-menu-item>
        </el-submenu>
        <el-submenu index="1">
            <template slot="title">
                <i class="el-icon-date"></i>
                <span>日程信息</span>
            </template>
            <el-menu-item index="/schedule">会议室日程</el-menu-item>
            <el-menu-item index="/history">历史日程</el-menu-item>
        </el-submenu>
        <div v-show="display">
            <el-menu-item index="/roommanage">
                <i class="el-icon-office-building"></i>
                <span slot="title">会议室管理</span>
            </el-menu-item>
            <el-menu-item index="/usermanage">
                <i class="el-icon-user"></i>
                <span slot="title">用户管理</span>
            </el-menu-item>
            <el-menu-item index="/rolemanage">
                <i class="el-icon-s-custom"></i>
                <span slot="title">角色管理</span>
            </el-menu-item>
            <el-menu-item index="/departmentmanage">
                <i class="el-icon-notebook-1"></i>
                <span slot="title">部门管理</span>
            </el-menu-item>
            <el-menu-item index="/equipmentmanage">
                <i class="el-icon-takeaway-box"></i>
                <span slot="title">设备管理</span>
            </el-menu-item>
            <el-submenu index="2">
                <template slot="title">
                    <i class="el-icon-bell"></i>
                    <span>需求提醒</span>
                </template>
                <el-menu-item index="/equipment">可选设备</el-menu-item>
                <el-menu-item index="/special">特殊需求</el-menu-item>
            </el-submenu>
        </div>
    </el-menu>
</template>

<script>
export default {
    name: "Aside",
    data() {
        return {
            roleName: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).roleName : "",
            display: false
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            if (this.roleName === '管理员') {
                this.display = true
            }
        }
    },
}
</script>

<style>
.el-menu {
    align-items: center;
    /* 垂直居中 */
    justify-content: center;
    /* 水平居中 */

}

.el-submenu .el-menu-item {
    height: 50px;
    line-height: 50px;
    padding: 0 100px;
    min-width: 200px;
    font-size: 13px;
}

.el-menu {
    border: 0 !important;
}

.el-dropdown-menu__item,
.el-menu-item {
    font-size: 13px;
    padding: 0 27px;
    cursor: pointer;
}

.el-submenu__title {
    font-size: 13px;
    color: #303133;
    padding: 0 120px;
    cursor: pointer;
    transition: border-color .3s, background-color .3s, color .3s;
    box-sizing: border-box;
}
</style>