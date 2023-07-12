<template>
    <el-card style="width: 38%; margin: 5% auto;">
        <el-row style="margin-bottom: 18px;">
            <span style="font-size: 18px; font-weight: 700;">修 改 密 码</span>
        </el-row>
        <el-form label-width="25%" :model="form" :rules="rules" ref="pass" class="frame">
            <el-form-item label="原密码:" prop="password">
                <el-input v-model="form.password" autocomplete="off" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码:" prop="newPassword">
                <el-input v-model="form.newPassword" autocomplete="off" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认新密码:" prop="confirmPassword">
                <el-input v-model="form.confirmPassword" autocomplete="off" show-password></el-input>
            </el-form-item>
            <el-button type="info" plain @click="$router.push('/homepage')">退 出</el-button>
            <el-button type="primary" plain @click="save">确 定</el-button>
        </el-form>
    </el-card>
</template>
  
<script>
export default {
    name: "Password",
    data() {
        return {
            form: {},
            rules: {
                password: [
                    { required: true, message: '请输入原密码', trigger: 'blur' },
                    { min: 3, message: '长度不少于3位', trigger: 'blur' }
                ],
                newPassword: [
                    { required: true, message: '请输入新密码', trigger: 'blur' },
                    { min: 3, message: '长度不少于3位', trigger: 'blur' }
                ],
                confirmPassword: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 3, message: '长度不少于3位', trigger: 'blur' }
                ],
            }
        }
    },
    methods: {
        save() {
            this.$refs.pass.validate((valid) => {
                if (valid) {  //合法
                    if (this.form.newPassword !== this.form.confirmPassword) {
                        this.$message.error("两次次输入的新密码不相同")
                        return false
                    }
                    // if (this.user.userPassword === this.form.password) {
                    this.request.put("/user/changePassword?newPassword=" + this.form.newPassword).then(res => {
                        if (res.code === 200) {
                            this.$message.success("修改成功")
                            $router.push('/homepage')
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
  
<style lang="scss">
.frame {
    .el-form-item {
        .el-input {
            width: 90%;
        }

        .el-select {
            width: 90%;
        }
    }
}

.el-form-item__label {
    font-size: 12px;
}
</style>