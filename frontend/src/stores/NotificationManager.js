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

    const DATETIME_FORMAT = "YYYY-MM-DD HH:mm:ss";

    //Refresh notifications interval, unit is 'ms'
    const REFRESH_INTERVAL = 60000;

    //Interval id of refresh notification
    let refreshIntervalId = null;

    //`loadNotifications`의 최소 호출 간격을 고정하기 위한 변수
    //Variables for throttling `loadNotifications`
    let isThrottled = false;
    const THROTTLE_TIME = 5000;

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
     * 5초에 한번만 호출 가능하다. (Throttling)
     * 
     * Minimum calling interval is 50000ms (Throttling)
     * @param {() => void} onSuccess 성공 시 실행되는 핸들러
     * @param {() => void} onFailure 실패 시 실행되는 핸들러
     *
     */
    async function loadNotifications(
        onSuccess=()=>{console.log("Successfully load notifications")},
        onFailure=()=>{console.log("Fail on load notifications")}
    ) {

        // 아직 최소 호출 간격이 지나지 않았다면, 요청을 보내지 않고 종료
        if (isThrottled) return;

        // Set throttle flag; now running
        isThrottled = true;

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

        //Reset throttle flag at `THROTTLE_TIME`ms later
        setTimeout(() => {
            isThrottled = false;
        }, THROTTLE_TIME);
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
     * 알림을 일정 주기로 polling하는 Interval을 추가한다. 
     * @param {()=>void} onSuccess onSuccess callback to `loadNotifications`
     */
    function setPollingInterval(onSuccess=()=>{sortNotifications()}){
        if (refreshIntervalId == null) {
            console.log("Notification polling on")
            refreshIntervalId = setInterval(loadNotifications, REFRESH_INTERVAL, onSuccess)
        }
    }

    /**
     * polling interval을 clear한다.
     */
    function clearPollingInterval() {
        if (refreshIntervalId != null) {
            console.log("Notification polling off")
            clearInterval(refreshIntervalId)
            refreshIntervalId = null;
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
     * 인자로 받은 타입에 해당하는 알림들을 새 배열로 반환한다.
     * @param {string} type 알림 타입, Default is NEW_DIARY 
     * @returns {object[]} 타입으로 필터링된 알림 배열 
     */
    function getNotificationByType(type = "NEW_DIARY") {
        return notifications.value.filter((value) => {
            return value['notificationType']['name'] == type
        })
    }

    /**
     * 인자로 받은 타입에 해당하는 알림들을 새 배열로 반환한다.
     * @param {string} level 알림 타입, Default is NEW_DIARY 
     * @returns {object[]} 타입으로 필터링된 알림 배열 
     */
    function getNotificationByLevel(level = "WARNING") {
        return notifications.value.filter((value) => {
            return value['notificationType']['level'] == level;
        })
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
     * 알림에 읽음 표시 
     * 
     * Mark as read to notification in `ids` with batch
     * @param {number[]} ids 
     */
    async function updateAsReads(ids) {
        if(await authManager.checkTokens()) {
            //Spread `ids` because `ids` is Proxy(Array) (Vue 3 ref's value)
            await axiosInstance.patch(`/read`, [...ids], {
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
     * Delete notification by `ids`
     * @param {number[]} ids 
     */
    async function deleteNotifications(ids) {
        if(await authManager.checkTokens()) {
            await axiosInstance.delete('', {
                //Spread `ids` because `ids` is Proxy(Array) (Vue 3 ref's value)
                data: [...ids],
                headers: authManager.getDefaultHeaders()
            }).then(res => {
                console.log(res);
            }).catch(err => {
                console.log(ids);
                console.log(err);
            })
        } else {
            console.log("Fail on deleteNotifications");
        }
    }

    /**
     * Delete all notification
     */
    async function deleteAllNotifications() {
        if(await authManager.checkTokens()) {
            await axiosInstance.delete('', {
                //Spread `ids` because `ids` is Proxy(Array) (Vue 3 ref's value)
                data: [...notifications.value.map((value) => value['id'])], 
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
        getNotificationByType,
        getNotificationByLevel,
        sortNotifications,
        formatRelativeTime,
        setPollingInterval,
        clearPollingInterval,
        applyDateFormat,
        updateAsRead,
        updateAsReads,
        updateAllAsRead,
        deleteNotification,
        deleteAllNotifications,
        deleteNotifications,
        formatObj,
        format,
    }
})