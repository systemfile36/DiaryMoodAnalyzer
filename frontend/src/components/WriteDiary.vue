<template>
    <!-- card 사용 -->
    <div class="card m-3 p-4 shadow-sm">
        <div class="card-header">
            <h2 class="display-5 mb-2">Diary 작성</h2>
        </div>
        <div class="card-body">
            <form class="d-flex flex-column">
                <div class="mb-3">
                    <label for="InputTitle" class="form-label">Diary Title</label>
                    <input 
                        type="text" class="form-control"
                        id="InputTitle"
                        maxlength="256"
                        required
                        v-model="title">
                    <!-- Composable을 통한 에러 메시지-->
                    <FormErrorText
                        :errors="errors"
                        :fieldName="'title'"/>
                </div>
                <div class="mb-3">
                    <label for="InputContent" class="form-label">Diary Content</label>
                    <textarea class="form-control" id="InputContent" 
                    rows="15" maxlength="1500" required 
                    style="resize: none;" v-model="content"></textarea>
                    <!-- Composable을 통한 에러 메시지-->
                    <FormErrorText
                        :errors="errors"
                        :fieldName="'content'"/>
                </div>

                <div class="d-flex ">
                    <button type="button" class="btn btn-outline-primary w-100 mt-3"
                    @click="onSubmit">Diary 작성</button>
                </div>
            </form>
        </div>
        
    </div>

</template>
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

//form 유효성 검사를 위한 import
import FormErrorText from './FormErrorText.vue';
import Validator from '../utils/Validator';
import ErrMessages from '../utils/ErrMessages';
import { useFormValidation } from '../utils/FormValidation';

import { useDiaryManagerStore } from '../stores/DiaryManager';
import { useModalManagerStore } from '../stores/ModalManager';

const router = useRouter();

const diaryManager = useDiaryManagerStore();

const modalManager = useModalManagerStore();

const title = ref("");
const content = ref("");

//Form Validation
const { errors, checkRules } = useFormValidation([
    {
        field: title,
        fieldName: 'title',
        rules: [
            {
                validate: (value) => value.length > 0,
                message: '제목을 입력해주세요.'
            },
            {
                validate: (value) => Validator.checkDiaryTitleMaxLength(value),
                message: '제목이 너무 깁니다!'
            }
        ]
    },
    {
        field: content,
        fieldName: 'content',
        rules: [
            {
                validate: (value) => value.length > 0,
                message: '본문을 입력해주세요.'
            },
            {
                validate: (value) => Validator.checkDiaryContentMaxLength(value),
                message: '본문이 너무 깁니다!'
            }
        ]
    }
]);

function onSubmit() {

    //유효성 검사를 수동으로 실행하여 errors 갱신 
    checkRules();

    //errors에 프로퍼티가 하나라도 있다면(=에러가 난 필드가 있다면) 추가하지 않음
    if(Object.keys(errors.value).length > 0) {
        modalManager.setModalText(
            "폼 오류",
            "잘못된 입력 값이 있습니다. 다시 확인해 주세요.",
            "확인","닫기"
        );
        modalManager.openModal(null, null);
    } else {
        diaryManager.addDiary(title.value, content.value);
    }
    //
}

</script>
<style lang="scss">
    
</style>