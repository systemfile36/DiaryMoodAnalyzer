<script setup>
import TempSideBar from './components/TempSideBar.vue'
import TempNavBar from './components/TempNavBar.vue';
import Mask from './components/Mask.vue';

import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router';
import { useAuthManagerStore } from './stores/AuthManager';
import { storeToRefs } from 'pinia';

const router = useRouter();

const authManager = useAuthManagerStore();

const { isAuthenticated }  = storeToRefs(authManager);

//접속 시, 인증되지 않았으면 로그인 화면으로 리다이렉트
onMounted(async ()=>{
  //로컬 스토리지에서 인증 정보 세팅 
  await authManager.initStates();

  //인증 정보를 확인하여 실행 
  if(!isAuthenticated.value) {
    router.push('/signin')
  }
})

</script>

<template>
  
  <div v-if="authManager.isAuthenticated">
    <!-- 인증 되었을 때만 표시 -->
    <TempSideBar/>
    <TempNavBar/>
    <Mask/>

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
