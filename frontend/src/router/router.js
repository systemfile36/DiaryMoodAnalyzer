//vue-router 설정 파일 

import { createWebHistory, createRouter } from "vue-router";
import { useAuthManagerStore } from "../stores/AuthManager";

import SignUp from "../components/SignUp.vue";

import SignIn from "../components/SignIn.vue";
import WriteDiary from "../components/WriteDiary.vue";
import TempDiaryList from "../components/TempDiaryList.vue";
import TempDiaryDetail from "../components/TempDiaryDetail.vue";
import Authority from "../utils/Authority";
import TempLogin from "../components/TempLogin.vue";
import ExpertPage from "../components/ExpertPage.vue";
import TempNotifications from "../components/TempNotifications.vue";

import CounselWritePage from "../components/CounselWritePage.vue";
import CounselEditPage from "../components/CounselEditPage.vue";
import Chart from "../components/Chart.vue";

import TempTitle from "../components/TempTitle.vue";
import UserGuide from "../components/UserGuide.vue";
import FrontPage from "../components/FrontPage.vue";
import Title from "../components/Title.vue";


//router 인스턴스 생성 
const router = createRouter({
    history: createWebHistory(),
    //인증이 필요한 페이지에 meta 속성 추가, 인증 필터에서 참조
    routes: [
        { path: '/signUp', component: SignUp },
        { path: '/signin', component: SignIn },
        { path: '/', component: Title, meta: { requireAuth: true } },
        { path: '/diary', name: 'WriteDiary', component: WriteDiary, meta: { requireAuth: true } },
        
        { path: '/chart', component: Chart, meta: { requireAuth: true} },
        { path: '/expertPage', component: ExpertPage, meta: { requireAuth: true} },
        { path: '/counselWritePage', name: 'CounselWritePage',component: CounselWritePage, meta: { requireAuth: true} },
        { path: '/counselEditPage', name: 'CounselEditPage', component: CounselEditPage, meta: { requireAuth: true} },

        { path: '/diaries', component: TempDiaryList, meta: { requireAuth: true} },
        //다이어리 상세보기
        { path: '/diaries/:id', component: TempDiaryDetail, meta: {requireAuth: true}},

        //전문가용 담당 사용자의 다이어리 목록 조회
        { path: '/expert/diaries/:email', component: TempDiaryList, meta: { requireAuth: true, authority: Authority.EXPERT } },

        //알림 상세보기 페이지 
        { path: '/notifications', component: TempNotifications, meta: { requireAuth: true }},

        { path: '/tempTitle', component: TempTitle, meta: { requireAuth: true} },
        { path: '/userGuide', name: 'UserGuide', component: UserGuide, meta: { requireAuth: true} },
        { path: '/tempLogin', component: TempLogin, meta: { requireAuth: true} },

        { path: '/test', component: FrontPage }
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
        //만약 권한이 필요한 곳이라면, 권한을 체크한다. 
        } else if(to.meta.authority) {

            return to.meta.authority === authManager.role;
        } else {
            return true;
        }
    }
})

export default router;