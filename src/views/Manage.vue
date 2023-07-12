<template>
  <el-container style="min-height: 100vh;">
    <el-header style="background-color: #323435;">
      <Header :user="user" />
    </el-header>
    <el-container>
      <el-aside :width="sideWidth + 'px'">
        <Aside />
      </el-aside>
      <el-container>
        <el-main>
          <router-view @refreshUser="getUser" />
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

<script>

import Aside from '@/components/Aside.vue'
import Header from '@/components/Header.vue'
import { socket } from '@/utils/websocket'
export default {
  name: 'Manage',
  data() {
    return {
      sideWidth: 140,
      user: {},
      loading: true,
      websocketCount: -1,
      //查询条件
      queryCondition: {
        type: 2,
      },
    }
  },
  created() {
    this.getUser()
    let Token = localStorage.getItem("Token") ? JSON.parse(localStorage.getItem("Token")) : null
    // 初始化websocket对象
    // window.location.host获取ip和端口，
    // process.env.VUE_APP_WEBSOCKET_BASE_API获取请求前缀
    socket.initWebSocket(
      `ws://82.157.191.133:9090/ws/${Token.token}`
    );
    // 绑定接收消息方法
    socket.websocket.onmessage = this.websocketOnMessage;
  },
  methods: {
    getUser() {
      this.request.get("/user/userInfo").then(res => {
        if (res.code === 200) {
          this.user = res.data
        }
        if (res.code === 401) {
          this.$router.push('/login')
        }
      })
    },
    init() {
      this.queryCondition.type = 2;
      socket.sendMsg(JSON.stringify(this.queryCondition));
    },
    websocketOnMessage(event) {
      // 初始化界面时，主动向后台发送一次消息，获取数据
      this.websocketCount += 1;
      if (this.websocketCount === 0) {
        this.init();
      }
      let info = JSON.parse(event.data);
      console.log(info);
      switch (info.type) {
        case 1:
          socket.websocketState = true;
          break;
        case 2:
          this.loading = true;
          this.$nextTick(() => {
            this.consumeMessage(info);
          })
          break;
        case 3:
          this.loading = false;
          break;
      }
    },
    consumeMessage(info) {
      this.$notify({
        title: info.title,
        message: info.msg,
        duration: 0,
        type: 'info'
      });
    }
  },
  components: { Aside, Header },
}
</script>
<style></style>
