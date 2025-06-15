<template>
    <article class="diary-detail px-4">
        <!-- Bootstrap Flex의 기능들을 활용하여 레이아웃 구성 -->
        <header class="mt-4 d-flex">
            <div class="diary-meta mt-3 d-flex flex-column">
                <h1 class="title">{{ diary.title }}</h1>
                <span>{{ diaryManager.formatDate(diary.createdAt, 'YYYY년 MM월 DD일') }}</span>
                <span class="mt-1">{{ `${contentLength} 자` }}</span>
            </div>

            <button class="delete btn btn-outline-danger ms-auto align-self-end"
                @click="onDelete">삭제
            </button>
        </header>
        
        <div class="content mt-4">
            <p class="mt-4">{{ diary.content }}</p>
        </div>

        <!-- Comment 영역 (후에 서버에서 받아올 것)-->
        <div class="comment-aria mt-4">
            <div class="comment-header mt-3">
                <i class="fa-solid fa-comments"></i>
                <span class="comment-total ms-3">{{ `${comments.length}개` }}</span>
            </div>

            <!-- 코멘트 목록 v-for로 출력 -->
            <ul class="comment-list mt-2">
                <!-- Comment -->
                <li class="comment-item py-3 d-flex" v-for="(comment, i) in comments" :key="i">
                    <i class="fa-solid fa-user profile mt-2"></i>
                    <div class="comment-wrapper d-flex flex-column ms-3">
                        <h3 class="comment-author mb-2">{{ comment.expertEmail }}</h3>
                        <span class="comment-content">
                            {{ comment.content }}
                        </span>
                    </div>   
                </li>
                <!-- Indicate no comment on this diary -->
                <span v-if="comments.length < 1">코멘트가 없습니다.</span>
            </ul>

            <!-- VAD score에 해석에 대한 안내 -->
            <div v-if="diary.vadScore != null" class="analyze-detail mt-4 p-3 border rounded bg-light">
                <h5 class="mb-3">
                    <i class="fa-solid fa-chart-line me-2"></i>감정 분석 결과 (VAD 점수)
                </h5>

                <ul class="ps-3 mb-3">
                    <li>
                        <strong>Valence (기분 정도)</strong>:
                        <span class="ms-1">{{ diary.vadScore.v }}</span>
                        <small class="text-muted ms-2">1 (부정적) ~ 9 (긍정적)</small>
                    </li>
                    <li>
                        <strong>Arousal (긴장감)</strong>:
                        <span class="ms-1">{{ diary.vadScore.a }}</span>
                        <small class="text-muted ms-2">1 (평온/무기력) ~ 9 (긴장/활성)</small>
                    </li>
                    <li>
                        <strong>Dominance (통제감)</strong>:
                        <span class="ms-1">{{ diary.vadScore.d }}</span>
                        <small class="text-muted ms-2">1 (무력) ~ 9 (주도적)</small>
                    </li>
                </ul>

                <p class="text-muted mb-0" style="font-size: 0.9rem;">
                    * 이 점수는 일기의 내용 기반으로 자동 분석된 결과이며, 참고용으로 제공됩니다.
                </p>
            </div>
            <div v-else class="analyze-detail mt-4 p-3 border rounded bg-light">
                <h5 class="mb-3">아직 측정 중입니다...</h5>
            </div>

            <form class="comment-write d-flex flex-column mt-3"
            v-if="authManager.role === Authority.EXPERT">
                <div class="mb-3 d-flex align-itmes-center">
                    <label for="writer-name" class="form-label">작성자</label>
                    <input type="text" id="writer-name" class="form-control ms-3" 
                    :value="authManager.userName"
                    :style="`width: ${authManager.userName.length + 5}ch`"
                    disabled>
                </div>
  
                <div class="mb-3">
                    <textarea id="comment" name="comment" class="form-control" placeholder="내용을 입력해주세요."
                    v-model="commentContent"></textarea>
                </div>

                <div class="mb-3">
                    <button id="comment-submit" type="button" class="btn btn-primary"
                    @click="onAddComment">코멘트 등록</button>
                </div>
            </form>
        </div>
    </article>
</template>
<script setup>
import { useDiaryManagerStore } from '@/stores/DiaryManager';
import { useAuthManagerStore } from '@/stores/AuthManager';
import { useModalManagerStore } from '@/stores/ModalManager';
import { useCommentManagerStore } from '@/stores/CommentManager';

import { storeToRefs } from 'pinia';

import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import Authority from '@/utils/Authority'


const diaryManager = useDiaryManagerStore();

const modalManager = useModalManagerStore();

const authManager = useAuthManagerStore();

const commentManager = useCommentManagerStore();

//코멘트 목록 
const { comments } = storeToRefs(commentManager);

const route = useRoute();

const router = useRouter();

//null 참조를 막기 위해 초기값을 넣어준다. 
//onMounted는 DOM 생성 후 발생하므로, 
//초기값을 null로 하면 템플릿 컴파일 시 에러가 난다.
const diary = ref({});

const contentLength = ref(0);

const commentContent = ref("");

//URL 파라미터로 받은 id로 다이어리를 읽어옴
onMounted(async ()=>{
    diary.value = await diaryManager.getDiaryById(route.params.id);

    if(diary.value === null){
        console.log('Invalid URL Parameter');
        router.go(-1);
    } else {
        contentLength.value = diary.value.content.length;
    }

    //코멘트 로드 
    await commentManager.loadCommentsByDiaryId(route.params.id);

})

//삭제 시 모달 창 띄움 
function onDelete() {

    modalManager.setModalText(
        "삭제 확인",
        "정말 삭제하시겠습니까?",
        "예",
        "아니오"
    );

    modalManager.openModal(()=>{
        //삭제 요청을 보낸 후, 목록으로 리다이렉트 
        diaryManager.deleteDiary(diary.value['id']);
        router.push('/diaries');
    }, null);
}

function onAddComment() {
    modalManager.setModalText(
        "작성 확인",
        "코멘트를 작성하시겠습니까?",
        "예",
        "취소"
    );

    modalManager.openModal(async ()=>{
        await commentManager.addCommentByDiaryId(route.params.id, commentContent.value);
        router.go(0);
    }, null);
}

</script>
<style scoped lang="scss">
    //seperator
    .comment-aria, .content {
        border-top: 1px solid #ccc;
    }

    .comment-list {
        list-style: none;
    }

    .profile {
        font-size: 2rem;
    }

    //Sizing delete button
    .delete {
        width: 20%;
        height: 50%;
    }

    //코멘트 창 관련 사이즈와 패딩 조정 
    #comment {
        width: 65%;
        min-width: 300px;
        height: 141px;
        max-width: 660px;
    }

    #comment-submit {
        width: 65%;
        min-width: 300px;
        max-width: 660px;
    }

    .comment-write {
        margin-left: 2rem;
    }

    // Temp analyze detail display
    .analyze-info {
        margin-top: 2.0rem;
        border-top: 1px solid #ccc;
    }

</style>