<template lang="">
    <div class="m-3">
        <h2 class="display-6 mb-4">회원가입</h2>
        <form class="p-2 border border-primary">
            <div class="mb-3">
                <label for="InputEmail" class="form-label">Email address</label>
                <input 
                     type="email" class="form-control" 
                     id="InputEmail" aria-describedby="emailHelp"
                     v-model.trim="email">
                <FormErrorText
                  :errors="errors"
                  :fieldName="'email'"/>
            </div>
            <div class="mb-3">
                <label for="InputPassword" class="form-label">Password</label>
              <input 
                    type="password" class="form-control" 
                   id="InputPassword"
                    v-model.trim="password">
              <FormErrorText
                :errors="errors"
                :fieldName="'password'"/>
             </div>
             <div class="mb-3">
              <label for="InputPasswordCheck" class="form-label">Password Check</label>
              <input
                type="password" class="form-control"
                id="InputPasswordCheck"
                v-model.trim="passwordCheck">
                <FormErrorText
                  :errors="errors"
                  :fieldName="'passwordCheck'"/>
             </div>
             <div class="d-flex">
              <button type="button" class="btn btn-primary"
              @click="signUp">회원가입</button>
             </div>  
        </form>
    </div>

</template>
<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import FormErrorText from './FormErrorText.vue'

import Validator from '../utils/Validator';
import ErrMessages from '../utils/ErrMessages';
import { useFormValidation } from '../utils/FormValidation';
import { useAuthManagerStore } from '../stores/AuthManager';

//setup 내에서 vue-router의 router를 사용하기 위함
const router = useRouter();

const authManager = useAuthManagerStore();

const email = ref("");
const password = ref("");
const passwordCheck = ref("");

//컴포저블을 통해 폼 입력값의 유효성 검사
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
  },
  {
    field: passwordCheck,
    fieldName: 'passwordCheck',
    rules: [
      {
        //비밀번호 확인란은 비밀번호의 값과 일치해야함
        validate: (value) => value == password.value,
        message: ErrMessages.ERR_PASSWORD_CHECK
      }
    ]
  }
]);

function signUp() {
  if(Object.entries(errors.value).length === 0) {
    authManager.signUp({
        email: email.value,
        password: password.value
    })
  } else {
    alert(ErrMessages.ERR_INVALID_FORM_INPUT);
  }
}
</script>
<style lang="scss">
    .error {
        color: red;
    }
</style>