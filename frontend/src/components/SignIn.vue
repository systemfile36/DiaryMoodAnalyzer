<template lang="">
    <div class="m-3">
        <h2 class="display-5 mb-4 text-center">로그인</h2>
        <form class="p-2 d-flex flex-column">
            <div class="mb-3">
                <label for="InputEmail" class="form-label">Email address</label>
                <input 
                     type="email" class="form-control" 
                     id="InputEmail" aria-describedby="emailHelp"
                     v-model="email">
                <FormErrorText
                    :errors="errors"
                    :fieldName="'email'"/>
            </div>
            <div class="mb-3">
                <label for="InputPassword" class="form-label">Password</label>
              <input 
                    type="password" class="form-control" 
                   id="InputPassword"
                    v-model="password">
             </div>

            <div class="d-flex">
                <button type="button" class="btn btn-primary"
                @click="signIn">로그인</button>
                <router-link to="/signup" class="btn btn-light">
                계정 만들기
                </router-link>
            </div>
        </form>
    </div>

</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import FormErrorText from './FormErrorText.vue';

import { useAuthManagerStore } from '@/stores/AuthManager';
import Validator from '@/utils/Validator';
import ErrMessages from '@/utils/ErrMessages';
import { useFormValidation } from '@/utils/FormValidation';

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

</script>
<style lang="scss">
    .error {
        color: red;
    }


</style>