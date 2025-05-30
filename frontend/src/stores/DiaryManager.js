import { defineStore } from 'pinia'
import { ref, computed, isRef } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import dayjs from 'dayjs';

import TokenUtils from '../utils/TokenUtils'
import DiaryColumns from '../utils/DiaryColumns';

import { useAuthManagerStore } from './AuthManager'


/**
 * 다이어리와 관련된 상태와 함수들이 정의된 Pinia 저장소 
 */
export const useDiaryManagerStore = defineStore('diaryManager', ()=>{
    const diariesUrl = '/api/diaries';
    const diaryUrl = '/api/diary';

    const diariesForExpertUrl = '/api/expert/diaries';

    const diariesStatisticsUrl = '/api/diaries/statistics';

    /**
     * 다이어리의 기본 날짜 포맷 형식 
     */
    const diaryDateFormat = 'YYYY-MM-DD HH:mm:ss';

    //페이징 관련 변수
    const currentPage = ref(0);
    const currentPageSize = ref(10);
    const currentTotalPages = ref(0);
    const isAscending = ref(false);

    const maxTitleLength = ref(50);
    const maxContentLength = ref(500);
    
    /** @type {Ref(string)} */
    const sortBy = ref(DiaryColumns.CREATED);

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
    async function addDiary(title, content, 
        onSuccess = () => { alert('일기 작성에 성공하였습니다.'); router.push("/diaries"); },
        onFailure = () => { alert('요청에 실패하였습니다. 잠시후 시도해 주세요.');}
    ) {
        if(await authManager.checkTokens()) {
            const diary = {
                title: title,
                content: content
            };
    
            const headers = authManager.getDefaultHeaders();
    
            await axios.post(diariesUrl, diary, {headers})
                .then((res)=>{
                    console.log(res);
                    //alert('일기 작성에 성공하였습니다.');
                    //router.go(0);
                    onSuccess();

                    return;

                }).catch((error)=>{
                    console.log(error);
                    onFailure();
                })
        } else {
            onFailure();
        }
    }

    /**
     * id에 해당하는 다이어리를 삭제한 후, 리로드 한다. 
     * @param {number | string} id - 삭제할 다이어리의 id
     */
    async function deleteDiary(id) {

        //토큰 유효성 체크 
        if(await authManager.checkTokens()) {

            const headers = authManager.getDefaultHeaders();

            //삭제 요청 보냄 
            await axios.delete(`${diaryUrl}/${id}`, {headers}).catch((err)=>{
                console.log(err);
            })

            //다이어리 리로드 
            await loadDiaries();
        } else {
            console.log("토큰 획득에 실패하였습니다.");
        }

    }

    /**
     * diaries 변수에 현재 인증된 유저의 다이어리를 로드한다. 
     * 로드한 다이어리의 정렬과 순서는 페이징 변수 설정에 따른다.
     */
    async function loadDiaries() {

        //요청 전에 토큰 유효성 검사/재발급
        if(await authManager.checkTokens()) {
            //토큰이 유효할 시 요청
            await axios.get(diariesUrl, {
                headers: authManager.getDefaultHeaders(),
                params: getPagingParams(),
            }).then((res) => {
                console.log(res);
    
                diaries.value = res.data.content;

                //전체 페이지 수 저장
                currentTotalPages.value = res.data.totalPages;

                //console.log(diaries.value);
            }).catch((error) => {
                console.log(error);
            })
        } else {
            console.log("토큰 획득에 실패하였습니다.");
        }
        
    }

    /**
     * 인자로 받은 이메일로 사용자의 다이어리 목록을 로드한다. 
     * 전문가가 자신이 관리하는 사용자의 다이어리 목록을 볼 때 사용한다. 
     * @param {string} email 조회할 다이어리의 소유자의 이메일
     */
    async function loadDiariesForExpert(email) {
        if(await authManager.checkTokens()) {

            const parameters = getPagingParams();
            parameters['ownerEmail'] = email;

            await axios.get(diariesForExpertUrl, {
                headers: authManager.getDefaultHeaders(),
                params: parameters,
            }).then((res) => {
                console.log(res);

                diaries.value = res.data.content;

                currentTotalPages.value = res.data.totalPages;
            }).catch((error) => {
                console.log(error);
            })
        } else {
            console.log("토큰 획득에 실패하였습니다.");
        }
    }

   /**
    * start 에서 end 까지 작성한 Diary 들의 일별 depression level 평균을 
    * 객체로 반환한다.
    * 
    * 주의! 정렬되어 있지 않음
    * @param {string} start - 시작일 YYYY-MM-DD
    * @param {string} end  - 종료일 YYYY-MM-DD
    * @param {string} target - 조회할 대상의 이메일. 대상이 본인일 경우 생략한다. @default undefined
    * @returns {Promise<object>}- 날짜를 key, 평균 depression level을 value로 가지는 객체
    */
    async function getDailyAvgDepressionLevel(start, end, target=undefined) {

        const temp = "/average/depressionLevel/daily";

        let url = diariesStatisticsUrl + temp;

        //GET 요청으로 넘길 파라미터 
        const params = {
            start: start,
            end: end
        }

        //대상이 지정되었을 경우, URL을 변경하고 파라미터에 조회할 대상의 이메일을 추가한다.
        if(target) {
            url = "/api/expert/diaries/statistics" + temp;
            params['email'] = target;
        }

        if(await authManager.checkTokens()){
            return await axios.get(url, {
                headers: authManager.getDefaultHeaders(),
                params: params
            }).then((res) => {
                return res.data.dailyAvg;
            }).catch((error)=>{
                console.log(error);
                return undefined;
            })
        } else {
            console.log("토큰 획득에 실패하였습니다.");
        }

        return undefined;
    }

    /**
     * 최근 n일간에 작성한 Diary의 일별 평균 depression level을 객체로 반환.
     * 
     * (현재 - days) 에서 현재까지의 범위를 가진다.
     * 
     * 주의! 정렬되어 있지 않음
     * @param {number} days amount of time ('days')
     * @param {string} target - 조회할 대상의 이메일. 대상이 본인일 경우 생략한다. @default undefined
     * @returns {Promise<object>} - 날짜를 key, 평균 depression level을 value로 가지는 객체
     */
    async function getRecentDailyAvgDepressionLevel(days, target=undefined) {
        const format = "YYYY-MM-DD";
        const today = dayjs(new Date());
        return getDailyAvgDepressionLevel(
            today.add(-days, 'day').format(format), 
            today.format(format), target);
    }

    /**
     * 인자로 받은 page의 Diary를 불러옴
     * @param {number} page - 페이지 번호. 0-indexed.
     */
    async function setDiaryPage(page) {
        currentPage.value = Math.min(page, currentPageSize.value);
        await loadDiaries();
    }

    /**
     * 현재 페이지가 첫번째 페이지인지 여부를 반환
     * @returns {boolean} - if current page is first, return true
     */
    function isFirstPage() {
        return currentPage.value === 0;
    }

    /**
     * 현재 페이지가 마지막 페이지인지 여부를 반환
     * @returns {boolean} - if current page is last, return true
     */
    function isLastPage() {
        return currentPage.value >= (currentTotalPages.value - 1);
    }

    /**
     * id를 받아서 해당 id와 일치하는 다이어리 객체를 반환함
     * 상세 보기에서 사용할 예정
     * 
     * If there is no diary in `diaries`, then fetch it from server.
     * @param {number} id - 찾을 다이어리의 id
     * @returns id가 일치하는 다이어리 객체. 찾지 못하면 null
     */
    async function getDiaryById(id) {
        //Search `diaries` first
        const result = diaries.value.find((value)=>{
            return value['id'] == id;
        });

        //If there is no diary with `id` in `diaries`, 
        //Fetch from server 
        if(!result) { 

            return await axios.get(`${diaryUrl}/${id}`, {
                headers: authManager.getDefaultHeaders()
            }).then((res) => {
                return res.data
            }).catch((error)=> {
                console.log(error);
                return undefined;
            })
        } else {
            return result;
        }
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
     * 페이징에 필요한 파라미터들을 설정한다. 
     * 객체 destructuring을 사용해
     * @param {{page?: number, 
     * size?: number,
     * sort?: string,
     * ascending?: boolean}} param0 
     */
    function setPagingParams({
        page = currentPage.value,
        size = currentPageSize.value,
        sort = sortBy.value,
        ascending = isAscending.value
    } = {}) {
        currentPage.value = page;
        currentPageSize.value = size;
        sortBy.value = sort;
        isAscending.value = ascending;
    }

    /**
     * 날짜를 store에 저장된 포맷으로 변환 후 반환
     * @param {string} date - datetime 문자열
     * @returns formatted datetime string
     */
    function formatDate(date, format=diaryDateFormat) {
        //day.js 사용 
        return dayjs(date).format(format); // 날짜 포맷 설정
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
        currentTotalPages,
        isAscending,
        sortBy,
        diaries,
        maxTitleLength,
        maxContentLength,
        addDiary,
        deleteDiary,
        loadDiaries,
        loadDiariesForExpert,
        getDailyAvgDepressionLevel,
        getRecentDailyAvgDepressionLevel,
        formatDate,
        getTruncated,
        getDiaryById,
        setDiaryPage,
        isFirstPage,
        isLastPage,
    }
})