<template>
    <div>
        <div style="margin: 20px 0">
            <b style="font-size: 18px; color: #323435;">会 议 室 管 理</b>
        </div>

        <div style="margin: 10px 0">
            <el-input style="width: 200px;margin-right: 5px;" placeholder="请输入会议室" suffix-icon="el-icon-search"
                v-model="roomName"></el-input>
            <el-button class="ml-5" plain type="primary" @click="search"><i class="el-icon-search"></i>
                搜索</el-button>
            <el-button type="warning" plain @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
        </div>

        <div style="margin: 10px 0">
            <el-button type="primary" plain @click="handleAdd"> 新增会议室 <i
                    class="el-icon-circle-plus-outline"></i></el-button>

            <el-popconfirm class="ml-10" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                icon-color="red" title="您确定批量删除选中的数据吗？" @confirm="delBatch">
                <el-button type="danger" plain slot="reference"> 批量删除 <i class="
                    el-icon-remove-outline"></i></el-button>
            </el-popconfirm>
        </div>

        <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center"></el-table-column>
            <el-table-column prop="roomId" label="会议室ID" align="center" sortable></el-table-column>
            <el-table-column prop="roomName" label="会议室" align="center"></el-table-column>
            <el-table-column prop="roomState" label="会议室使用状态" align="center"></el-table-column>
            <el-table-column prop="roomLocation" label="会议室位置" align="center"></el-table-column>
            <el-table-column prop="roomCapacity" label="限制人数" align="center"></el-table-column>
            <el-table-column label="操作" align="center" width="300">
                <template slot-scope="scope">
                    <el-button type="info" plain @click="handleDetail(scope.row)">详情 <i
                            class="el-icon-thumb"></i></el-button>
                    <el-button type="success" plain @click="handleEdit(scope.row)">编辑 <i
                            class="el-icon-edit"></i></el-button>
                    <el-popconfirm class="ml-5" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                        icon-color="red" title="您确定删除吗？" @confirm="del(scope.row.roomId)">
                        <el-button class="ml-5" type="danger" plain slot="reference">删除 <i
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

        <el-dialog title="编辑会议室信息" :visible.sync="dialogFormVisible" width="35%">
            <el-form label-width="22%" class="frame">
                <el-form-item label="会议室名:">
                    <el-input v-model="edit.roomName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="会议室状态:">
                    <el-input v-model="edit.roomState" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="会议室位置:">
                    <el-input v-model="edit.roomLocation" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="限制人数:">
                    <el-input v-model="edit.roomCapacity" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="固定设备:">
                    <el-input v-model="edit.roomEquipment" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="描述:">
                    <el-input v-model="edit.roomDescription" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="可用部门:">
                    <el-select v-model="value1" placeholder="请重新选择" multiple>
                        <el-option v-for="(item, key) in departmentEditlist" :key="key" :label="item.departmentName"
                            :value="item.departmentId" class="select_item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="固定房间:">
                    <el-select v-model="value2" placeholder="请重新选择" multiple>
                        <el-option v-for="(item, key) in fixedroomEditlist" :key="key" :label="item.fixedRoomName"
                            :value="item.fixedRoomId" class="select_item">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="新增会议室信息" :visible.sync="dialogAddVisible" width="35%">
            <el-form label-width="22%" class="frame">
                <el-form-item label="会议室名:">
                    <el-input v-model="form.roomName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="会议室位置:">
                    <el-input v-model="form.roomLocation" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="限制人数:">
                    <el-input v-model="form.roomCapacity" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="固定设备:">
                    <el-input v-model="form.roomEquipment" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="描述:">
                    <el-input v-model="form.roomDescription" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="可用部门:">
                    <el-select v-model="form.departments" placeholder="请选择" multiple clearable>
                        <el-option v-for="item in departmentAddlist" :key="item.departmentId" :label="item.departmentName"
                            :value="item.departmentId" class="select_item">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="固定房间:">
                    <el-select v-model="form.fixedRoomIds" placeholder="请选择" multiple clearable>
                        <el-option v-for="item in fixedroomAddlist" :key="item.fixedRoomId" :label="item.fixedRoomName"
                            :value="item.fixedRoomId" class="select_item">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogAddVisible = false">取 消</el-button>
                <el-button type="primary" @click="add">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="会议室信息详情" :visible.sync="dialogDetailVisible" width="35%">
            <el-form label-width="22%" class="frame">
                <el-form-item label="会议室名:">
                    <el-input v-model="form.roomName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="会议室状态:">
                    <el-input v-model="form.roomState" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="会议室位置:">
                    <el-input v-model="form.roomLocation" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="限制人数:">
                    <el-input v-model="form.roomCapacity" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="固定设备:">
                    <el-input v-model="form.roomEquipment" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="描述:">
                    <el-input v-model="form.roomDescription" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="可用部门:">
                    <el-input v-model="departmentlist" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="固定房间:">
                    <el-input v-model="fixedroomlist" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "Roommanage",
    data() {
        return {
            tableData: [],
            multipleSelection: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            form: {},
            edit: {},
            dialogFormVisible: false,
            dialogAddVisible: false,
            dialogDetailVisible: false,
            departmentlist: '',
            departmentAddlist: [],
            departmentEditlist: [],
            fixedroomlist: '',
            fixedroomAddlist: [],
            fixedroomEditlist: [],
            roomName: '',
            value1: [],
            value2: [],
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.request.get("/room/all", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                }
            }).then(res => {
                // console.log(res.data.rooms)
                this.tableData = res.data.rooms
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
            this.request.get("/room/getDepartments").then(res => {
                // console.log(res.data)
                this.departmentAddlist = res.data.departments
            })
            this.request.get("/room/getFixedRoom").then(res => {
                this.fixedroomAddlist = res.data.rooms
            })
        },
        del(id) {
            this.request.delete("/room/delete?roomId=" + id).then(res => {
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        delBatch() {
            let ids = this.multipleSelection.map(v => v.roomId)
            this.request.delete("/room/delete?roomId=" + ids).then(res => {
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
        handleDetail(row) {
            this.departmentlist = ""
            this.fixedroomlist = ""
            this.form = JSON.parse(JSON.stringify(row))
            // console.log(this.form)
            for (let i = 0; i < this.form.departments.length; i++) {
                this.departmentlist = this.departmentlist + this.form.departments[i].departmentName + ' '
            }
            for (let i = 0; i < this.form.fixedRooms.length; i++) {
                this.fixedroomlist = this.fixedroomlist + this.form.fixedRooms[i].fixedRoomName + ' '
            }
            if (this.form.roomState == 1) {
                this.form.roomState = "可用"
            } else {
                this.form.roomState = "禁用"
            }

            this.dialogDetailVisible = true
        },
        handleEdit(row) {
            this.value1 = []
            this.value2 = []
            this.edit = JSON.parse(JSON.stringify(row))
            for (let i = 0; i < this.edit.departments.length; i++) {
                this.value1.push(this.edit.departments[i].departmentId)
            }
            for (let i = 0; i < this.edit.fixedRooms.length; i++) {
                this.value2.push(this.edit.fixedRooms[i].fixedRoomId)
            }
            this.dialogFormVisible = true
            this.request.get("/room/getDepartments").then(res => {
                this.departmentEditlist = res.data.departments
            })
            this.request.get("/room/getFixedRoom").then(res => {
                this.fixedroomEditlist = res.data.rooms
            })
        },
        save() {
            this.edit.departments = this.value1
            this.edit.fixedRoomIds = this.value2
            this.request.put("/room/update", this.edit).then(res => {
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.dialogFormVisible = false
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        add() {
            this.request.post("/room/add", this.form).then(res => {
                // console.log(res)
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.dialogAddVisible = false
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        search() {
            this.request.get("/room/all", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    roomName: this.roomName
                }
            }).then(res => {
                this.tableData = res.data.rooms
                this.total = res.data.total - 0
            })
        },
        reset() {
            this.roomName = ""
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