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
                <span class="comment-total ms-3">1개</span>
            </div>

            <ul class="comment-list mt-2">
                <!-- Comment -->
                <li class="comment-item py-3 d-flex">
                    <i class="fa-solid fa-user profile mt-2"></i>
                    <div class="comment-wrapper d-flex flex-column ms-3">
                        <h3 class="comment-author mb-2">Professor</h3>
                        <span class="comment-content">
                            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                            Maecenas maximus nunc eget nibh dignissim varius. Cras gravida fermentum sem ut tincidunt.
                        </span>
                    </div>   
                </li>
            </ul>
        </div>
    </article>
</template>
<script setup>
import { useDiaryManagerStore } from '../stores/DiaryManager';
import { useAuthManagerStore } from '../stores/AuthManager';
import { useModalManagerStore } from '../stores/ModalManager';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';


const diaryManager = useDiaryManagerStore();

const modalManager = useModalManagerStore();

const route = useRoute();

const router = useRouter();

//null 참조를 막기 위해 초기값을 넣어준다. 
//onMounted는 DOM 생성 후 발생하므로, 
//초기값을 null로 하면 템플릿 컴파일 시 에러가 난다.
const diary = ref({});

const contentLength = ref(0);

//URL 파라미터로 받은 id로 다이어리를 읽어옴
onMounted(()=>{
    diary.value = diaryManager.getDiaryById(route.params.id);

    if(diary.value === null){
        console.log('Invalid URL Parameter');
        router.go(-1);
    } else {
        contentLength.value = diary.value.content.length;
    }
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

</style>