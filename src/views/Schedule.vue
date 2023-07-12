<template>
    <div>
        <div style="margin: 20px 0">
            <b style="font-size: 18px; color: #323435;">会 议 室 日 程 安 排</b>
        </div>
        <div style="margin: 20px 0">
            <el-select v-model="roomName" placeholder="请选择会议室">
                <el-option v-for="item in roomlist" :key="item.roomId" :label="item.roomName" :value="item.roomName"
                    class="select_item">
                </el-option>
            </el-select>
            <el-button class="ml-10" plain type="primary" @click="search"><i class="el-icon-search"></i>
                搜索</el-button>
            <el-button type="warning" plain @click="reset"><i class="el-icon-refresh"></i> 重置</el-button>
        </div>

        <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
            @selection-change="handleSelectionChange">
            <el-table-column prop="roomName" label="会议室" align="center"></el-table-column>
            <el-table-column prop="meetingName" label="会议" align="center"></el-table-column>
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
    name: "Schedule",
    data() {
        return {
            tableData: [],
            multipleSelection: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            status: 0,
            roomName: "",
            roomlist: []
        }
    },
    created() {
        this.load()
    },
    methods: {
        load() {
            this.request.get("/meeting/list", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    status: this.status
                }
            }).then(res => {
                this.tableData = res.data.rows
                this.total = res.data.total - 0

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
        search() {
            this.request.get("/meeting/list", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    roomName: this.roomName,
                    status: this.status,
                }
            }).then(res => {
                this.tableData = res.data.rows
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

<style>
.headerBg {
    background: #eee !important;
}

.ml-5 {
    margin-left: 5px;
}

.ml-10 {
    margin-left: 10px;
}

.select_item {
    height: 25px;
    line-height: 25px;
    font-size: 12px;
}
</style>