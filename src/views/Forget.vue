<template>
    <div class="wrapper">
        <div
            style="margin: 130px auto; background-color:rgba(16, 17, 12, 0.201); width: 50%; padding: 20px; border-radius: 10px">
            <div style="margin: 10px 0; text-align: center; font-size: 22px;color:white;"><b>找 回 密 码</b></div>
            <el-steps :active="active" finish-status="success" align-center>
                <el-step title="确认邮箱"></el-step>
                <el-step title="重置密码"></el-step>
                <el-step title="重置成功"></el-step>
            </el-steps>
            <el-form :model="user">
                <el-input placeholder="请输入邮箱" style="margin: 5px 0; width: 50%;" prefix-icon="el-icon-user"
                    v-model="user.userEmail"></el-input>
            </el-form>
            <el-button style="margin-top: 20px;" @click="$router.push('/login')" type="info" plain>返回登录</el-button>
            <el-button style="margin-top: 20px;" @click="next" type="primary" plain>下一步</el-button>
        </div>
    </div>
</template>

<script>
export default {
    name: "Forget",
    data() {
        return {
            user: {},
            active: 1,
        }
    },
    methods: {
        next() {
            if (this.user.userEmail != undefined) {
                localStorage.setItem("email", this.user.userEmail)
                this.$router.push('/reset')
            } else {
                this.$message.error("邮箱不能为空")
            }
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
