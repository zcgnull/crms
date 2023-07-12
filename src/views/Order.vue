<template>
    <el-card style="width: 80%; margin: 0% auto;">
        <el-form label-width="28%" size="small" class="frame" :model="form" :rules="rules">
            <el-row style="margin-bottom: 18px;">
                <span style="font-size: 18px;font-weight: 700;">预 约 会 议</span>
            </el-row>
            <el-row>
                <el-col :span="11">
                    <el-form-item label="预定人 :">
                        <el-input v-model="user.userName" autocomplete="off" disabled></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="会议名称 :" prop="meetingName">
                        <el-input v-model="form.meetingName" autocomplete="off" placeholder="请填写会议名称"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="11">
                    <el-form-item label="开始时间 :" prop="meetingStarttime">
                        <el-date-picker v-model="form.meetingStarttime" type="datetime" placeholder="选择日期时间" align="right"
                            value-format="yyyy-MM-dd HH:mm:ss" :picker-options="pickerOptionsStart">
                        </el-date-picker>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="结束时间 :" prop="meetingEndtime">
                        <el-date-picker v-model="form.meetingEndtime" type="datetime" placeholder="选择日期时间" align="right"
                            value-format="yyyy-MM-dd HH:mm:ss" :picker-options="pickerOptionsEnd">
                        </el-date-picker>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row style="margin-bottom: 18px;">
                <p>* 请提前30分钟预约会议，填写完开始时间和结束时间后需点击下面的按钮确认！</p>
                <el-button round @click="check">请 先 确 认 好 预 约 时 间 <i class="el-icon-check" size="small"></i></el-button>
            </el-row>
            <el-row>
                <el-col :span="11">
                    <el-form-item label="会议室 :" prop="roomId">
                        <el-select v-model="form.roomId" placeholder="请选择会议室">
                            <el-option v-for="item in roomlist" :key="item.roomId" :label="item.roomName"
                                :value="item.roomId" class="select_item" :disabled="item.disabled">
                                <span style="float: left;">{{ item.roomName }}</span>
                                <span style="float: right; font-size: 10px; color: #8492a6;">{{ item.stateMsg }}</span>
                            </el-option>
                            <div slot="empty"
                                style="color: #c0c4cc; text-align: center; line-height: 35px;font-size: 10px;">
                                该时段不可选择
                            </div>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="参会人员 :" prop="users">
                        <el-select v-model="form.users" placeholder="请选择参会人员" multiple>
                            <el-option v-for="item in userlist" :key="item.userId" :label="item.userName"
                                :value="item.userId" class="select_item">
                                <span style="float: left;">{{ item.userName }}</span>
                                <span style="float: right; font-size: 10px; color: #8492a6;">可约</span>
                            </el-option>
                            <div slot="empty"
                                style="color: #c0c4cc; text-align: center; line-height: 35px;font-size: 10px;">
                                请先确定会议时间
                            </div>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="20">
                    <el-form-item label="可选设备 :">
                        <!-- <el-select v-model="value1" placeholder="请选择可选设备" multiple>
                            <el-option v-for="item in equipmentlist" :key="item.equipmentId" :label="item.equipmentName"
                                :value="item.equipmentId" class="select_item" :disabled="item.disabled">
                                <span style="float: left;">{{ item.equipmentName }}</span>
                                <span style="float: right; font-size: 10px; color: #8492a6;">剩余{{ item.equipmentNum
                                }}个</span>
                            </el-option>
                            <div slot="empty"
                                style="color: #c0c4cc; text-align: center; line-height: 35px;font-size: 10px;">
                                请先确定会议时间
                            </div>
                        </el-select> -->
                        <el-checkbox-group v-model="value1">
                            <el-checkbox v-for="item in equipmentlist" :key="item.equipmentId" :label="item.equipmentId"
                                style="display: block;float: left;" :disabled="item.disabled"
                                @change="changeChecked($event, item.equipmentId)" v-model="item.checked"
                                :checked="item.checked">{{ item.equipmentName }}：剩余{{ item.equipmentNum }}个，选择：
                                <el-input style="width: 60px;margin-left: 5px;" type="number" v-model="item.num"
                                    oninput="if(value<0)value=0" :disabled="!item.checked"></el-input> 个
                            </el-checkbox>
                        </el-checkbox-group>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="20">
                    <el-form-item label="会议描述 :" prop="meetingProfile">
                        <el-input v-model="form.meetingProfile" autocomplete="off" type="textarea"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="11">
                    <el-form-item label="会议特殊需求 :">
                        <el-input v-model="form.meetingDemand" autocomplete="off" type="textarea"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="其他设备需求 :">
                        <el-input v-model="form.meetingEquipment" autocomplete="off" type="textarea"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row style="margin-bottom: 18px;">
                <el-button type="info" plain @click="$router.go(-1)">退 出</el-button>
                <el-button type="success" plain @click="save">确 定</el-button>
            </el-row>
        </el-form>
    </el-card>
</template>
  
