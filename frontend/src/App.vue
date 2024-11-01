<script setup>
import TempSideBar from './components/TempSideBar.vue'
import TempNavBar from './components/TempNavBar.vue';
import Mask from './components/Mask.vue';

import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router';
import { useAuthManagerStore } from './stores/AuthManager';
import { storeToRefs } from 'pinia';


const authManager = useAuthManagerStore();


</script>

<template>
  <!-- AuthManager.js를 통해 인증 여부 확인 -->
  <div v-if="authManager.isAuthenticated">
    <!-- 인증 되었을 때만 표시 -->
    <TempSideBar/>
    <TempNavBar/>
    <Mask/>

    <!-- 사이드바의 컨트롤을 위해 #main 아래에 <router-view/> 배치-->
    <div id="main">
      <router-view/>
    </div>
  </div>
  <div v-else>
    <!-- 인증 되지 않았을 때-->
    <router-view/>
  </div>

</template>

<style scoped>

</style>
