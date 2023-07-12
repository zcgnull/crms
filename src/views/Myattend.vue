<template>
    <div>
        <div style="margin: 20px 0">
            <b style="font-size: 18px; color: #323435;">我 的 待 参 加 会 议</b>
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
            <el-table-column prop="userName" label="会议预定人" align="center"></el-table-column>
            <el-table-column prop="meetingStarttime" label="开始时间" align="center"></el-table-column>
            <el-table-column prop="meetingEndtime" label="结束时间" align="center"></el-table-column>
        </el-table>

        <div style="padding: 10px 0">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageNum"
                :page-sizes="[2, 5, 10, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
export default {
    name: "Myattend",
    data() {
        return {
            tableData: [],
            multipleSelection: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            searchName: '',
            searchMeetingStarttime: '',
            searchMeetingEndtime: '',
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.request.get("/meeting/myAttend", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    meetingName: this.searchName,
                    meetingStarttime: this.searchMeetingStarttime,
                    meetingEndtime: this.searchMeetingEndtime
                }
            }).then(res => {
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