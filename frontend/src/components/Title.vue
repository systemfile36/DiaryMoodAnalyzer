<template>
    <div class="container text-center">
        <h2 class="display-3">Title</h2>
        <div class="container text-center btns row">
            <router-link to="/signup"
                class="btn btn-primary row">SignUp</router-link>
            <router-link to="/signin"
                class="btn btn-primary row">SignIn</router-link>
            <button class="btn btn-primary row"
                @click="decodingJWT">Test</button>
            <p class="row" v-if="isLogined">{{ userEmail }}</p>
            <router-link to="/diary"
                class="btn btn-primary row">Write Diary</router-link>
        </div>
        
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'

let userEmail = "";

let isLogined = ref(false);

let expire = new Date();

function decodingJWT() {
    const accessToken = localStorage.getItem('accessToken');
    const claim = JSON.parse(window.atob(accessToken.split('.')[1]));
    userEmail = claim.sub;
    expire = new Date(claim.exp * 1000);
    console.log(userEmail, expire);
}

onMounted(()=>{
    const accessToken = localStorage.getItem('accessToken');
    const claim = JSON.parse(window.atob(accessToken.split('.')[1]));
    userEmail = claim.sub;
    if(userEmail != null) {
        console.log(userEmail);
        isLogined.value = true;
    }
})

</script>
<style lang="scss">
    .btns {
        .btn {
            margin: 10px;
        }
    }
</style>