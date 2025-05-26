<template>
<div class="notification-wrapper w-100">
    <div class="notification-title-wrapper px-4">
        <h2 class="notification-title mt-4">최근 알림</h2>
    </div>
    <!--알림 카테고리-->
    <nav class="notification-category navbar navbar-expand px-4">
        <!--카테코리를 선택하면 `localNotifications`를 필터링한 후 동기화한다.-->
        <ul class="navbar-nav">
            <li class="nav-item" :class="currentType == 'ALL' ? 'active' : ''" type="ALL" @click="syncNotificationsToLocal('ALL')">
                <a class="nav-link">전체</a>
            </li>
            <li class="nav-item" :class="currentType == 'NEW_DIARY' ? 'active' : ''" type="NEW_DIARY" @click="syncNotificationsToLocal('NEW_DIARY')">
                <a class="nav-link">새로운 Diary</a>
            </li>
            <li class="nav-item" :class="currentType == 'NEW_COMMENT' ? 'active' : ''" type="NEW_COMMENT" @click="syncNotificationsToLocal('NEW_COMMENT')">
                <a class="nav-link">새로운 코멘트</a>
            </li>
            <li class="nav-item" :class="currentType == 'HIGH' ? 'active' : ''" level="HIGH" @click="syncNotificationsToLocal('HIGH', false)">
                <a class="nav-link">중요</a>
            </li>
            <li class="nav-item" :class="currentType == 'WARNING' ? 'active' : ''" level="WARNING" @click="syncNotificationsToLocal('WARNING', false)">
                <a class="nav-link">경고</a>
            </li>
        </ul>
    </nav>
    <!--Notification modify buttons-->
    <div class="notification-buttons d-flex flex-row">
        <!--Hide when there is no checked notification-->
        <div :class="isChecked ? '' : 'd-none'">
            <button type="button" class="btn btn-outline-primary"
                @click="onClickRead">
                읽음
            </button>
            <button type="button" class="btn btn-outline-danger"
                @click="onClickDelete">
                삭제
            </button>
        </div>
        <button type="button" class="btn btn-outline-primary"
            @click="notificationStore.updateAllAsRead">
            전체 읽음
        </button>
        <button type="button" class="btn btn-outline-danger"
            @click="notificationStore.deleteAllNotifications">
            전체 삭제
        </button>
    </div>
    <div class="notification-list d-flex flex-column justify-content-center">
        <!--Reference `localNotifications` for filter and categorize notifications-->
        <div class="notification-item-wrapper"  v-for="(notification, i) in localNotifications" :key="i">
            
            <!--//Use `v-model` binding. See following link https://vuejs.org/guide/essentials/forms.html#checkbox-->
            <input class="notification-check" 
                type="checkbox" :value="notification.id" 
                v-model="checkedNotificationIds" @change="onChangeCheckbox"/>
            <!--Check `refLink` is falsey. when falsey, set `to` to empty string (will be replaced to '#')-->
            <router-link class="notification-item" :class="notification.read ? 'disabled' : ''"
            :to="notification.refLink ? notification.refLink : ''"
            :type="notification.notificationType.name">
                <h4 class="notification-content">{{ notification.content }}</h4>
                <p class="notification-sender">{{ notification.senderEmail }}</p>
                <div class="notification-time">
                    <time class="createdAt">{{ notificationStore.applyDateFormat(notification.createdAt) }}</time>
                    <span class="time-ago">{{ notificationStore.formatRelativeTime(notification.createdAt) }}</span>
                </div>
            </router-link>
        </div>
    </div>
</div>
</template>
<script setup>
import { onMounted, ref, watch } from 'vue';
import { useNotificationManagerStore } from '../stores/NotificationManager';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';

//For manage notifications
const notificationStore = useNotificationManagerStore();

//반응형을 유지하기 위해 destructuring 해서 받아옴 
// Get reactive variable from Pinia store
const { notifications } = storeToRefs(notificationStore)

//Local notifications. use for filtering or categorizing
const localNotifications = ref(null);

//Current selected category; Default is "ALL"
const currentType = ref("ALL");

//Array contains the id from currently selected notification
//Use `v-model` binding. See following link https://vuejs.org/guide/essentials/forms.html#checkbox
const checkedNotificationIds = ref([]);

//If `checkedNotificationIds.length > 0` true else false
const isChecked = ref(false);

//Get current router instance
const router = useRouter();

//https://vuejs.org/api/reactivity-core.html#watch
// watch `notifications` reactive for sync with `localNotifications`
//UI 표시용과, Pinia 저장소의 알림 정보를 동기화한다. 
watch(notifications, async () => {
    await syncNotificationsToLocal(currentType.value);
})

