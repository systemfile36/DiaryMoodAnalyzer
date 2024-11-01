//vue-router 설정 파일 

import { createWebHistory, createRouter } from "vue-router";
import { useAuthManagerStore } from "../stores/AuthManager";

import SignUp from "../components/SignUp.vue";
import Title from "../components/Title.vue";
import SignIn from "../components/SignIn.vue";
import WriteDiary from "../components/WriteDiary.vue";
import WrittenDiary from "../components/WrittenDiary.vue";
import TempDiaryList from "../components/TempDiaryList.vue";
import TempDiaryDetail from "../components/TempDiaryDetail.vue";

//router 인스턴스 생성 
const router = createRouter({
    history: createWebHistory(),
    //인증이 필요한 페이지에 meta 속성 추가, 인증 필터에서 참조
    routes: [
        { path: '/signUp', component: SignUp },
        { path: '/signin', component: SignIn },
        { path: '/', component: Title, meta: { requireAuth: true } },
        { path: '/diary', component: WriteDiary, meta: { requireAuth: true } },
        { path: '/writtenDiary', component: WrittenDiary, meta: { requireAuth: true }},
        { path: '/updateDiary/:id', name: 'UpdateDiary', component: () => import('../components/UpdateDiary.vue') },
        
        { path: '/test', component: TempDiaryList, meta: { requireAuth: true} },
        //다이어리 상세보기
        { path: '/diaries/:id', component: TempDiaryDetail, meta: {requireAuth: true}}
    ]
})

/* 
인증이 필요한 곳에 접속 시, 인증 여부를 확인한다. 일종의 인증 필터
만약 인증된 상태가 아니라면 로그인 페이지로 리다이렉트 한다. 
*/
router.beforeEach(async (to, from) => {
    //목적지가 인증이 필요한 곳이라면 
    if(to.meta.requireAuth) {

        //인증 필터에서 사용할 인증 매니저
        const authManager = useAuthManagerStore();

        //인증 정보 초기화 
        await authManager.initStates();

        //인증된 상태가 아니라면 로그인 창으로 리다이렉트
        if(!authManager.isAuthenticated) {
            return '/signin';
        } else {
            return true;
        }
    }
})

export default router;