import { defineStore } from 'pinia'
import { ref, computed, isRef } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

import { useAuthManagerStore } from './AuthManager'

export const useCommentManagerStore = defineStore("commentManager", () => {
    const commentsUrl = "/api/diary/{id}/comments"

    const maxCommentLength = ref(500);

    /**
     * 코멘트 목록이 저장됨
     */
    const comments = ref([]);

    const router = useRouter();

    const authManager = useAuthManagerStore();

    /**
     * 다이어리 아이디를 받아, 해당 다이어리의 코멘트 목록을 로드한다. 
     * @param {number} diaryId 
     */
    async function loadCommentsByDiaryId(diaryId) {
        if(await authManager.checkTokens()) {

            const url = commentsUrl.replace("{id}", diaryId);

            await axios.get(url, {
                headers: authManager.getDefaultHeaders(),
            }).then((res)=>{
                console.log(res);

                comments.value = res.data;

            }).catch((error)=>{
                console.log(error);
            })
        } else {
            console.log("토큰 획득에 실패하였습니다.");
        }
    }

    async function addCommentByDiaryId(diaryId, content) {
        if(await authManager.checkTokens()) {

            const url = commentsUrl.replace("{id}", diaryId);

            await axios.post(url, { content: content }, {
                headers: authManager.getDefaultHeaders()
            }).then((res)=> {
                console.log(res);

            }).catch((error)=>{
                console.log(error);
            })
        } else {
            console.log("토큰 획득에 실패하였습니다.");
        }
    }

    return {
        maxCommentLength,
        comments,
        loadCommentsByDiaryId,
        addCommentByDiaryId
    }
})