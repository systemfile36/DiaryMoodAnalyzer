<template>
  <div class="signup-container">
    <div class="signup-wrapper m-3">
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
            <div class="verify-wrapper mb-3">
              <!--Disable input field and buttons when loading-->
              <input 
                type="text" id="InputEmailVerificationCode"
                class="form-control" inputmode="numeric" placeholder="Email Verification Code"
                pattern="\d*"
                :disabled="states.verify.isLoading || states.verify.isSuccess"
                v-model.trim="emailVerificationCode"/>
              <!--Change button style via `isSuccess` flag-->
              <button type="button" class="send-code btn"
                :class="states.send.isSuccess ? 'btn-success' : 'btn-outline-secondary'"
                :disabled="states.send.isLoading"
                @click="onClickSend">
              {{ states.send.isLoading ? '' : 'Send' }}
              <i v-if="states.send.isLoading" class="fa-solid fa-spinner fa-spin-pulse"></i>
            </button>
            <button type="button" class="submit-code btn"
              :class="states.verify.isSuccess ? 'btn-success' : 'btn-outline-secondary'"
              @click="onClickVerify">
              {{ states.verify.isLoading ? '' : 'Verify' }}
            </button>
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
                v-model="isExpert">
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
    <div class="signup-background"></div>
  </div>
</template>
<script setup>
import { ref, reactive } from 'vue'
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
const emailVerificationCode = ref("");
const password = ref("");
const passwordCheck = ref("");

const isExpert = ref(false);

// Wrapper of flags for email verification
// use `reactive` for reactive object. 
// See folowing link: https://vuejs.org/guide/essentials/reactivity-fundamentals.html#reactive
const states = reactive({
  verify: {
    isSuccess: false, 
    isLoading: false,
  }, 
  send: {
    isSuccess: false, 
    isLoading: false
  }
})

async function onClickSend() {
  // Set loading flag
  states.send.isLoading = true;
  await authManager.sendVerificationCode(email.value, 
    //onSuccess
    ()=>{
      // Reset loading flag
      states.send.isLoading = false;
      states.send.isSuccess = true;

      modalManager.setModalText(
        "Info", 
        "인증 번호가 전송되었습니다. 인증번호는 30분간 유효합니다. 시간이 지나도 도착하지 않을 경우, 스팸메일함을 확인해주세요.", 
        "확인", "닫기"
      );
      modalManager.openModal();
    }, 
    //onFailure
    ()=>{
      states.send.isSuccess = false;
      states.send.isLoading = false;
      // Set modal state and open it
      modalManager.setModalText(
        "Error", "전송에 실패하였습니다. 잠시후 다시 시도해주세요.",
        "확인", "닫기"
      );
      modalManager.openModal();
    }
  );
}

async function onClickVerify() {
  // Set loading flag
  states.verify.isLoading = true;
  await authManager.verifyCode(
    email.value, emailVerificationCode.value, 
    ()=>{
      states.verify.isLoading = false;
      states.verify.isSuccess = true;

      modalManager.setModalText(
        "Info", "인증되었습니다. 회원가입을 계속해주세요.", 
        "확인", "닫기"
      );

      modalManager.openModal();
    }, 
    ()=>{
      states.verify.isSuccess = false;
      states.verify.isLoading = false;
      modalManager.setModalText(
        "Error", "인증에 실패하였습니다.", 
        "확인", "닫기"
      );
      modalManager.openModal();
    }, 
    ()=>{
      states.verify.isSuccess = false;
      states.verify.isLoading = false;
      modalManager.setModalText(
        "Error", "인증 횟수가 초과하였습니다. 인증 코드를 재발급해주세요.", 
        "확인", "닫기"
      );
      modalManager.openModal();
    }
  );
}

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

async function signUp() {
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
    await authManager.signUp({
      email: email.value,
      password: password.value,
      authority: isExpert.value ? "EXPERT": "USER"
    }, (_)=>{
      modalManager.setModalText(
        "Info", "회원가입에 성공하였습니다.", 
        "확인", "닫기"
      ); 
      modalManager.openModal(()=>{
        router.push("/");
      });
    }, (error)=>{
      // Check status code and set proper message for modal
      let bodyText = null;
      if(error.response.status == "409") {
        bodyText = "중복된 이메일입니다. 다른 이메일을 사용해주세요.";
      } else if(error.response.status == "403") {
        bodyText = "인증되지 않은 이메일입니다. 먼저 인증을 진행해주세요.";
      }
      modalManager.setModalText(
          "Error", bodyText, 
          "확인", "닫기"
        );

      modalManager.openModal();
    });
  }
}

</script>
<style lang="scss">

  .signup-container {
    // align to center both of x and y
    display: flex;
    justify-content: center;
    align-items: center;

    height: 100vh;
  }

  .signup-wrapper {
    // set width and limit width to fixed value
    width: 100%;
    max-width: 600px;

    padding: 1.5rem;

    background-color: var(--bs-body-bg);

    // set border
    border-radius: 1rem;
    border: 1px solid var(--bs-border-color);
    box-shadow: 0 0.5rem 1rem rgba(black, 0.15);

    // verification code form style
    .verify-wrapper {
      display: flex;

      // Limit width of buttons
      .send-code {
        width: 25%;
      }
      .submit-code {
        width: 25%;
      }
    }
  }

  .signup-background {
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