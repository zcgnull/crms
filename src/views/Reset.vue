<template>
    <div class="wrapper">
        <div
            style="margin: 100px auto; background-color:rgba(16, 17, 12, 0.201); width: 50%; padding: 20px; border-radius: 10px">
            <div style="margin: 10px 0; text-align: center; font-size: 22px; color: white;"><b>找 回 密 码</b></div>
            <el-steps :active="active" finish-status="success" align-center>
                <el-step title="确认邮箱"></el-step>
                <el-step title="重置密码"></el-step>
                <el-step title="重置成功"></el-step>
            </el-steps>
            <el-form :model="user" ref="pass">
                <el-form-item>
                    <el-input placeholder="请输入新密码" style="margin: 5px 0; width: 50%;" prefix-icon="el-icon-lock"
                        show-password v-model="user.userPassword"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input placeholder="请确认新密码" style="margin: 5px 0; width: 50%;" prefix-icon="el-icon-lock"
                        show-password v-model="user.confirmPassword"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input placeholder="邮箱" style="margin: 5px 0; width: 50%;" prefix-icon="el-icon-user"
                        v-model="user.userEmail" disabled></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input placeholder="请输入验证码" style="margin: 5px 0;width: 35%;" prefix-icon="el-icon-check"
                        v-model="user.code"></el-input>
                    <el-button type="primary" plain round style="margin-left: 5px;" @click="getCode">获取验证码</el-button>
                </el-form-item>
            </el-form>
            <el-button style="margin-top: 20px;" @click="$router.go(-1)" type="info" plain>上一步</el-button>
            <el-button style="margin-top: 20px;" @click="next" type="primary" plain>下一步</el-button>
        </div>
    </div>
</template>

<script>
export default {
    name: "Reset",
    data() {
        return {
            user: { userEmail: localStorage.getItem('email') },
            active: 2,
        }
    },
    methods: {
        getCode() {
            this.request.get("/user/verify?email=" + this.user.userEmail).then(res => {
                console.log(res)
            })
        },
        next() {
            this.$refs.pass.validate((valid) => {
                if (valid) {  //合法
                    if (this.user.userPassword !== this.user.confirmPassword) {
                        this.$message.error("两次输入的新密码不相同")
                        return false
                    }
                    this.request.post("/user/forget", this.user).then(res => {
                        if (res.code === 200) {
                            this.$router.push('/success')
                        } else {
                            this.$message.error(res.msg)
                        }
                    })
                }
            })
        },
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
