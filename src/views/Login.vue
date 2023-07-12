<template>
    <div class="wrapper">
        <div
            style="margin: 150px auto; background-color:rgba(16, 17, 12, 0.201); width: 25%; padding: 20px; border-radius: 10px">
            <div style="margin: 20px 0; text-align: center; font-size: 22px; color:white;"><b>会议室管理系统</b></div>
            <el-form :model="user" :rules="rules" ref="userForm">
                <el-form-item prop="userEmail">
                    <el-input placeholder="请输入邮箱" style="margin: 5px 0" prefix-icon="el-icon-user" v-model="user.userEmail">
                    </el-input>
                </el-form-item>
                <el-form-item prop="userPassword">
                    <el-input placeholder="请输入密码" style="margin: 5px 0" prefix-icon="el-icon-lock" show-password
                        v-model="user.userPassword"></el-input>
                </el-form-item>
                <el-form-item>
                    <!-- <el-button type="warning" autocomplete="off" @click="$router.push('/register')"
                        style="width: 145px;">注册</el-button> -->
                    <el-button type="info" plain autocomplete="off" @click="$router.push('/forget')"
                        style="width: 25%;">忘记密码</el-button>
                    <el-button type="primary" plain autocomplete="off" @click="login" style="width: 25%;">登 录</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
export default {
    name: "Login",
    data() {
        return {
            user: {},
            rules: {
                userEmail: [{
                    required: true,
                    message: '请输入邮箱',
                    trigger: 'blur'
                },
                {
                    min: 1,
                    max: 20,
                    message: '长度在 1 到 20 个字符',
                    trigger: 'blur'
                }
                ],
                userPassword: [{
                    required: true,
                    message: '请输入密码',
                    trigger: 'blur'
                },
                {
                    min: 1,
                    max: 20,
                    message: '长度在 1 到 20 个字符',
                    trigger: 'blur'
                }
                ],
            }
        }
    },
    methods: {
        login() {
            this.$refs['userForm'].validate((valid) => {
                if (valid) {  // 表单校验合法
                    this.request.post("/user/login", this.user).then(res => {
                        // console.log(res.data)
                        if (res.code === 200) {
                            localStorage.setItem("Token", JSON.stringify(res.data))
                            this.request.get("/user/userInfo").then(res => {
                                localStorage.setItem("user", JSON.stringify(res.data))
                                this.$router.push("/")
                                this.$message.success("登录成功")
                            })
                        } else {
                            this.$message.error(res.msg)
                        }
                    })
                }
            });
        }
    }
}
</script>

<style>
.wrapper {
    height: 100vh;
    background: url("../assets/login.jpg");
    width: 100%;
    position: fixed;
    background-size: 100% 100%;
}
</style>