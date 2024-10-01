//vue-router 설정 파일 

import { createWebHistory, createRouter } from "vue-router";
import SignUp from "../components/SignUp.vue";
import Title from "../components/Title.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/signUp', component: SignUp },
        { path: '/', component: Title }
    ]
})

export default router;