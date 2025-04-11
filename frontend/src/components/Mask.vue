<!-- 사이드 바가 켜져 있을 때, 메인 화면을 가리는 반투명 mask 컴포넌트 -->

<template>
    <!-- 사이드 바가 켜져 있을 때만 표시함 -->
    <div class="mask" :class="{ 'd-block' : sidebarStore.isSideBarOn }"
        @click="onClickMask">
    </div>
</template>
<script setup>
import { useSideBarStore } from '@/stores/SideBarManager';

const sidebarStore = useSideBarStore(); 

//마스크 된 곳을 클릭하면 사이드 바를 접음 
function onClickMask() {
    sidebarStore.toggleSideBar();
}
</script>
<style scoped lang="scss">
    @import '../style.scss';

    .mask {
        //기본적으로는 off
        display: none;
        position: fixed;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.6);
        
        //top, right, bottom, left 모두 0 (꽉 채움)
        inset: 0 0 0 0;

        //사이드 바 보다 아래에 있게 함
        z-index: 1;
    }
</style>