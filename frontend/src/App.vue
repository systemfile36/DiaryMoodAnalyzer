<script setup>
import TempSideBar from './components/TempSideBar.vue'
import TempNavBar from './components/TempNavBar.vue';
import Mask from './components/Mask.vue';
import Modal from './components/Modal.vue';

import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router';
import { useAuthManagerStore } from '@/stores/AuthManager';
import { storeToRefs } from 'pinia';

import { useThemeStore } from '@/stores/ThemeManager';

const authManager = useAuthManagerStore();

const themeManager = useThemeStore();

//테마 초기화
themeManager.initTheme();

</script>

<template>
  <!-- AuthManager.js를 통해 인증 여부 확인 -->
  <div v-if="authManager.isAuthenticated">
    <!-- 인증 되었을 때만 사이드바 같은 UI 표시 -->
    <TempSideBar/>
    <TempNavBar/>

    <!-- SideBar를 위한 마스크 -->
    <Mask/>

    <!-- 모달 창 -->
    <Modal/>

    <!-- 사이드바의 컨트롤을 위해 #main 아래에 <router-view/> 배치-->
    <div id="main">
      <router-view/>
    </div>
  </div>
  <div v-else>
    <!-- 인증 되지 않았을 때 -->
    <router-view/>

    <!--Add Modal for signup and login-->
    <Modal/>
  </div>

</template>

<style scoped>

</style>
