import { defineStore } from 'pinia'
import { ref, computed, isRef } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import TokenUtils from '../utils/TokenUtils'
import { useAuthManagerStore } from './AuthManager'

export const useDiaryManagerStore = defineStore('diaryManager', ()=>{
    const diariesUrl = '/api/diaries';
    const diaryUrl = '/api/diary';
    const diariesTitleUrl = '/api/diaries/title';


    const currentPage = ref(0);
    const currentPageSize = ref(10);
    const isAscending = ref(false);
    
    //나중에 열거형 상수로 정의할 예정 
    const sortBy = ref('createdAt');

    const diariesTitle = ref([]);
    const diaries = ref([]);

    const router = useRouter();

    const authManager = useAuthManagerStore();

    async function addDiary(title, content) {
        const diary = {
            title: title,
            content: content
        };

        const headers = authManager.getDefaultHeaders();

        await axios.post(diariesUrl, diary, {headers})
            .then((res)=>{
                console.log(res);
                alert('일기 작성에 성공하였습니다.');
                router.go(0);
            }).catch((error)=>{
                console.log(error);
            })
    }

    async function getDiariesTitle() {
        await axios.get(diariesTitleUrl, {
            headers: authManager.getDefaultHeaders(),
            params: getPagingParams(),
        }).then((res) => {
            console.log(res);

            //store 변수에 설정 
            diariesTitle.value = res.data.content;

            console.log(diariesTitle.value);
        }).catch((error) => {
            console.log(error);
        })
    }

    async function getDiaries() {
        await axios.get(diariesUrl, {
            headers: authManager.getDefaultHeaders(),
            params: getPagingParams(),
        }).then((res) => {
            console.log(res);

            diaries.value = res.data.content;

            console.log(diaries.value);
        }).catch((error) => {
            console.log(error);
        })
    }


    function getPagingParams() {
        return {
            page: currentPage.value,
            size: currentPageSize.value,
            sortBy: sortBy.value,
            isAscending: isAscending.value,
        };
    }

    return {
        currentPage,
        currentPageSize,
        isAscending,
        sortBy,
        diariesTitle,
        diaries,
        addDiary,
        getDiariesTitle,
        getDiaries,
    }
})