onMounted(async ()=>{

    //Load notifications (await)
    //Sort when `loadNotifications` done successfuly (by callback)
    await notificationStore.loadNotifications(
        () => notificationStore.sortNotifications()
    );

    await syncNotificationsToLocal(currentType.value);

    //Turn on notification polling
    notificationStore.setPollingInterval();
})

/**
 * 현재 필터링을 기준으로 컴포넌트 로컬과 Pinia 반응형 변수 동기화
 * 
 * Sync `localNotifications` and `notifications` from `NotificationManager.js`
 * @param {string} category_name `notificationType.name` or `notificationType.level`
 * @param {bool} isType If this flag is true, use `category_name` as `notificationType.name`. Default is true
 */
async function syncNotificationsToLocal(category_name = "ALL", isType = true) {

    //If notification is empty, return
    if(notifications.value.length <= 0) return;

    //Load and sort notifications
    await notificationStore.loadNotifications(
        () => notificationStore.sortNotifications()
    );

    //If category is "ALL", then copy entire notifications to local
    //else, filter by category name(filter)
    if(category_name == "ALL") {
        //Copy notifications to local array (Deep copy of array, but elements is shallow)
        localNotifications.value = [...notifications.value];

        //Set current selected category on local reactive
        currentType.value = category_name;

    } else {
        //when `isType` flag is true, return `getNotificationByType` else `getNotificationByLevel`
        localNotifications.value = 
            isType ? notificationStore.getNotificationByType(category_name)
                 : notificationStore.getNotificationByLevel(category_name);

        //Set current selected category on local reactive
        currentType.value = category_name;
    }
}

/**
 * Set `isChecked`
 */
function onChangeCheckbox() {
    isChecked.value = checkedNotificationIds.value.length > 0;
}

async function onClickRead() {
    await notificationStore.updateAsReads(checkedNotificationIds.value);
    //Reload page
    router.go(0);
}

async function onClickDelete() {
    await notificationStore.deleteNotifications(checkedNotificationIds.value);
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
<style lang="scss">
@import '../style.scss';
// root wrapper of this component
.notification-wrapper {

    //알림 카테고리 스타일 
    .notification-category {
        .nav-item {
            margin-left: 0.6rem;
            margin-right: 0.6rem;

            //첫번째 요소는 왼쪽 여백 제외
            //remove left margin on first child of categories element
            &:first-child {
                margin-left: 0;
            }

            //Underline when active
            &.active {
                border-bottom: 2px solid var(--bs-primary);
            }

            //알림 카테고리 버튼 설정 
            .nav-link {
                //테두리 둥글게, 호버 시 강조 표시
                border-radius: 0.6rem;
                &:hover {
                    background-color: var(--bs-secondary-bg);
                }

                cursor: pointer;
            }
        }
    }

    //알림 조작 버튼 모음 
    .notification-buttons {

        margin-left: 1.8rem;

        button {
            width: 6.0rem;
        }
    }

    //알림 아이템들을 감싸는 wrapper
    .notification-item-wrapper {
        //horizontal flex box
        display: flex;
        flex-direction: row;

        align-items: start;

        padding: 1.8rem;
        border-radius: 0.6rem;
        
        //최대 크기를 제한
        max-width: 600px;
        
        * {
            //링크의 밑줄 제거 
            //remove underline and change color to default from <router-link>
            text-decoration: none;
            color: inherit;
        }

        //호버 강조
        &:hover {
            background-color: var(--bs-dark-bg-subtle);
            cursor: pointer;
        }
    }

    // Checkbox on select mode 
    .notification-check {
        margin-right: 1.0rem;
    }

    .notification-item {
        //vertical flex box
        display: flex;
        flex-direction: column;

        flex-grow: 1;

        //diable when `read` is true
        &.disabled {
            color: var(--bs-secondary-color);
        }

        * {
            margin-bottom: 0.5rem;

            //Fill parent element (`.notification-item-wrapper`)
            width: 100%;
        }

        //시간 관련 스타일 
        .notification-time {
            //horizontal flex box
            display: flex;
            flex-direction: row;
            justify-content: start;

            font-size: 0.9rem;
        
            * {
                //Like 'wrap_content'
                display: inline-block;
                width: auto;

                //마지막이 아닐 경우 왼쪽에 여백 
                &:not(:first-child) {
                    margin-left: 1.0rem;
                }
            }
        }

    }

}

</style>