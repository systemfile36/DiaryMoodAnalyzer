import { defineStore } from 'pinia'
import { ref, computed, isRef } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import TokenUtils from '../utils/TokenUtils'
import { useAuthManagerStore } from './AuthManager'
import dayjs from 'dayjs';

/**
 * 다이어리와 관련된 상태와 함수들이 정의된 Pinia 저장소 
 */
export const useDiaryManagerStore = defineStore('diaryManager', ()=>{
    const diariesUrl = '/api/diaries';
    const diaryUrl = '/api/diary';
    const diariesTitleUrl = '/api/diaries/title';

    /**
     * 다이어리의 날짜 포맷 형식 
     */
    const diaryDateFormat = 'YYYY-MM-DD HH:mm:ss';

    //페이징 관련 변수
    const currentPage = ref(0);
    const currentPageSize = ref(10);
    const isAscending = ref(false);

    const maxTitleLength = ref(50);
    const maxContentLength = ref(500);
    
    //나중에 열거형 상수로 정의할 예정 
    const sortBy = ref('createdAt');

    /**
     * 다이어리 목록이 저장될 배열
     */
    const diaries = ref([]);

    const router = useRouter();

    const authManager = useAuthManagerStore();

    /**
     * 제목과 내용을 받아서 다이어리를 추가함 
     * @param {string} title 
     * @param {string} content 
     */
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

    /**
     * diaries 변수에 현재 인증된 유저의 다이어리들을 로드한다.
     */
    async function loadDiaries() {
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

    /**
     * 페이징에 필요한 파라미터를 반환한다.
     * @returns 페이징 파라미터
     */
    function getPagingParams() {
        return {
            page: currentPage.value,
            size: currentPageSize.value,
            sortBy: sortBy.value,
            isAscending: isAscending.value,
        };
    }

    /**
     * 날짜를 store에 저장된 포맷으로 변환 후 반환
     * @param {string} date - datetime 문자열
     * @returns formatted datetime string
     */
    function formatDate(date) {
        //day.js 사용 
        return dayjs(date).format(diaryDateFormat); // 날짜 포맷 설정
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

    return {
        currentPage,
        currentPageSize,
        isAscending,
        sortBy,
        diaries,
        maxTitleLength,
        maxContentLength,
        addDiary,
        loadDiaries,
        formatDate,
        getTruncated,
    }
})