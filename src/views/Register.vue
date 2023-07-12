<template>
    <div class="wrapper">
        <div
            style="margin: 50px auto; background-color:rgba(16, 17, 12, 0.201); width: 350px; height: 480px; padding: 20px; border-radius: 10px">
            <div style="margin: 10px 0; text-align: center; font-size: 22px;"><b>注 册 邮 箱</b></div>
            <el-form :model="user" :rules="rules" ref="userForm">
                <el-form-item prop="userEmail">
                    <el-input placeholder="请输入邮箱" size="medium" style="margin: 5px 0" prefix-icon="el-icon-user"
                        v-model="user.userEmail"></el-input>
                </el-form-item>
                <el-form-item prop="code">
                    <el-input placeholder="请输入验证码" size="medium" style="margin: 5px 0;" prefix-icon="el-icon-check"
                        v-model="user.code"></el-input>
                    <el-button type="primary" round style="float:right;" @click="getCode">获取验证码</el-button>
                </el-form-item>
                <el-form-item prop="userPassword">
                    <el-input placeholder="请输入密码" size="medium" style="margin: 5px 0" prefix-icon="el-icon-lock"
                        show-password v-model="user.userPassword"></el-input>
                </el-form-item>
                <el-form-item prop="confirmPassword">
                    <el-input placeholder="请确认密码" size="medium" style="margin: 5px 0" prefix-icon="el-icon-lock"
                        show-password v-model="user.confirmPassword"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input placeholder="请输入姓名" size="medium" style="margin: 5px 0" prefix-icon="el-icon-s-custom"
                        v-model="user.userName"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-select v-model="user.departmentName" placeholder="请选择部门" size="medium">
                        <el-option v-for="(value, k) in departmentlist" :key="k" :label="value" :value="value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item style="margin: 5px 0; text-align: right">
                    <el-button type="warning" size="small" autocomplete="off" plain
                        @click="$router.push('/login')">返回登录</el-button>
                    <el-button type="success" icon="el-icon-check" circle @click="login" size="medium"></el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
export default {
    name: "Register",
    data() {
        return {
            user: {},
            departmentlist: [],
            rules: {
                userEmail: [
                    { required: true, message: '请输入邮箱', trigger: 'blur' },
                    { min: 5, max: 20, message: '长度在 5 到 20 个字符', trigger: 'blur' }
                ],
                code: [
                    { required: true, message: '请输入验证码', trigger: 'blur' },
                    { min: 5, max: 20, message: '长度在 5 到 20 个字符', trigger: 'blur' }
                ],
                userPassword: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
                ],
                confirmPassword: [
                    { required: true, message: '请再次输入密码', trigger: 'blur' },
                    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
                ],
            }
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.request.get("/department/allDepartmentNames").then(res => {
                this.departmentlist = res
            })
        },
        getCode() {
            this.request.get("/user/verify?email=" + this.user.userEmail).then(res => {
                console.log(res)
            })
        },
        login() {
            this.$refs['userForm'].validate((valid) => {
                if (valid) {  // 表单校验合法
                    if (this.user.userPassword !== this.user.confirmPassword) {
                        this.$message.error("两次输入的密码不一致")
                        return false
                    }
                    this.request.post("/user/register", this.user).then(res => {
                        if (res.code === 200) {
                            this.$message.success("注册成功")
                            this.$router.push("/login")
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

.el-form-item__label {
    font-size: 18px;
}

.el-form-item__label-wrap {
    margin-left: 0px !important;

}

.el-form-item__label {
    word-break: keep-all !important;
}

.el-form-item__content {
    margin: 0 !important;
    width: -webkit-fill-available;
}

.el-input.el-input--medium,
.el-select.el-select--medium {
    width: 100%;
}

.el-form-item.el-form-item--medium {
    display: flex;
    justify-content: start;
}
</style>
