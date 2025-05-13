import { defineStore } from 'pinia'
import { ref } from 'vue';
import { useAuthManagerStore } from './AuthManager';
import axios from 'axios';
import dayjs from 'dayjs';

/**
 * 알림을 관리하는 Pinia 저장소
 */
export const useNotificationManagerStore = defineStore('notificationManager', ()=>{
    const BASE_URL = '/api/notifications';

    const DATETIME_FORMAT = "MM-DD HH:mm:ss";

    class NotiMsgFormat {
        static NEW_COMMENT = "{sender_nickname}님의 새 코멘트: {content}"
    }

    //create instance with baseURL and timeout
    const axiosInstance = axios.create({
        baseURL: BASE_URL,
        timeout: 3000
    });

    //알림들이 저장되는 반응형 변수 
    const notifications = ref([]);

    //인증 정보를 관리하기 위한 Pinia 저장소
    const authManager = useAuthManagerStore();

    /**
     * 현재 인증 정보로 알림을 로드 
     * @param {() => void} onSuccess 성공 시 실행되는 핸들러
     * @param {() => void} onFailure 실패 시 실행되는 핸들러
     *
     */
    async function loadNotifications(
        onSuccess=()=>{console.log("Successfully load notifications")},
        onFailure=()=>{console.log("Fail on load notifications")}
    ) {
        if(await authManager.checkTokens()) {
            await axiosInstance.get('', {
                headers: authManager.getDefaultHeaders()
            }).then(res => {
                
                notifications.value = res.data;
                
                console.log(res.data);

                onSuccess();
            }).catch(err => {
                console.log(err);
                onFailure();
            })
        } else {
            onFailure();
        }
    }

    /**
     * 알림을 기준에 따라 정렬한다. 
     * @param {string} column 정렬 기준이 될 컬럼, Default is created_at
     * @param {boolean} isAsc 오름차순 여부, Default is false
     */
    function sortNotifications(column='createdAt', isAsc=false) {

    }

    /**
     * 알림에 읽음 표시
     * @param {number} id 읽음 표시할 id
     */
    async function updateAsRead(id) {
        if(await authManager.checkTokens()) {
            await axiosInstance.patch(`/${id}/read`, {}, {
                headers: authManager.getDefaultHeaders()
            }).then(res => {
                console.log(res);
            }).catch(err => {
                console.log(err);
            })
        } else {
            console.log('Fail on updateAsRead');
        }
    }

    /**
     * Delete notification
     * @param {number} id id of notification that will be deleted
     */
    async function deleteNotification(id) {
        if(await authManager.checkTokens()) {
            await axiosInstance.delete(`/${id}`, {
                headers: authManager.getDefaultHeaders()
            }).then(res => {
                console.log(res);
            }).catch(err => {
                console.log(err);
            })
        } else {
            console.log("Fail on deleteNotifications");
        }
    }

    function formatObj(str, obj) {
        return str.replace(/{(\w+)}/g, (_, key) => obj[key] ?? '');
    }

    return {
        notifications,
        loadNotifications,
        sortNotifications,
        formatObj,
        NotiMsgFormat,
    }
})