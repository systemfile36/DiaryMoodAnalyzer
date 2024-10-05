<template lang="">
    <div class="container">
        <h2 class="display-6 mb-4">Sign Up</h2>
        <form class="p-2 border border-primary">
            <div class="mb-3">
                <label for="InputEmail" class="form-label">Email address</label>
                <input 
                     type="email" class="form-control" 
                     id="InputEmail" aria-describedby="emailHelp"
                     v-model.trim="email">
                <div class="error form-text" v-if="errors['email']"> 
                    <div v-for="(message, i) in errors['email']" :key="i">
                        {{ message }}
                    </div>
                </div>
            </div>
            <div class="mb-3">
                <label for="InputPassword" class="form-label">Password</label>
              <input 
                    type="password" class="form-control" 
                   id="InputPassword"
                    v-model.trim="password">
               <div class="error form-text" v-if="errors['password']"> 
                    <div v-for="(message, i) in errors['password']" :key="i">
                        {{ message }}
                    </div>
                </div>
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
import Validator from '../utils/Validator';
import ErrMessages from '../utils/ErrMessages';
import { useFormValidation } from '../utils/FormValidation';
import { useAuthManagerStore } from '../stores/AuthManager';

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
  },
  {
    field: password,
    fieldName: 'password',
    rules: [
      {
        validate: (value) => Validator.checkPassword(value),
        message: ErrMessages.ERR_PASSWORD
      }
    ]
  }
]);

//회원가입 테스트용 메소드 
function signUp() {

    authManager.signUp({
        email: email.value,
        password: password.value
    })

}
</script>
<style lang="scss">
    .error {
        color: red;
    }
</style>