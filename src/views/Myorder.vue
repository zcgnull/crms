<template>
    <div>
        <div style="margin: 20px 0">
            <b style="font-size: 18px; color: #323435;">我 预 约 的 会 议</b>
        </div>

        <div style="margin: 10px 0">
            <el-input style="width: 200px;margin-right: 5px;" placeholder="请输入会议名" prefix-icon="el-icon-search"
                v-model="searchName"></el-input>
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
            <el-table-column prop="roomLocation" label="会议室位置" align="center"></el-table-column>
            <el-table-column label="操作" width="350" align="center">
                <template slot-scope="scope">
                    <el-button class="ml-5" type="info" plain @click="handleDetail(scope.row.meetingId)">详情 <i
                            class="el-icon-thumb"></i></el-button>
                    <el-button class="ml-5" type="warning" plain @click="handleRemind(scope.row.meetingId)">通知 <i
                            class="el-icon-warning-outline"></i></el-button>
                    <el-button class="ml-5" type="primary" plain @click="handleEdit(scope.row.meetingId)">修改 <i
                            class="el-icon-edit"></i></el-button>
                    <el-button class="ml-5" type="danger" plain slot="reference" @click="handleDel(scope.row.meetingId)">删除
                        <i class="el-icon-remove-outline"></i></el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
        </div>

        <el-dialog title="会议信息详情" :visible.sync="dialogDetailVisible" width="38%">
            <el-form label-width="20%" class="frame">
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
                <el-form-item label="可选设备:">
                    <el-input v-model="equDetail" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="参会人员:">
                    <el-input v-model="userDetail" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="会议描述:">
                    <el-input v-model="form.meetingProfile" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="特殊需求:">
                    <el-input v-model="form.meetingDemand" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="其他设备需求:">
                    <el-input v-model="form.meetingEquipment" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-dialog title="管理员是否确认预定人需求?" :visible.sync="dialogRemindVisible" width="35%">
            <el-form label-width="125px">
                <el-form-item label="可选设备：">
                    <el-input v-model="remind.equipmentRemind" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="可选设备拒绝原因：" v-show="remind.equipmentRemindState === 2">
                    <el-input v-model="remind.equipmentRemindReason" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="特殊需求：">
                    <el-input v-model="remind.demandRemind" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="特殊需求拒绝原因：" v-show="remind.demandRemindState === 2">
                    <el-input v-model="remind.demandRemindReason" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-dialog title="修改已预约的会议" :visible.sync="dialogEditVisible" width="45%" @close="cancel">
            <el-form label-width="18%" class="frame">
                <el-form-item label="会议:">
                    <el-input v-model="form.meetingName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="开始时间 :" prop="meetingStarttime">
                    <el-date-picker v-model="form.meetingStarttime" type="datetime" placeholder="选择日期时间" align="right"
                        value-format="yyyy-MM-dd HH:mm:ss" :picker-options="pickerOptionsStart">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="结束时间 :" prop="meetingEndtime">
                    <el-date-picker v-model="form.meetingEndtime" type="datetime" placeholder="选择日期时间" align="right"
                        value-format="yyyy-MM-dd HH:mm:ss" :picker-options="pickerOptionsEnd">
                    </el-date-picker>
                </el-form-item>
                <el-row style="margin-bottom: 18px;">
                    <p>* 如需更改时间，请改好后点击下面的按钮！</p>
                    <el-button round @click="check">请 再 次 确 认 预 约 时 间 <i class="el-icon-check"></i></el-button>
                </el-row>

                <el-form-item label="会议室:">
                    <el-select v-model="form.roomName" placeholder="请再次选择会议室">
                        <el-option v-for="item in roomEditlist" :key="item.roomId" :label="item.roomName"
                            :value="item.roomName" class="select_item" :disabled="item.disabled">
                            <span style="float: left;">{{ item.roomName }}</span>
                            <span style="float: right; font-size: 10px; color: #8492a6;">{{ item.stateMsg }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="可选设备:">
                    <el-select v-model="equEditValue" placeholder="请再次选择" multiple v-show="showed">
                        <el-option v-for="(item, key) in equEditlist" :key="key" :label="item.equipmentName"
                            :value="item.equipmentId" class="select_item">
                        </el-option>
                    </el-select>
                    <el-checkbox-group v-model="value1" v-show="!showed">
                        <el-checkbox v-for="item in equipmentlist" :key="item.equipmentId" :label="item.equipmentId"
                            style="display: block;float: left;" :disabled="item.disabled"
                            @change="changeChecked($event, item.equipmentId)" v-model="item.checked"
                            :checked="item.checked">{{ item.equipmentName }}：剩余{{ item.equipmentNum }}个，选择：
                            <el-input style="width: 60px;margin-left: 5px;" type="number" v-model="item.num"
                                oninput="if(value<0)value=0" :disabled="!item.checked"></el-input> 个
                        </el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
                <el-form-item label="参会人员:">
                    <el-select v-model="userEditValue" placeholder="请再次选择" multiple>
                        <el-option v-for="(item, key) in userEditlist" :key="key" :label="item.userName"
                            :value="item.userId" class="select_item">
                            <span style="float: left;">{{ item.userName }}</span>
                            <span style="float: right; font-size: 10px; color: #8492a6;">可约</span>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="会议描述:">
                    <el-input v-model="form.meetingProfile" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="特殊需求:">
                    <el-input v-model="form.meetingDemand" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="其他设备需求:">
                    <el-input v-model="form.meetingEquipment" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="cancel">取 消</el-button>
                <el-button type="primary" @click="edit">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="您确定要删除此会议吗" :visible.sync="dialogDelVisible" width="35%">
            <el-form label-width="100px">
                <el-form-item label="请填写理由：">
                    <el-input v-model="delForm.reason" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogDelVisible = false">取 消</el-button>
                <el-button type="primary" @click="del">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
export default {
    name: "Myorder",
    data() {
        return {
            tableData: [],
            multipleSelection: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            dialogDetailVisible: false,
            dialogRemindVisible: false,
            dialogEditVisible: false,
            dialogDelVisible: false,
            form: {
                meetingId: 0,
                roomId: 0,
                roomName: '',
                userId: 0,
                meetingName: '',
                meetingStarttime: '',
                meetingEndtime: '',
                meetingDemand: '',
                meetingProfile: '',
                meetingEquipment: '',
                equipments: {},
                users: []
            },
            delForm: {
                meetingId: 0,
                reason: ''
            },
            remind: {},
            userValue: [],
            userlist: [],
            userEditValue: [],
            userEditlist: [],
            equValue: [],
            equlist: [],
            equEditValue: [],
            equEditlist: [],
            newForm: {},
            searchName: '',
            searchMeetingStarttime: '',
            searchMeetingEndtime: '',
            roomEditlist: [],
            equDetail: '',
            userDetail: '',
            showed: true,
            value1: [],
            numArr: [],
            equipmentlist: [],
            pickerOptionsStart: {
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
                }],
                disabledDate: (time) => {
                    if (this.form.meetingEndtime) {
                        return (time.getTime() > new Date(this.form.meetingEndtime).getTime())
                    } else {
                        return (time.getTime() < Date.now() - 8.64e7)
                    }
                }
            },
            pickerOptionsEnd: {
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
                }],
                disabledDate: (time) => {
                    if (this.form.meetingStarttime) {
                        return (time.getTime() < new Date(this.form.meetingStarttime).getTime() - 8.64e7);
                    }
                }
            },
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.request.get("/meeting/myMeetings", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    meetingName: this.searchName,
                    meetingStarttime: this.searchMeetingStarttime,
                    meetingEndtime: this.searchMeetingEndtime
                }
            }).then(res => {
                // console.log(searchMeetingStarttime)
                // console.log(res.data.meetings)
                this.tableData = res.data.meetings
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
        handleSelectionChange(val) {
            this.multipleSelection = val
        },
        handleDetail(id) {
            this.dialogDetailVisible = true
            this.request.get("/meeting/info?meetingId=" + id).then(res => {
                // this.userValue = []
                // this.equValue = []
                this.form = res.data.meeting
                this.equDetail = ''
                this.userDetail = ''
                this.form.meetingEquipments.forEach(obj => {
                    this.equDetail = this.equDetail + obj.equipmentName + ',数量为：' + obj.equipmentNum + ' '
                })
                this.form.meetingUsers.forEach(obj => {
                    this.userDetail = this.userDetail + obj.userName + ' '
                })
            })
        },
        handleRemind(id) {
            this.dialogRemindVisible = true
            this.request.get(`/msg/remind?meetingId=${id}`).then(res => {
                this.remind = res.data.remind
                console.log(this.remind)
            })
        },
        handleEdit(id) {
            this.dialogEditVisible = true
            this.request.get("/meeting/info?meetingId=" + id).then(res => {
                this.userEditValue = []
                this.equEditValue = []
                this.numArr = []
                this.form = res.data.meeting
                console.log(this.form)
                // this.roomEditValue = res.data.meeting.roomId
                this.userEditlist = res.data.meeting.meetingUsers
                this.equEditlist = res.data.meeting.meetingEquipments
                for (let i = 0; i < this.form.meetingUsers.length; i++) {
                    this.userEditValue.push(this.form.meetingUsers[i].userId)
                }
                for (let i = 0; i < this.form.meetingEquipments.length; i++) {
                    this.equEditValue.push(this.form.meetingEquipments[i].equipmentId)
                    this.numArr.push(this.form.meetingEquipments[i].equipmentNum)
                }
            })
        },
        changeChecked(n, id) {
            let index = this.equipmentlist.findIndex(info => id == info.equipmentId)
            this.equipmentlist[index].checked = n
            if (!this.equipmentlist[index].checked) {
                this.equipmentlist[index].num = 0
            }
        },
        check() {
            this.showed = false
            if (this.form.meetingEndtime && (Date.now() + 30 * 60 * 1000 <= new Date(this.form.meetingStarttime).getTime())) {
                if (new Date(this.form.meetingEndtime).getTime() >= (new Date(this.form.meetingStarttime).getTime() + 30 * 60 * 1000)) {
                    this.userEditValue = []
                    this.equEditValue = []
                    this.form.roomName = ''
                    this.request.post("/user/UserOfMeeting", {
                        scheduleStarttime: this.form.meetingStarttime,
                        scheduleEndtime: this.form.meetingEndtime,
                    }).then(res => {
                        if (res.code === 550) {
                            this.$message.error(res.msg)
                        }
                        if (res.code === 200) {
                            this.$message.success(res.msg)
                        }
                    })
                    this.request.post("/meeting/findRoom", {
                        meetingStarttime: this.form.meetingStarttime,
                        meetingEndtime: this.form.meetingEndtime,
                        meetingId: this.form.meetingId
                    }).then(res => {
                        this.roomEditlist = res.data.roomInfo
                        this.roomEditlist.forEach((obj) => {
                            if (obj.state === 0) {
                                obj.disabled = false
                            } else {
                                obj.disabled = true
                            }
                        })
                    })

                    this.request.post("/meeting/findEquipment", {
                        meetingStarttime: this.form.meetingStarttime,
                        meetingEndtime: this.form.meetingEndtime,
                        meetingId: this.form.meetingId
                    }).then(res => {
                        if (res.code === 200) {
                            this.equipmentlist = res.data.equipments
                            this.equipmentlist.forEach((obj) => {
                                // this.$set(obj, 'checked', false)
                                obj.checked = false
                                if (obj.equipmentNum === 0) {
                                    // this.$set(obj, 'disabled', true)
                                    obj.disabled = true
                                } else {
                                    // this.$set(obj, 'disabled', false)
                                    obj.disabled = false
                                }
                            })
                        }
                    })

                    this.request.post("/user/statusUser", {
                        scheduleStarttime: this.form.meetingStarttime,
                        scheduleEndtime: this.form.meetingEndtime
                    }).then(res => {
                        // console.log(res)
                        if (res.code === 200) {
                            this.userEditlist = res.data
                        }
                    })
                } else {
                    this.$message.error("您预约会议的时长至少要30min!")
                }
            } else {
                this.$message.error("您需求至少提前30min才能预约会议!")
            }
        },
        edit() {
            if (this.showed === false) {
                this.numArr = []
                this.equipmentlist.forEach((obj) => {
                    if (obj.num) {
                        this.numArr.push(Number(obj.num))
                    }
                })
                var map = {}
                for (let a = 0; a < this.value1.length; a++) {
                    map[this.value1[a]] = this.numArr[a]
                }
            } else {
                var map = {}
                for (let a = 0; a < this.equEditValue.length; a++) {
                    map[this.equEditValue[a]] = this.numArr[a]
                }
            }
            this.newForm['equipments'] = map
            this.newForm.users = this.userEditValue
            this.newForm.meetingId = this.form.meetingId
            this.newForm.roomName = this.form.roomName
            this.newForm.userId = this.form.userId
            this.newForm.meetingName = this.form.meetingName
            this.newForm.meetingStarttime = this.form.meetingStarttime
            this.newForm.meetingEndtime = this.form.meetingEndtime
            this.newForm.meetingDemand = this.form.meetingDemand
            this.newForm.meetingProfile = this.form.meetingProfile
            this.newForm.meetingEquipment = this.form.meetingEquipment
            this.request.put("/meeting/update", this.newForm).then(res => {
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.dialogFormVisible = false
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
            })
            this.dialogEditVisible = false
            this.showed = true
        },
        cancel() {
            this.dialogEditVisible = false
            this.showed = true
        },
        handleDel(id) {
            this.dialogDelVisible = true
            this.delForm.meetingId = id
        },
        del() {
            this.request.delete("/meeting/delete?meetingId=" + this.delForm.meetingId + "&reason=" + this.delForm.reason).then(res => {
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.load()
                    this.dialogDelVisible = false
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        reset() {
            this.searchName = ""
            this.searchMeetingStarttime = ""
            this.searchMeetingEndtime = ""
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

.select_item {
    height: 25px;
    line-height: 25px;
    font-size: 12px;
}

.el-row p {
    font-size: 10px;
    color: red;
    margin-top: 0px;
}

p {
    font-size: 10px;
    margin-top: 0px;
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

.el-checkbox__label {
    display: inline-block;
    padding-left: 10px;
    margin: 5px;
    line-height: 19px;
    font-size: 12px;
}
</style>