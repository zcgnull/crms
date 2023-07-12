<template>
    <div>
        <div style="margin: 20px 0">
            <b style="font-size: 18px; color: #323435;">我 收 到 的 会 议 邀 请 通 知</b>
        </div>

        <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
            @selection-change="handleSelectionChange">
            <el-table-column prop="meetingName" label="会议" align="center"></el-table-column>
            <el-table-column prop="roomName" label="会议室" align="center"></el-table-column>
            <el-table-column prop="meetingStarttime" label="开始时间" align="center"></el-table-column>
            <el-table-column prop="meetingEndtime" label="结束时间" align="center"></el-table-column>
            <el-table-column prop="userName" label="会议邀请人" align="center" width="100"></el-table-column>
            <el-table-column prop="userReplyAlter" label="我的回复" align="center" width="80"></el-table-column>
            <el-table-column label="操作" width="280" align="center">
                <template slot-scope="scope">
                    <el-popconfirm class="ml-5" confirm-button-text='确定' cancel-button-text='我再想想' icon="el-icon-info"
                        icon-color="green" title="您确定参会吗？" @confirm="accept(scope.row)">
                        <el-button class="ml-5" type="success" plain slot="reference">接受 <i
                                class="el-icon-circle-check"></i></el-button>
                    </el-popconfirm>
                    <el-button class="ml-10" type="info" plain slot="reference" @click="handleUndetermined(scope.row)">待定 <i
                            class="el-icon-remove-outline"></i></el-button>

                    <el-button class="ml-5" type="danger" plain slot="reference" @click="handleRefuse(scope.row)">拒绝 <i
                            class="el-icon-circle-close"></i></el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
        </div>

        <el-dialog title="您确定要待定此会议吗" :visible.sync="dialogUndeterminedVisible" width="35%">
            <el-form label-width="100px">
                <el-form-item label="请填写理由：">
                    <el-input v-model="formm.userInfo" autocomplete="off" type="textarea"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogUndeterminedVisible = false">取 消</el-button>
                <el-button type="primary" @click="undetermined">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="您确定要拒绝此会议吗" :visible.sync="dialogRefuseVisible" width="35%">
            <el-form label-width="100px">
                <el-form-item label="请填写理由：">
                    <el-input v-model="formmm.userInfo" autocomplete="off" type="textarea"></el-input>
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
    name: "Invite",
    data() {
        return {
            tableData: [],
            multipleSelection: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            form: {},
            formm: {},
            formmm: {},
            dialogUndeterminedVisible: false,
            dialogRefuseVisible: false,
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.request.get("/meetingUser/invite", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                }
            }).then(res => {
                this.tableData = res.data.rows
                this.total = res.data.total - 0
                for (let i = 0; i < this.tableData.length; i++) {
                    if (this.tableData[i].userReply === 0) {
                        this.tableData[i].userReplyAlter = "未回复"
                    }
                    else if (this.tableData[i].userReply === 1) {
                        this.tableData[i].userReplyAlter = "已接受"
                    }
                    else if (this.tableData[i].userReply === 2) {
                        this.tableData[i].userReplyAlter = "已拒绝"
                    }
                    else if (this.tableData[i].userReply === 3) {
                        this.tableData[i].userReplyAlter = "已待定"
                    }
                }
            })
        },
        accept(row) {
            this.form.id = row.id
            this.form.meetingId = row.meetingId
            this.form.userReply = 1
            this.request.post("/meetingUser/choice", this.form).then(res => {
                console.log(res)
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.load()
                } else {
                    this.$message.error(res.msg)
                }
                this.load()
            })
        },
        handleUndetermined(row) {
            this.dialogUndeterminedVisible = true
            this.formm.id = row.id
            this.formm.meetingId = row.meetingId
            this.formm.userReply = 3
        },
        undetermined() {
            this.request.post("/meetingUser/choice", this.formm).then(res => {
                // console.log(res)
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.load()
                    this.dialogUndeterminedVisible = false
                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        handleRefuse(row) {
            this.dialogRefuseVisible = true
            this.formmm.id = row.id
            this.formmm.meetingId = row.meetingId
            this.formmm.userReply = 2
        },
        refuse() {
            this.request.post("/meetingUser/choice", this.formmm).then(res => {
                // console.log(res)
                if (res.code === 200) {
                    this.$message.success(res.msg)
                    this.load()
                    this.dialogRefuseVisible = false
                } else {
                    this.$message.error(res.msg)
                }
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