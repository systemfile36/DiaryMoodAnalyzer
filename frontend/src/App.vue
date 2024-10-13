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
