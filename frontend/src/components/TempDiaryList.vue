<template>
    <div id="diary-list" class="flex-grow-1 px-md-4">
        <article class="card-wrapper card" v-for="(diary, i) in diaryManager.diaries" :key="i">
            <router-link to="#" class="diary-preview row g-0 flex-md-row-reverse">
                <div class="col-md-12">
                    <div class="card-body d-flex flex-column">
                        <h1 class="card-title my-2 mt-md-0 text-primary-emphasis">{{ diary.title }}</h1>
                        <div class="card-text content mt-0 mb-3 text-secondary">
                            <p> {{ diary.content }} </p>
                        </div>
                        <div class="diary-meta flex-grow-1 d-flex align-items-end text-primary-emphasis">
                            <div class="me-auto">
                                <i class="fa-regular fa-calendar"></i>
                                <time>{{ new Date(diary.createdAt).toISOString().split('T')[0] }}</time>
                            </div>
                        </div>
                    </div>
                </div>
            </router-link>
        </article>
    </div>
</template>
<script setup>
import { onMounted } from 'vue';
import { useDiaryManagerStore } from '../stores/DiaryManager';

const diaryManager = useDiaryManagerStore();

onMounted(()=>{
    diaryManager.getDiaries();
    console.log(`diaries : ${diaryManager.diaries}`);
})

</script>
<style lang="scss">
    #diary-list {
        margin-top: 2.5rem;

        * {
            text-decoration: none;
        }

        .diary-preview {

            //배경 가상 요소. opacity의 변화에 따라 transition
            //&:를 사용해서 .diary-preview의 가상요소에만 적용(안그러면 )
            &:before {
                content: "";
                width: 100%;
                height: 100%;
                position: absolute;
                background-color: var(--bs-secondary-bg);
                opacity: 0;
                transition: opacity, .35s ease-in-out;
            }

            //
            &:hover {
                &::before {
                    opacity: 0.3;
                }
            }

            background-color: var(--bs-body-bg);
            border-radius: .625rem;
            
        }

        .card-wrapper:not(:last-child) {
            margin-bottom: 1.25rem;
        }

        .diary-meta {
            time {
                padding-left: 0.5rem;
            }
        }
    }
</style>