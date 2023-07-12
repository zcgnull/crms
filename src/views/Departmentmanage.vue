<template>
    <div>
        <div style="margin: 15px 0">
            <b style="font-size: 18px; color: #323435;">部 门 管 理</b>
        </div>

        <div style="margin: 10px 0">
            <el-input style="width: 200px;margin-right: 5px;" placeholder="请输入部门" suffix-icon="el-icon-search"
                v-model="departmentName"></el-input>
            <el-button class="ml-5" type="primary" plain @click="search"><i class="el-icon-search"></i>
                搜索</el-button>
            <el-button type="warning" plain @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
        </div>

        <div style="margin: 10px 0">
            <el-button type="primary" @click="handleAdd" plain> 新增部门 <i class="el-icon-circle-plus-outline"></i></el-button>

            <el-popconfirm class="ml-10" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                icon-color="red" title="您确定批量删除选中的数据吗？" @confirm="delBatch">
                <el-button type="danger" plain slot="reference"> 批量删除 <i class="
                    el-icon-remove-outline"></i></el-button>
            </el-popconfirm>
        </div>

        <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="60" align="center"></el-table-column>
            <el-table-column prop="departmentId" label="部门ID" align="center" sortable width="100"></el-table-column>
            <el-table-column prop="departmentName" label="部门名" align="center"></el-table-column>
            <el-table-column label="操作" align="center">
                <!-- eslint-disable-next-line -->
                <template slot-scope="scope">
                    <el-button type="success" plain @click="handleEdit(scope.row)">编辑 <i
                            class="el-icon-edit"></i></el-button>
                    <!-- 气泡确认框 -->
                    <el-popconfirm class="ml-5" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                        icon-color="red" title="您确定删除吗？" @confirm="del(scope.row.departmentId)">
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

        <el-dialog title="编辑部门信息" :visible.sync="dialogFormVisible" width="30%">
            <el-form label-width="18%" class="frame">
                <el-form-item label="部门ID:">
                    <el-input v-model="form.departmentId" autocomplete="off" disabled></el-input>
                </el-form-item>
                <el-form-item label="部门:">
                    <el-input v-model="form.departmentName" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="新增部门信息" :visible.sync="dialogAddVisible" width="25%">
            <el-form label-width="16%" class="frame">
                <el-form-item label="部门:">
                    <el-input v-model="form.departmentName" autocomplete="off"></el-input>
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
    name: "Departmentmanage",
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
            departmentName: '',
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.request.get("/department/list", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                }
            }).then(res => {
                this.tableData = res.data.rows
                this.total = res.data.total - 0

            })
        },
        handleSizeChange(pageSize) {
            this.pageSize = pageSize
            this.load()
        },
        handleCurrentChange(pageNum) {
            this.pageNum = pageNum
            this.load()
        },
        handleAdd() {
            this.dialogAddVisible = true
            this.form = {}
        },
        del(id) {
            this.request.delete("/department/departmentIds?departmentIds=" + id).then(res => {
                if (res.code === 200) {
                    this.$message.success("删除成功")
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        delBatch() {
            let ids = this.multipleSelection.map(v => v.departmentId)
            this.request.delete("/department/departmentIds?departmentIds=" + ids).then(res => {
                if (res.code === 200) {
                    this.$message.success("批量删除成功")
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        handleSelectionChange(val) {
            this.multipleSelection = val
        },
        handleEdit(row) {
            this.form = JSON.parse(JSON.stringify(row))
            this.dialogFormVisible = true
        },
        save() {
            this.request.put("/department/edit", this.form).then(res => {
                if (res.code === 200) {
                    this.$message.success("保存成功")
                    this.dialogFormVisible = false
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        add() {
            this.request.post("/department/addDepartment", this.form).then(res => {
                if (res.code === 200) {
                    this.$message.success("保存成功")
                    this.dialogAddVisible = false
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        search() {
            this.request.get("/department/list", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    departmentName: this.departmentName,
                }
            }).then(res => {
                this.tableData = res.data.rows
                this.total = res.data.total - 0

            })
        },
        reset() {
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