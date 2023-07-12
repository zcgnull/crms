<template>
    <div>
        <div style="margin: 20px 0">
            <span style="font-size: 18px; font-weight: 700;">个 人 状 态</span>
        </div>

        <div style="margin: 20px 0">
            <el-button type="primary" plain @click="handleAdd"> 新增状态 <i class="el-icon-circle-plus-outline"></i></el-button>

            <el-popconfirm class="ml-10" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                icon-color="red" title="您确定批量删除选中的数据吗？" @confirm="delBatch">
                <el-button type="danger" plain slot="reference"> 批量删除 <i class="
                    el-icon-remove-outline"></i></el-button>
            </el-popconfirm>

            <el-button type="info" plain @click="$router.push('/homepage')" class="ml-10">退出 <i
                    class="el-icon-switch-button"></i></el-button>
        </div>

        <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="60" align="center"></el-table-column>
            <el-table-column prop="scheduleId" label="状态ID" align="center" sortable width="100"></el-table-column>
            <el-table-column prop="scheduleName" label="状态" align="center"></el-table-column>
            <el-table-column prop="scheduleStarttime" label="开始时间" align="center"></el-table-column>
            <el-table-column prop="scheduleEndtime" label="结束时间" align="center"></el-table-column>
            <el-table-column label="操作" align="center">
                <template slot-scope="scope">
                    <el-button type="success" plain @click="handleEdit(scope.row)">修改状态 <i
                            class="el-icon-edit"></i></el-button>
                    <el-popconfirm class="ml-5" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                        icon-color="red" title="您确定删除吗？" @confirm="del(scope.row.scheduleId)">
                        <el-button class="ml-5" plain type="danger" slot="reference">删除状态 <i
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

        <el-dialog title="修改个人状态" :visible.sync="dialogFormVisible" width="30%">
            <el-form label-width="22%" class="frame">
                <el-form-item label="状态:">
                    <el-select v-model="form.scheduleName" placeholder="请选择">
                        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"
                            class="select_item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="开始时间:">
                    <el-date-picker v-model="form.scheduleStarttime" type="datetime" placeholder="选择日期时间" align="right"
                        value-format="yyyy-MM-dd HH:mm:ss" :picker-options="pickerOptions">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="结束时间:">
                    <el-date-picker v-model="form.scheduleEndtime" type="datetime" placeholder="选择日期时间" align="right"
                        value-format="yyyy-MM-dd HH:mm:ss" :picker-options="pickerOptions">
                    </el-date-picker>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="新增个人状态" :visible.sync="dialogAddVisible" width="30%">
            <el-form label-width="22%" class="frame">
                <el-form-item label="状态:">
                    <el-select v-model="form.scheduleName" placeholder="请选择">
                        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"
                            class="select_item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="开始时间:">
                    <el-date-picker v-model="form.scheduleStarttime" type="datetime" placeholder="选择日期时间" align="right"
                        value-format="yyyy-MM-dd HH:mm:ss" :picker-options="pickerOptions">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="结束时间:">
                    <el-date-picker v-model="form.scheduleEndtime" type="datetime" placeholder="选择日期时间" align="right"
                        value-format="yyyy-MM-dd HH:mm:ss" :picker-options="pickerOptions">
                    </el-date-picker>
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
    name: "Status",
    data() {
        return {
            tableData: [],
            multipleSelection: [],
            total: 0,
            pageNum: 1,
            pageSize: 5,
            form: {},
            dialogFormVisible: false,
            dialogAddVisible: false,
            options: [{
                value: '工作',
                label: '工作'
            }, {
                value: '休假',
                label: '休假'
            }, {
                value: '出差',
                label: '出差'
            }],
            value: '',
            pickerOptions: {
                shortcuts: [{
                    text: '今天',
                    onClick(picker) {
                        picker.$emit('pick', new Date());
                    }
                }, {
                    text: '明天',
                    onClick(picker) {
                        const date = new Date();
                        date.setTime(date.getTime() + 3600 * 1000 * 24);
                        picker.$emit('pick', date);
                    }
                }, {
                    text: '一周后',
                    onClick(picker) {
                        const date = new Date();
                        date.setTime(date.getTime() + 3600 * 1000 * 24 * 7);
                        picker.$emit('pick', date);
                    }
                }]
            },
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.request.get("/user/getStatus", {
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
            this.request.delete("/user/deleteStatus?scheduleIds=" + id).then(res => {
                if (res.code === 200) {
                    this.$message.success("删除成功")
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        delBatch() {
            let ids = this.multipleSelection.map(v => v.scheduleId)
            this.request.delete("/user/deleteStatus?scheduleIds=" + ids).then(res => {
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
            this.request.put("/user/statusEdit", this.form).then(res => {
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
            this.request.post("/user/addStatus", this.form).then(res => {
                if (res.code === 200) {
                    this.$message.success("保存成功")
                    this.dialogAddVisible = false
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
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