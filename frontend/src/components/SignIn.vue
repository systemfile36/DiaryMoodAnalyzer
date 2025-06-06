<template>
<div class="signin-container">
    <div class="signin-wrapper m-3">
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
    <div class="signin-background"></div>
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
import { useModalManagerStore } from '../stores/ModalManager';

//setup 내에서 vue-router의 router를 사용하기 위함
const router = useRouter();

const authManager = useAuthManagerStore();

const modalManager = useModalManagerStore();

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
    }, (err) => {
        // When 401 Unauthorized
        let bodyText = null;
        if(err.response.status == "401") {
            bodyText = "이메일 또는 비밀번호가 맞지 않습니다."
        } else {
            bodyText = "로그인에 실패하였습니다. 잠시후 다시 시도해주세요."
        }
        modalManager.setModalText(
            "Error", bodyText, 
            "확인", "닫기"
        ); 
        modalManager.openModal();
    });

}

</script>
<style lang="scss">

    .signin-container{
        display: flex;
        justify-content: center;
        align-items: center;

        height: 100vh;
    }

    .signin-wrapper {
        width: 100%;

        max-width: 600px;

        padding: 1.5rem;

        background-color: var(--bs-body-bg);

        // set border
        border-radius: 1rem;
        border: 1px solid var(--bs-border-color);
        box-shadow: 0 0.5rem 1rem rgba(black, 0.15);
    }

    .signin-background {
        position: absolute;
        width: 100%;
        height: 100%;

        // fill entire display
        // top right bottom left
        inset: 0 0 0 0;

        background-color: rgba(0, 0, 0, 0.4);

        // lower priority
        z-index: -1;
    }

    .error {
        color: red;
    }
</style>