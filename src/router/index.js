import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/forget',
    name: 'forget',
    component: () => import('../views/Forget.vue')
  },
  {
    path: '/reset',
    name: 'reset',
    component: () => import('../views/Reset.vue')
  },
  {
    path: '/success',
    name: 'success',
    component: () => import('../views/Success.vue')
  },
  {
    path: "/404",
    name: "NotFound",
    component: () => import("../views/404.vue"),
  },
  {
    path: '/',
    name: 'manage',
    component: () => import('../views/Manage.vue'),
    redirect:"/homepage",
    children:[
      {path:'homepage', name:'首页', component: () => import("../views/Homepage.vue")},
      {path:'person', name:'个人信息', component: () => import("../views/Person.vue")},
      {path:'password', name:'修改密码', component: () => import("../views/Password.vue")},
      {path:'status', name:'个人状态', component: () => import("../views/Status.vue")},
      {path:'order', name:'预定会议', component: () => import("../views/Order.vue")},
      {path:'myorder', name:'我的预约', component: () => import("../views/Myorder.vue")},
      {path:'myattend', name:'我的参加', component: () => import("../views/Myattend.vue")},
      {path:'history', name:'历史日程', component: () => import("../views/History.vue")},
      {path:'schedule', name:'会议室日程', component: () => import("../views/Schedule.vue")},
      {path:'invite', name:'会议邀请', component: () => import("../views/Invite.vue")},
      {path:'cancel', name:'会议取消', component: () => import("../views/Cancel.vue")},
      {path:'alter', name:'会议修改', component: () => import("../views/Alter.vue")},
      {path:'roommanage', name:'会议室管理', component: () => import("../views/Roommanage.vue")},
      {path:'usermanage', name:'用户管理', component: () => import("../views/Usermanage.vue")},
      {path:'rolemanage', name:'角色管理', component: () => import("../views/Rolemanage.vue")},
      {path:'departmentmanage', name:'部门管理', component: () => import("../views/Departmentmanage.vue")},
      {path:'equipmentmanage', name:'设备管理', component: () => import("../views/Equipmentmanage.vue")},
      {path:'special', name:'特殊需求', component: () => import("../views/Special.vue")},
      {path:'equipment', name:'可选设备', component: () => import("../views/Equipment.vue")},
    ]
  },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  // 无需登录路径
  var nologinList = ['/login','/register','/forget','/reset','/success','/404'];
  
  if(to.path && (nologinList.includes(to.path) || localStorage.getItem("Token"))){
    next()  // 放行路由
  }else{
    // 跳回登陆页面
    next("/login")
  }
  
})

export default router
