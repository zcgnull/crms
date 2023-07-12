<template>
    <div>
        <div style="margin: 20px 0">
            <b style="font-size: 18px; color: #323435;">可 选 设 备 消 息 提 醒</b>
        </div>

        <div style="margin: 10px 0">
            <el-input style="width: 200px;margin-right: 5px;" placeholder="请输入会议" prefix-icon="el-icon-search"
                v-model="searchName"></el-input>
            <el-select v-model="searchRoomName" placeholder="请选择会议室" style="margin-right: 5px;width: 200px;">
                <el-option v-for="item in roomlist" :key="item.roomId" :label="item.roomName" :value="item.roomName"
                    class="select_item">
                </el-option>
            </el-select>
            <el-date-picker v-model="searchMeetingStarttime" type="datetime" placeholder="请选择开始时间" align="right"
                value-format="yyyy-MM-dd HH:mm:ss" style="margin-right: 5px;width: 200px;">
            </el-date-picker>
            <el-date-picker v-model="searchMeetingEndtime" type="datetime" placeholder="请选择结束时间" align="right"
                value-format="yyyy-MM-dd HH:mm:ss" style="margin-right: 5px;width: 200px;">
            </el-date-picker>
            <el-button class="ml-5" type="primary" @click="load" plain><i class="el-icon-search"></i>
                搜索</el-button>
            <el-button type="warning" plain @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
        </div>

        <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
            @selection-change="handleSelectionChange">
            <el-table-column prop="meetingName" label="会议" align="center"></el-table-column>
            <el-table-column prop="roomName" label="会议室" align="center"></el-table-column>
            <el-table-column prop="meetingStarttime" label="开始时间" align="center"></el-table-column>
            <el-table-column prop="meetingEndtime" label="结束时间" align="center"></el-table-column>
            <el-table-column prop="status" label="确认状态" align="center"></el-table-column>
            <el-table-column label="操作" align="center" width="280">
                <template slot-scope="scope">
                    <el-button class="ml-5" type="info" plain @click="handleDetail(scope.row)">详情 <i
                            class="el-icon-thumb"></i></el-button>
                    <el-popconfirm class="ml-5" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                        icon-color="green" title="您已确认好可选设备了吗？" @confirm="yes(scope.row.id)">
                        <el-button class="ml-5" type="success" plain slot="reference">确认 <i
                                class="el-icon-check"></i></el-button>
                    </el-popconfirm>
                    <el-button class="ml-10" type="danger" plain slot="reference" @click="handleRefuse(scope.row)">拒绝 <i
                            class="el-icon-close"></i></el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
        </div>

        <el-dialog title="可选设备详情" :visible.sync="dialogDetailVisible" width="35%">
            <el-form label-width="22%">
                <el-form-item label="会议:">
                    <el-input v-model="form.meetingName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="会议室:">
                    <el-input v-model="form.roomName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="开始时间:">
                    <el-input v-model="form.meetingStarttime" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="结束时间:">
                    <el-input v-model="form.meetingEndtime" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="确认状态:">
                    <el-input v-model="form.status" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="可选设备:">
                    <el-input v-model="form.equipments" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="其他需求:">
                    <el-input v-model="form.otherEquipment" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-dialog title="您确定要拒绝此可选设备需求吗" :visible.sync="dialogRefuseVisible" width="35%">
            <el-form label-width="100px">
                <el-form-item label="请填写理由：">
                    <el-input v-model="refuseForm.reason" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogRefuseVisible = false">取 消</el-button>
                <el-button type="primary" @click="refuse">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
export default {
    name: "Equipment",
    data() {
        return {
            tableData: [],
            multipleSelection: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            dialogDetailVisible: false,
            dialogRefuseVisible: false,
            form: {},
            searchName: '',
            searchRoomName: '',
            searchMeetingStarttime: '',
            searchMeetingEndtime: '',
            roomlist: [],
            refuseForm: {}
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.request.get("msg/allEquipments", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    meetingName: this.searchName,
                    roomName: this.searchRoomName,
                    meetingStarttime: this.searchMeetingStarttime,
                    meetingEndtime: this.searchMeetingEndtime
                }
            }).then(res => {
                this.tableData = res.data.reminds
                this.total = res.data.total - 0
                // console.log(this.tableData)
                this.tableData.forEach((obj) => {
                    if (obj.state === 1) {
                        obj.status = '已确认'
                    } else if (obj.state === 0) {
                        obj.status = '未确认'
                    } else if (obj.state === 2) {
                        obj.status = '已拒绝'
                    }
                })
            })
            this.request.get("/room/getRoomIds").then(res => {
                this.roomlist = res.data.rooms
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
        handleSelectionChange(val) {
            this.multipleSelection = val
        },
        handleDetail(row) {
            this.form = row
            this.request.get("/msg/equipmentRemind?meetingId=" + this.form.meetingId).then(res => {
                this.$set(this.form, 'otherEquipment', res.data.otherEquipment)
                let equ = res.data.equipments.map(item => item.equipmentName).join(',')
                var reg = /,$/gi;
                equ = equ.replace(reg, "");
                this.$set(this.form, 'equipments', equ)
            })
            this.dialogDetailVisible = true
        },
        yes(id) {
            this.request.get("/msg/equipmentSure?id=" + id).then(res => {
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        reset() {
            this.searchName = ""
            this.searchMeetingStarttime = ""
            this.searchMeetingEndtime = ""
            this.searchRoomName = ""
            this.load()
        },
        handleRefuse(row) {
            this.dialogRefuseVisible = true
            this.refuseForm.id = row.id
        },
        refuse() {
            this.request.get(`/msg/equipmentDeny?id=${this.refuseForm.id}&reason=${this.refuseForm.reason}`)
                .then(res => {
                    if (res.code === 200) {
                        this.$message.success(res.msg)
                        this.load()
                        this.dialogRefuseVisible = false
                    } else {
                        this.$message.error(res.msg)
                    }
                })
        }
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