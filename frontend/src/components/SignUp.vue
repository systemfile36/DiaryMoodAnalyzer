<template lang="">
    <div class="m-3">
        <h2 class="display-6 mb-4 text-center">회원가입</h2>
        <form class="p-2 d-flex flex-column">
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
                    v-model.trim="password"
                    @input="checkRules"> 
                    <!-- 비밀 번호가 바뀌면 비밀 번호 일치도 갱신해야 함 -->
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

             <div class="form-check form-switch mb-3 mt-3">
                <input class="form-check-input" type="checkbox" role="switch"
                value="" id="isExpert"
                @change="onIsExpertChanged">
                <label class="form-check-label" for="isExpert">
                    당신은 전문가 입니까?
                </label>
             </div>

             <div class="d-flex">
              <button type="button" class="btn btn-primary"
              @click="signUp">회원가입</button>
              <button type="button" class="btn btn-light"
                @click="$router.go(-1)">
                뒤로 가기
              </button>
             </div>  
        </form>
    </div>

</template>
<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import FormErrorText from './FormErrorText.vue'

import Validator from '@/utils/Validator';
import ErrMessages from '@/utils/ErrMessages';
import Authority from '@/utils/Authority';
import { useFormValidation } from '@/utils/FormValidation';
import { useAuthManagerStore } from '@/stores/AuthManager';
import { useModalManagerStore } from '@/stores/ModalManager';

//setup 내에서 vue-router의 router를 사용하기 위함
const router = useRouter();

const authManager = useAuthManagerStore();

const modalManager = useModalManagerStore();

const email = ref("");
const password = ref("");
const passwordCheck = ref("");

const isExpert = ref(false);

//컴포저블을 통해 폼 입력값의 유효성 검사
//checkRules도 받아옴 
const { errors, checkRules } = useFormValidation([
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
  //예외 처리 강제 실행
  checkRules();

  if(Object.keys(errors.value).length > 0) {
    modalManager.setModalText(
      "입력값 오류",
      "잘못된 입력 값이 있습니다. 다시 확인해 주세요.",
      "확인", "닫기"
    );

    modalManager.openModal(null, null);
  } else {
    authManager.signUp({
      email: email.value,
      password: password.value,
      authority: isExpert.value ? "EXPERT": "USER"
    })
  }
}

function onIsExpertChanged(event) {
  isExpert.value = event.target.checked;
}

</script>
<style lang="scss">
    .error {
        color: red;
    }
</style>