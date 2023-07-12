<template>
    <div>
        <div style="margin: 15px 0">
            <b style="font-size: 18px; color: #323435;">用 户 管 理</b>
        </div>

        <div style="margin: 10px 0">
            <el-input style="width: 200px;margin-right: 5px;" placeholder="请输入姓名" suffix-icon="el-icon-search"
                v-model="userName"></el-input>
            <el-select v-model="departmentName" placeholder="请选择部门">
                <el-option v-for="(value, k) in departmentlist" :key="k" :label="value" :value="value" class="select_item">
                </el-option>
            </el-select>
            <el-button class="ml-10" type="primary" plain @click="search"><i class="el-icon-search"></i>
                搜索</el-button>
            <el-button type="warning" plain @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
        </div>

        <div style="margin: 10px 0">
            <el-button type="primary" plain @click="handleAdd"> 新增用户 <i class="el-icon-circle-plus-outline"></i></el-button>

            <el-popconfirm class="ml-10" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                icon-color="red" title="您确定批量删除选中的数据吗？" @confirm="delBatch">
                <el-button plain type="danger" slot="reference"> 批量删除 <i class="el-icon-remove-outline"></i></el-button>
            </el-popconfirm>
        </div>

        <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="40" align="center"></el-table-column>
            <el-table-column prop="userId" label="ID" width="60" align="center" sortable></el-table-column>
            <el-table-column prop="userName" label="姓名" align="center"></el-table-column>
            <el-table-column prop="userEmail" label="邮箱" align="center" width="300"></el-table-column>
            <el-table-column prop="departmentName" label="部门" align="center"></el-table-column>
            <el-table-column prop="roleName" label="角色" align="center"></el-table-column>
            <el-table-column label="操作" width="300" align="center">
                <template slot-scope="scope">
                    <el-button type="success" plain @click="handleEdit(scope.row)">编辑 <i
                            class="el-icon-edit"></i></el-button>
                    <el-popconfirm class="ml-5" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                        icon-color="red" title="您确定重置该用户密码吗？" @confirm="resetPassword(scope.row.userId)">
                        <el-button class="ml-5" type="info" plain slot="reference">重置密码 <i
                                class="el-icon-warning-outline"></i></el-button>
                    </el-popconfirm>
                    <el-popconfirm class="ml-5" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                        icon-color="red" title="您确定删除吗？" @confirm="del(scope.row.userId)">
                        <el-button class="ml-5" plain type="danger" slot="reference">删除 <i
                                class="el-icon-remove-outline"></i></el-button>
                    </el-popconfirm>
                </template>
            </el-table-column>
        </el-table>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
        </div>

        <el-dialog title="编辑用户信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form label-width="16%" class="frame">
                <el-form-item label="ID:">
                    <el-input v-model="form.userId" autocomplete="off" disabled></el-input>
                </el-form-item>
                <el-form-item label="姓名:">
                    <el-input v-model="form.userName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="邮箱:">
                    <el-input v-model="form.userEmail" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="部门:">
                    <div>
                        <el-select v-model="form.departmentName" placeholder="请选择">
                            <el-option v-for="(value, k) in departmentlist" :key="k" :label="value" :value="value"
                                class="select_item">
                            </el-option>
                        </el-select>
                    </div>
                </el-form-item>
                <el-form-item label="角色:">
                    <div>
                        <el-select v-model="form.roleName" placeholder="请选择">
                            <el-option v-for="(value, k) in rolelist" :key="k" :label="value" :value="value"
                                class="select_item">
                            </el-option>
                        </el-select>
                    </div>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="新增用户信息" :visible.sync="dialogAddVisible" width="30%">
            <el-form label-width="16%" class="frame">
                <el-form-item label="姓名:">
                    <el-input v-model="form.userName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="密码:">
                    <el-input v-model="form.userPassword" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="邮箱:">
                    <el-input v-model="form.userEmail" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="部门:">
                    <div>
                        <el-select v-model="form.departmentName" placeholder="请选择">
                            <el-option v-for="(value, k) in departmentlist" :key="k" :label="value" :value="value"
                                class="select_item">
                            </el-option>
                        </el-select>
                    </div>
                </el-form-item>
                <el-form-item label="角色:">
                    <div>
                        <el-select v-model="form.roleName" placeholder="请选择">
                            <el-option v-for="(value, k) in rolelist" :key="k" :label="value" :value="value"
                                class="select_item">
                            </el-option>
                        </el-select>
                    </div>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogAddVisible = false">取 消</el-button>
                <el-button type="primary" @click="add">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "Usermanage",
    data() {
        return {
            tableData: [],
            multipleSelection: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            form: {},
            dialogFormVisible: false,
            dialogAddVisible: false,
            userName: '',
            departmentName: '',
            rolelist: [],
            departmentlist: [],
        }
    },
    created() {
        // 请求分页查询数据
        this.load()
    },
    methods: {
        load() {
            this.request.get("/user/list", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                }
            }).then(res => {
                // console.log(res.data.rows)
                this.tableData = res.data.rows
                this.total = res.data.total - 0

            })
            this.request.get("/role/allRoleNames").then(res => {
                this.rolelist = res
            })
            this.request.get("/department/allDepartmentNames_Login").then(res => {
                this.departmentlist = res
            })
        },
        handleSizeChange(pageSize) {
            // console.log(pageSize)
            this.pageSize = pageSize
            this.load()
        },
        handleCurrentChange(pageNum) {
            // console.log(pageNum)
            this.pageNum = pageNum
            this.load()
        },
        handleAdd() {
            this.dialogAddVisible = true
            this.form = {}
        },
        resetPassword(id) {
            this.request.put("/user/resetPassword?userId=" + id).then(res => {
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        del(id) {
            this.request.delete("/user/userIds?userIds=" + id).then(res => {
                console.log(res)
                if (res.code === 200) {
                    this.$message.success("删除成功")
                    this.load()
                } else {
                    this.$message.error("删除失败")
                }
            })
        },
        delBatch() {
            let ids = this.multipleSelection.map(v => v.userId)
            this.request.delete("/user/userIds?userIds=" + ids).then(res => {
                console.log(res)
                if (res.code === 200) {
                    this.$message.success("批量删除成功")
                    this.load()
                } else {
                    this.$message.error("批量删除失败")
                }
            })
        },
        handleSelectionChange(val) {
            // console.log(val)  //打印每次选中的数据
            this.multipleSelection = val
        },
        handleEdit(row) {
            this.form = JSON.parse(JSON.stringify(row))
            this.dialogFormVisible = true
        },
        save() {
            // console.log(this.form)
            this.request.put("/user/edit", this.form).then(res => {
                if (res.code === 200) {
                    this.$message.success("保存成功")
                    this.dialogFormVisible = false
                    this.load()
                } else {
                    this.$message.error("保存失败")
                }
            })
        },
        add() {
            this.request.post("/user/add", this.form).then(res => {
                if (res.code === 200) {
                    this.$message.success("保存成功")
                    this.dialogAddVisible = false
                    this.load()
                } else {
                    this.$message.error("保存失败")
                }
            })
        },
        search() {
            this.request.get("/user/list", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    userName: this.userName,
                    departmentName: this.departmentName
                }
            }).then(res => {
                console.log(res)
                this.tableData = res.data.rows
                this.total = res.data.total - 0

            })
        },
        reset() {
            this.userName = ""
            this.departmentName = ""
            this.load()
        },
    }
}
</script>

<style lang="scss">
.headerBg {
    background: #eee !important;
}

.ml-5 {
    margin-left: 5px;
}

.ml-10 {
    margin-left: 10px;
}

p {
    font-size: 10px;
    margin-top: 0px;
}

.select_item {
    height: 25px;
    line-height: 25px;
    font-size: 12px;
}

.el-dialog__title {
    font-size: 15px;
}

.el-form-item__label {
    font-size: 12px;
}

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
</style>