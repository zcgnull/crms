<template>
    <el-card style="width: 50%; margin: 5% auto;">
        <el-row style="margin-bottom: 18px;">
            <span style="font-size: 18px; font-weight: 700;">个 人 信 息</span>
        </el-row>
        <el-form label-width="22%" class="frame">
            <el-form-item label="用户ID :">
                <el-input v-model="form.userId" disabled autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="用户名 :">
                <el-input v-model="form.userName" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="邮箱 :">
                <el-input v-model="form.userEmail" autocomplete="off" disabled></el-input>
            </el-form-item>
            <el-form-item label="部门 :">
                <el-select v-model="form.departmentName" placeholder="请选择部门">
                    <el-option v-for="(value, k) in departmentlist" :key="k" :label="value" :value="value"
                        class="select_item">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="角色 :">
                <el-input v-model="form.roleName" autocomplete="off" disabled></el-input>
            </el-form-item>
            <el-button type="info" plain @click="$router.push('/homepage')">退 出</el-button>
            <el-button type="primary" plain @click="save">确 定</el-button>
        </el-form>
    </el-card>
</template>
  
<script>
export default {
    name: "Person",
    data() {
        return {
            form: {},
            departmentlist: [],
        }
    },
    created() {
        this.request.get("/user/userInfo").then(res => {
            if (res.code === 200) {
                this.form = res.data
            }
        })
        this.request.get("/department/allDepartmentNames").then(res => {
            this.departmentlist = res
        })
    },
    methods: {
        save() {
            this.request.put("/user/userInfo", this.form).then(res => {
                // console.log(res)
                if (res.code === 200) {
                    this.$message.success("保存成功")
                    this.$emit("refreshUser")
                    this.$router.push('/homepage')
                } else {
                    this.$message.error(res.msg)
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

.select_item {
    height: 25px;
    line-height: 25px;
    font-size: 12px;
}

.el-form-item__label {
    font-size: 12px;
}
</style>