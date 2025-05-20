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

    //create instance with baseURL and timeout
    const axiosInstance = axios.create({
        baseURL: BASE_URL,
        timeout: 3000
    });

    //알림들이 저장되는 반응형 변수 
    const notifications = ref([]);

    //현재 인증된 유저의 알림 설정 반응형 변수
    const notificationSettings = ref([]);

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
     * 현재 인증 정보로 알림 설정 로드 
     * @param {() => void} onSuccess 성공 시 실행되는 핸들러
     * @param {() => void} onFailure 실패 시 실행되는 핸들러
     */
    async function loadNotificationSetting(
        onSuccess=()=>{console.log("Successfully load notification settings")},
        onFailure=()=>{console.log("Fail on load notification settings")}
    ){
        if(await authManager.checkTokens()) {
            await axiosInstance.get('/settings', {
                headers: authManager.getDefaultHeaders()
            }).then(res => {
                
                notificationSettings.value = res.data;

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
     * @param {string} column 정렬 기준이 될 컬럼, Default is createdAt
     * @param {boolean} isAsc 오름차순 여부, Default is false
     */
    function sortNotifications(column = 'createdAt', isAsc = false) {
        notifications.value.sort((a, b) => {
            const aVal = a[column];
            const bVal = b[column];

            if (aVal > bVal) return isAsc ? 1 : -1;
            if (aVal < bVal) return isAsc ? -1 : 1;
            return 0;
        });
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
     * 전체 알림에 읽음 표시
     */
    async function updateAllAsRead() {
        if(await authManager.checkTokens()) {
            await axiosInstance.patch('/read/all', {}, {
                headers: authManager.getDefaultHeaders()
            }).then(res => {
                console.log(res);
            }).catch(err => {
                console.log(err);
            })
        } else {
            console.log("Fail on updateAllAsRead");
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

    /**
     * 인자로 받은 날짜값에 포맷 적용
     * @param {*} date 
     * @returns 
     */
    function applyDateFormat(date) {
        return dayjs(date).format(DATETIME_FORMAT);
    }

    /**
     * 인자로 받은 날짜가 현재로부터 얼마나 떨어져 있는지 계산하여
     * "n분 전", "n시간 전", "n일 전" 형식의 문자열을 반환한다.
     * @param {string | Date | dayjs.Dayjs} createdAt 날짜
     * @returns {string} 포맷된 문자열
     */
    function formatRelativeTime(createdAt) {
        const now = dayjs();
        const time = dayjs(createdAt);
        const diffMinutes = now.diff(time, 'minute');

        if (diffMinutes < 60) {
            return `${diffMinutes}분 전`;
        }

        const diffHours = now.diff(time, 'hour');
        if (diffHours < 24) {
            return `${diffHours}시간 전`;
        }

        const diffDays = now.diff(time, 'day');
        return `${diffDays}일 전`;
    }

    function formatObj(str, obj) {
        return str.replace(/{(\w+)}/g, (_, key) => obj[key] ?? '');
    }

    /**
     * 템플릿에 values를 순서대로 적용한다. 
     * @param {str} template 
     * @param {str[]} values 
     * @returns 
     */
    function format(template, values) {
        let i = 0;
        return template.replace(/\{\}/g, () => values[i++] ?? '');
    }

    return {
        notifications,
        notificationSettings,
        loadNotifications,
        loadNotificationSetting,
        sortNotifications,
        formatRelativeTime,
        applyDateFormat,
        updateAsRead,
        updateAllAsRead,
        deleteNotification,
        formatObj,
        format,
    }
})