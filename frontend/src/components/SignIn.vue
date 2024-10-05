<template lang="">
    <div class="container">
        <h2 class="display-5 mb-4">Sign In</h2>
        <form class="p-2 border border-primary">
            <div class="mb-3">
                <label for="exampleInputEmail1" class="form-label">Email address</label>
                <input 
                     type="email" class="form-control" 
                     id="exampleInputEmail1" aria-describedby="emailHelp"
                     v-model="email">
                <div class="error form-text" v-if="errors['email']"> 
                    <div v-for="(message, i) in errors['email']" :key="i">
                        {{ message }}
                    </div>
                </div>
            </div>
            <div class="mb-3">
                <label for="exampleInputPassword1" class="form-label">Password</label>
              <input 
                    type="password" class="form-control" 
                   id="exampleInputPassword1"
                    v-model="password"    >
             </div>
             <button type="button" class="btn btn-primary"
              @click="signIn">Submit</button>
            </form>
    </div>

</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuthManagerStore } from '../stores/AuthManager';
import Validator from '../utils/Validator';
import ErrMessages from '../utils/ErrMessages';
import { useFormValidation } from '../utils/FormValidation';

//setup 내에서 vue-router의 router를 사용하기 위함
const router = useRouter();

const authManager = useAuthManagerStore();

const email = ref("");
const password = ref("");
const { errors } = useFormValidation([
  {
    field: email,
    fieldName: 'email',
    rules: [
      {
        validate: (value) => Validator.checkEmail(value),
        message: ErrMessages.ERR_EMAIL
      }
    ]
  }
]);

function signIn() {

    authManager.login({
        email: email.value,
        password: password.value
    });

}

//테스트용 
//
/*
onMounted(()=>{
    const accessToken = localStorage.getItem('accessToken');
    const claim = JSON.parse(window.atob(accessToken.split('.')[1]));
    let exp = new Date(claim.exp * 1000);
    console.log(exp.getTime(), Date.now());
    if(exp < Date.now()) {
        axios.post('api/token', {
            refreshToken: localStorage.getItem('refreshToken')
        }, {
            headers: {
                "Content-Type": 'application/json',
            }
        })
        .then((res)=> {
            localStorage.setItem('accessToken', res.data.accessToken);
            console.log(res.data);
            router.push('/');
        }).catch((error)=>{
            console.log(error);
        })
    } else {
        router.push('/');
    }
})
    */
</script>
<style lang="scss">
    .error {
        color: red;
    }
</style>