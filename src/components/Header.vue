<template>
    <div style="font-size: 12px; line-height: 60px;">
        <div style="height: 60px; line-height: 60px; text-align:center;float: left;">
            <img src="../assets/logo.png" style="width: 25px; position: relative; top: 5px; right: 5px">
            <b style="color:#e9e9e9; font-size: 20px;">会议室管理平台</b>
        </div>
        <div style="float: right;">
            <el-dropdown style="float: right; cursor: pointer;">
                <div style="display: inline-block">
                    <div class="icon-size">
                        <i class="el-icon-s-custom" style="margin: 0 5px;color: #e9e9e9;"></i>
                    </div>
                    <span style="margin: 0 2px;font-size: 14px;color: #e9e9e9;">{{ user.userName }}</span><i
                        class="el-icon-arrow-down" style="margin-left: 5px;color: #e9e9e9;"></i>
                </div>
                <el-dropdown-menu slot="dropdown" style="width:80px; text-align:center">
                    <el-dropdown-item style="font-size: 12px; padding: 5px 0">
                        <router-link to="/person" style="text-decoration: none">个人信息</router-link>
                    </el-dropdown-item>
                    <el-dropdown-item style="font-size: 12px; padding: 5px 0">
                        <router-link to="/password" style="text-decoration: none">修改密码</router-link>
                    </el-dropdown-item>
                    <el-dropdown-item style="font-size: 12px; padding: 5px 0">
                        <router-link to="/status" style="text-decoration: none">个人状态</router-link>
                    </el-dropdown-item>
                    <el-dropdown-item style="font-size: 12px; padding: 5px 0">
                        <span style="text-decoration: none" @click="logout">退出</span>
                    </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
        }
    },
    name: "Header",
    props: ["user"],
    methods: {
        logout() {
            this.request.get("/user/logout").then(res => {
                console.log(res)
                if (res.code === 200) {
                    localStorage.removeItem("user")
                    localStorage.removeItem("Token")
                    localStorage.removeItem("email")
                    this.$router.push("/login")
                    this.$message.success("退出成功")
                } else {
                    this.$message.error(res.msg)
                }

            })
        },
    }
}
</script>

<style>
.icon-size {
    font-size: 25px;
    display: inline-block;
}
</style>