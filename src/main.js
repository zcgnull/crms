import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import request from "@/utils/request";
import store from "./store";
import axios from 'axios';

Vue.config.productionTip = false
Vue.use(ElementUI,{size:"mini"});
Vue.prototype.request = request

axios.defaults.withCredentials=true;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')