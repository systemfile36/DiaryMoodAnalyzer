<template>
    <div class="container text-center">
        <div class="container">
            <h2 class="display-3">Title</h2>
            <h3 class="fs-6" v-if="authManager.isAuthenticated">
                {{ `Wellcome ${authManager.userName}` }}
            </h3>
        </div>
        <div class="flex-grow-1 px-xl-1">
            <div v-if="!authManager.isAuthenticated">
                <router-link to="/signup"
                    class="btn btn-primary">SignUp</router-link>
                <router-link to="/signin"
                    class="btn btn-primary">SignIn</router-link>
            </div>
            <div v-else>
            <router-link to="/diary"
                class="btn btn-primary">Write Diary</router-link>
            <button class="btn btn-primary"
                @click="logoutHandler">Logout</button>
            </div>         
        </div>       
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router';
import { useAuthManagerStore } from '../stores/AuthManager';

const authManager = useAuthManagerStore();

const router = useRouter();

function logoutHandler() {
    authManager.logout();
    
}


</script>
<style lang="scss">

    .btn {
        margin: 10px;
        width: 100%;
    }

</style>