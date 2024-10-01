<template lang="">
    <div class="container">
        <h2 class="display-5 mb-4">Sign Up</h2>
        <form class="p-2 border border-primary">
            <div class="mb-3">
                <label for="exampleInputEmail1" class="form-label">Email address</label>
                <input 
                     type="email" class="form-control" 
                     id="exampleInputEmail1" aria-describedby="emailHelp"
                     v-model="email">
                <div id="emailHelp" class="form-text">We'll never share your email with anyone else.</div>
            </div>
            <div class="mb-3">
                <label for="exampleInputPassword1" class="form-label">Password</label>
              <input 
                    type="password" class="form-control" 
                   id="exampleInputPassword1"
                    v-model="password"    >
             </div>
             <div class="mb-3 form-check">
              <input type="checkbox" class="form-check-input" id="exampleCheck1">
              <label class="form-check-label" for="exampleCheck1">Check me out</label>
             </div>
             <button type="button" class="btn btn-primary"
              @click="signUp">Submit</button>
            </form>
    </div>

</template>
<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

//setup 내에서 vue-router의 router를 사용하기 위함
const router = useRouter();

const email = ref("");
const password = ref("");

//회원가입 테스트용 메소드 
function signUp() {

    axios.post('/api/auth/signup',
        {
            email: email.value,
            password: password.value
        }
    ).then((res) => {
        console.log(res.status);
        alert('SignUp Success!');
        router.push('/');
    }).catch((error) => {
        console.log(error);
    })

}
</script>
<style lang="scss"></style>