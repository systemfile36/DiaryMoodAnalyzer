import { defineStore } from "pinia";
import { ref } from "vue";

/**
 * 사이드 바의 상태와 접고 펼치는 것을 관리하는 Pinia 저장소
 */
export const useSideBarStore = defineStore('sideBarManager', () => {
    const isSideBarOn = ref(false);
    const SIDEBAR_ATTR = 'sidebar-on';

    const body = document.querySelector('body');

    function toggleSideBar() {
        //현재 body의 속성을 확인해서 상태 확인
        //해당 속성이 있으면 null이 아니므로 true, 없으면 null이므로 false
        const state = body.getAttribute(SIDEBAR_ATTR) !== null;
        
        //body에 sidebar-on 을 추가/삭제 하여 사이드 바 토글 구현
        if(state) {
            body.removeAttribute(SIDEBAR_ATTR);
        } else {
            body.setAttribute(SIDEBAR_ATTR, '');
        }

        //토글하면 상태가 변하였으므로 !state 할당
        isSideBarOn.value = !state;
    }

    return {
        isSideBarOn,
        toggleSideBar
    }
})