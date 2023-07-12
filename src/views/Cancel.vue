<template>
    <div>
        <div style="margin: 20px 0">
            <b style="font-size: 18px; color: #323435;">我 收 到 的 会 议 取 消 通 知</b>
        </div>

        <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
            @selection-change="handleSelectionChange">
            <el-table-column prop="meetingName" label="会议" align="center"></el-table-column>
            <el-table-column prop="meetingStarttime" label="开始时间" align="center"></el-table-column>
            <el-table-column prop="meetingEndtime" label="结束时间" align="center"></el-table-column>
            <el-table-column prop="userName" label="会议邀请人" align="center"></el-table-column>
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
    name: "Cancel",
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
            this.request.get("/meetingUser/delete", {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                }
            }).then(res => {
                // console.log(res.data)
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
        handleSelectionChange(val) {
            this.multipleSelection = val
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
</style>