<script>
export default {
    name: "Order",
    data() {
        return {
            form: {},
            user: {},
            roomlist: [],
            equipmentlist: [],
            userlist: [],
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
                    // console.log(time.getTime())
                    if (this.form.meetingEndtime) {
                        // return (time.getTime() > Date.now() && time.getTime() < this.form.meetingEndtime)
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
                        //     return (time.getTime() > this.form.meetingStarttime)
                        return (time.getTime() < new Date(this.form.meetingStarttime).getTime() - 8.64e7);
                    }
                    // else {
                    //     return (time.getTime() > Date.now())
                    // }
                }
            },
            rules: {
                meetingStarttime: [
                    { required: true, message: '请输入开始时间', trigger: 'blur' },
                ],
                meetingEndtime: [
                    { required: true, message: '请输入结束时间', trigger: 'blur' },
                ],
                roomId: [
                    { required: true, message: '请选择会议室', trigger: 'blur' },
                ],
                meetingName: [
                    { required: true, message: '请输入会议名称', trigger: 'blur' },
                ],
                meetingProfile: [
                    { required: true, message: '请添加会议描述', trigger: 'blur' },
                ],
                value1: [
                    { required: true, message: '请选择需要的可选设备', trigger: 'blur' },
                ],
                users: [
                    { required: true, message: '请选择需要参加会议的员工', trigger: 'blur' },
                ],
            },
            value1: [],
            numArr: [],
        }
    },
    created() {
        this.request.get("/user/userInfo").then(res => {
            // console.log(res.data)
            if (res.code === 200) {
                this.user = res.data
            }
        })
        this.request.get("/meeting/findAllEquipment").then(res => {
            // console.log(res.data)
            if (res.code === 200) {
                this.equipmentlist = res.data.equipments
                this.equipmentlist.forEach((obj) => {
                    this.$set(obj, 'checked', false)
                    if (obj.equipmentNum === 0) {
                        this.$set(obj, 'disabled', true)
                    } else {
                        this.$set(obj, 'disabled', false)
                        // this.$set(obj, 'num', 0)
                    }
                })
            }
        })
    },
    methods: {
        changeChecked(n, id) {
            let index = this.equipmentlist.findIndex(info => id == info.equipmentId)
            this.equipmentlist[index].checked = n
            if (!this.equipmentlist[index].checked) {
                this.equipmentlist[index].num = 0
            }
        },
        save() {
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
            this.form['equipments'] = map
            if (this.form.users.length !== 0) {
                this.request.post("/meeting/add", this.form).then(res => {
                    if (res.code === 200) {
                        this.$message.success(res.msg)
                        this.$router.go(-1)
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            }
        },
        check() {
            if (this.form.meetingEndtime && (Date.now() + 30 * 60 * 1000 <= new Date(this.form.meetingStarttime).getTime())) {
                if (new Date(this.form.meetingEndtime).getTime() >= (new Date(this.form.meetingStarttime).getTime() + 30 * 60 * 1000)) {
                    this.request.post("/user/UserOfMeeting", {
                        scheduleStarttime: this.form.meetingStarttime,
                        scheduleEndtime: this.form.meetingEndtime
                    }).then(res => {
                        // console.log(res)
                        if (res.code === 550) {
                            this.$message.error(res.msg)
                        }
                        if (res.code === 200) {
                            this.$message.success(res.msg)
                        }
                    })

                    this.request.post("/user/statusUser", {
                        scheduleStarttime: this.form.meetingStarttime,
                        scheduleEndtime: this.form.meetingEndtime
                    }).then(res => {
                        if (res.code === 200) {
                            // console.log(res.data)
                            this.userlist = res.data
                        }
                    })

                    this.request.post("/meeting/findRoom", {
                        meetingStarttime: this.form.meetingStarttime,
                        meetingEndtime: this.form.meetingEndtime
                    }).then(res => {
                        // 清空select
                        // TODO
                        // this.roomlist = []
                        // console.log(res.data)
                        this.roomlist = res.data.roomInfo
                        this.roomlist.forEach((obj) => {
                            if (obj.state === 0) {
                                obj.disabled = false
                            } else {
                                obj.disabled = true
                            }
                        })
                        // for (let i = 0; i < res.data.roomInfo.length; i++) {
                        //     if (res.data.roomInfo[i].state === 0) {
                        //         this.roomlist.push(res.data.roomInfo[i])
                        //     }
                        // }
                    })

                    this.request.post("/meeting/findEquipment", {
                        meetingStarttime: this.form.meetingStarttime,
                        meetingEndtime: this.form.meetingEndtime
                    }).then(res => {
                        this.equipmentlist = res.data.equipments
                        this.equipmentlist.forEach((obj) => {
                            obj.checked = false
                            if (obj.equipmentNum === 0) {
                                obj.disabled = true
                            } else {
                                obj.disabled = false
                                // obj.num = 0
                            }
                        })
                    })
                } else {
                    this.$message.error("您预约会议的时长至少要30min!")
                }
            } else {
                this.$message.error("您需求至少提前30min才能预约会议!")
            }
        }
    }
}
</script>
  
<style lang="scss" scoped>
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

p {
    font-size: 10px;
    color: red;
    margin-top: 0px;
}

// ::v-deep .el-input__inner {
//     // background-color: transparent !important;
//     // border-color: #80ffff;
//     // box-shadow: 1px 1px 5px 1px RGB(128, 255, 255, 0.8) inset;
//     text-align: center;
// }

::v-deep .el-form-item__label {
    font-size: 12px;
    // font-weight: 500;
}

::v-deep .el-checkbox__label {
    display: inline-block;
    padding-left: 10px;
    margin: 5px;
    line-height: 19px;
    font-size: 12px;
}

::v-deep input::-webkit-input-placeholder {
    // color: red;
    font-size: 3px;

}

::v-deep input::-moz-input-placeholder {
    // color: red;
    font-size: 3px;
}

::v-deep input::-ms-input-placeholder {
    // color: red;
    font-size: 3px;
}
</style>