<template>
    <el-row type="flex" justify="space-between">
        <el-col :span="3">
            <div class="room-content">
                <div class="room-title">会议室</div>
                <div class="room-list" :class="{ 'active-list': index === activeNum }"
                    v-for="(item, index) in meetingRoomList" :key="index" ref="todo"
                    @click="chooseRoom(index, item.roomId)">
                    <span>{{ item.roomName }}</span>
                </div>
            </div>
        </el-col>
        <el-col :span="20">
            <div class="calendar-box">
                <full-calendar ref="fullCalendar" style="height: 90%" :options="calendarOptions">
                </full-calendar>
            </div>
        </el-col>
    </el-row>
</template>
  
<script>
import _ from 'lodash' //导入loadsh插件
// 引入日历组件
import FullCalendar from "@fullcalendar/vue";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";

export default {
    name: 'Home',
    components: {
        FullCalendar,
    },
    data() {
        return {
            activeNum: 0,
            dialogVisible: false,
            meetingRoomList: [],
            subList: [],
            scheduleList: [],
            calendarOptions: {
                //   timeGridPlugin  可显示每日时间段
                height: 700,
                plugins: [dayGridPlugin, interactionPlugin],
                headerToolbar: {
                    left: "prev,next today",
                    center: "title",
                    right: "dayGridMonth,dayGridWeek,dayGrid",
                    // right: 'agendaWeek,dayGridWeek,dayGrid'
                },
                buttonText: {
                    // 设置按钮
                    today: "今天",
                    month: "月",
                    week: "周",
                    dayGrid: "天",
                },
                // allDaySlot: false,
                editable: true,
                selectable: true,
                navLinks: true,
                // displayEventEnd: true,//所有视图显示结束时间
                initialView: "dayGridMonth", // 设置默认显示月，可选周、日
                dateClick: this.handleDateClick,
                // eventClick: this.handleEventClick,
                eventsSet: this.handleEvents,
                select: this.handleDateSelect,
                // timezone
                // 设置日程
                events: [],
                eventColor: "#f08f00", // 修改日程背景色
                locale: "zh-cn", // 设置语言
                weekNumberCalculation: "ISO", // 周数
                customButtons: {
                    prev: { // this overrides the prev button
                        text: "PREV",
                        click: () => {
                            this.prev();
                        }
                    },
                    next: { // this overrides the next button
                        text: "PREV",
                        click: () => {
                            this.next();
                        }
                    },
                    today: {
                        text: "今天",
                        click: () => {
                            this.today();
                        }
                    }
                }
            },
        }
    },
    created() {
        this.load()
    },
    mounted() {
        // 获取用户信息
        this.calendarApi = this.$refs.fullCalendar.getApi();
    },
    methods: {
        load() {
            this.request.get("/room/getRoomIds").then(res => {
                // console.log("111")
                this.meetingRoomList = res.data.rooms
                this.$nextTick(() => {
                    this.getRoomList(this.meetingRoomList[0].roomId)
                })
            })
        },
        async getRoomList(roomId) {
            await this.request.get("/meeting/Roomlist?roomId=" + roomId).then(res => {
                this.scheduleList = res.data
                this.scheduleList.forEach((obj) => {
                    if (obj.meetingState === 0) {
                        obj.color = "#6bb377"
                    } else {
                        obj.color = "#999"
                    }
                })
            })
            this.getReservationList(this.scheduleList);
        },
        getReservationList(arrayData) {
            let newArr = [];
            this.subList = arrayData;
            arrayData.forEach((item) => {
                newArr.push({
                    start: this.dealWithTime(item.meetingStarttime),
                    end: this.addDate(this.dealWithTime(item.meetingEndtime), 1),
                    color: item.color,
                    id: item.meetingId,
                    title: `${this.getTitle(item.meetingStarttime, item.meetingEndtime)} ${item.meetingName}`,
                })
            });
            this.calendarOptions.events = newArr;
        },
        // 切换会议室
        chooseRoom(index, roomId) {
            this.activeNum = index;
            this.getRoomList(roomId)
        },
        // 日期加1天
        addDate(date, days) {
            var d = new Date(date);
            d.setDate(d.getDate() + days);
            var mon = (d.getMonth() + 1) < 10 ? "0" + (d.getMonth() + 1) : d.getMonth() + 1;
            let endD = d.getDate() < 10 ? '0' + d.getDate() : d.getDate();
            return `${d.getFullYear()}-${mon}-${endD}`;
        },
        // 获取会议事件title
        getTitle(date1, date2) {
            let start = date1.substring(11, 16);
            let end = date2.substring(11, 16);
            return `${start}~${end}`;
        },
        // 获取时分时间
        getHoursMin(value) {
            return value.substring(11, 16);
        },
        // 处理会议时间格式
        dealWithTime(date) {
            let newDate = /\d{4}-\d{1,2}-\d{1,2}/g.exec(date)[0];
            return newDate;
        },
        // 点击日历
        handleDateClick: function (arg) {
            this.$forceUpdate();
            // console.log(arg, '事件1')
            this.dialogVisible = true;
        },
        handleEvents(events) {
            // console.log(events, '事件3');
        },
        handleDateSelect(selectInfo) {
            // console.log(selectInfo, '事件4');
        },
        // 切换上一个按钮
        prev() {
            this.calendarApi.prev();
        },
        // 切换下一个按钮事件
        next() {
            this.calendarApi.next();
        },
        // 点击今天按钮
        today() {
            this.calendarApi.today();
        },
    }
}
</script>
<style lang="scss" scoped>
@import "src/assets/css/home";
</style>
  