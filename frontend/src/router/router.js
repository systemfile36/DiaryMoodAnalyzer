//vue-router 설정 파일 

import { createWebHistory, createRouter } from "vue-router";
import SignUp from "../components/SignUp.vue";
import Title from "../components/Title.vue";
import SignIn from "../components/SignIn.vue";
import WriteDiary from "../components/WriteDiary.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/signUp', component: SignUp },
        { path: '/', component: Title },
        { path: '/signin', component: SignIn },
        { path: '/diary', component: WriteDiary }
    ]
})

export default router;