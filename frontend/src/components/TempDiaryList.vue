<template>
    <div id="diary-list" class="flex-grow-1 px-4">
        <article class="card-wrapper card" v-for="(diary, i) in diaries" :key="i">
            <router-link :to="`/diaries/${diary.id}`" class="diary-preview row g-0 flex-md-row-reverse">
                <div class="col-md-12">
                    <div class="card-body d-flex flex-column">
                        <h1 class="card-title my-2 mt-md-0 text-primary-emphasis">
                            {{ getTruncated(diary.title, 50) }}
                        </h1>
                        <div class="card-text content mt-0 mb-3 text-secondary">
                            <p class="text-clip-2"> {{ getTruncated(diary.content) }} </p>
                        </div>
                        <div class="diary-meta flex-grow-1 d-flex align-items-end text-primary-emphasis">
                            <div class="me-auto">
                                <!-- 작성 시각 -->
                                <i class="fa-regular fa-calendar"></i>
                                <time>
                                    {{ diaryManager.formatDate(diary.createdAt) }}
                                </time>
                                <!-- 감정 분석 결과 -->
                                <i class="fa-regular fa-face-smile"></i>
                                <span class="sentimental">not bad</span>
                                <!-- 코멘트 개수 -->
                                <i class="fa-solid fa-comments"></i>
                                <span class="comments">{{ diary.commentsSize + "개" }}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </router-link>
        </article>
        <!-- Pagination -->
        <nav aria-label="Page navigation">
            <ul class="pagination justify-content-center">
                <li class="page-item" :class="{ disabled : diaryManager.isFirstPage() }" >
                    <a class="page-link" href="#"
                        @click="onMovePage(diaryManager.currentPage - 1)">
                        <i class="fa-solid fa-arrow-left"></i>
                    </a>
                </li>
                <li class="page-item" v-for="i in diaryManager.currentTotalPages" :key="i" 
                     :class="{ active : (diaryManager.currentPage + 1) === i}">
                    <!-- convert 1-indexed to 0-indexed -->
                    <a class="page-link" href="#" @click="onMovePage(i-1)">{{ i }}</a>
                </li>
                <li class="page-item" :class="{ disabled : diaryManager.isLastPage() }">
                    <a class="page-link" href="#"
                        @click="onMovePage(diaryManager.currentPage + 1)">
                        <i class="fa-solid fa-arrow-right"></i>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
</template>
<script setup>
import { onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useDiaryManagerStore } from '../stores/DiaryManager';

const diaryManager = useDiaryManagerStore();

//반응형을 유지하기 위해 destructuring 해서 받아옴 
const { diaries } = storeToRefs(diaryManager);

onMounted(async ()=>{
    //마운트 되면 다이어리들 로드 
    await diaryManager.loadDiaries();

})

function onMovePage(page) {
    diaryManager.setDiaryPage(page);
}

 /**
  * maxLength 만큼 자른 값을 반환한다. 
  * 만약 길이가 maxLength보다 작다면 그대로 리턴한다.
  * @param {string} content 
  * @param {number} maxLength 최대 길이. 기본값 140
  * @returns {string}
  */
function getTruncated(content, maxLength = 140) {

    //길이가 최대 길이를 넘으면 잘라서, 그렇지 않으면 그냥 리턴
    return content.length > maxLength ? 
        content.substring(0, maxLength) + '...' : content;
}

</script>
<style scoped lang="scss">
    #diary-list {
        margin-top: 2.5rem;

        //a 태그의 기본 값인 텍스트 밑줄 제거
        * {
            text-decoration: none;
        }

        //두 줄을 넘는 콘텐츠 텍스트는 ...으로 truncated 한다.
        //webkit을 사용한다. 
        .text-clip-2 {
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            line-clamp: 2;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        //다이어리 미리보기 창 
        .diary-preview {
            //배경 가상 요소. opacity의 변화에 따라 transition
            //&:를 사용해서 .diary-preview의 가상요소에만 적용(안그러면 )
            &:before {
                content: "";
                width: 100%;
                height: 100%;
                position: absolute;
                background-color: var(--bs-dark);
                opacity: 0;
                transition: opacity, .35s ease-in-out;
            }

            //배경 요소에 hover하면 투명도 올림 (호버 강조 효과)
            &:hover {
                &::before {
                    opacity: 0.3;
                }
            }

            background-color: var(--bs-body-bg);
            
        }

        //마지막 요소가 아니라면 아래에 margin
        .card-wrapper:not(:last-child) {
            margin-bottom: 1.25rem;
        }

        .diary-meta {
            //아이콘과 거리 두기 
            time, span {
                padding-left: 0.5rem;
            }

            //첫번째 요소가 아닌 i(아이콘)의 왼쪽 margin을 설정
            i:not(:first-child) {
                margin-left: 1.5rem;
            }
        }
    }
</style>