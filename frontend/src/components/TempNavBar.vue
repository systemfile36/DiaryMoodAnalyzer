<template>
    <nav id="topbar" class="navbar bg-body-tertiary">
        <div class="container-fluid">
            <button id="sidebar-toggler" class="navbar-toggler" type="button"
                @click="sidebarStore.toggleSideBar();">
                <span class="navbar-toggler-icon"></span>
            </button>
            <a id="topbar-title" class="navbar-brand" href="#">DiaryMoodAnalyzer</a>
            <!--우상단 기능 래퍼-->
            <div id="features-wrapper" class="d-flex align-items-center">
                <div id="notification-menu" ref="notiMenuRef" class="nav-item dropdown">
                    <button class="btn btn-link nav-link dropdown-toggle d-flex align-items-center"
                    type="button" data-bs-toggle="dropdown" data-bs-display="static"
                    :aria-expanded="isNotiMenuExpanded.toString()"
                    @click="toggleNotifiMenu">
                        <div class="notify-dot"
                        :class="notificationStore.notifications.length > 0 ? 'd-block' : 'd-none'"></div>
                        <i class="fa-solid fa-bell"></i>
                    </button>
                    <div class="dropdown-menu dropdown-menu-end"
                    :class="isNotiMenuExpanded ? 'show' : ''">
                        <div class="notification-header 
                        d-flex justify-content-between align-items-center px-3">
                            <span class="">알림</span>
                            <button class="" type="button">
                                <i class="fa-solid fa-gear"></i>
                            </button>
                        </div>
                        <div class="dropdown-divider"></div>
                        <ul class="notification-wrapper">
                            <!-- v-for로 반복 -->
                            <li class="notification-item dropdown-item d-flex flex-column text-break"
                                v-for="(notification, i) in notifications" :key="i">
                                <router-link to="/" class="text-clip-2 content">
                                        {{ notification.content }}
                                </router-link>
                                <span class="time-ago">{{ notificationStore.formatRelativeTime(notification.createdAt) }}</span>
                            </li>
                        </ul>
                        <div class="dropdown-divider"></div>
                        <div class="notification-footer d-block text-center">
                            <router-link to="/">
                                전체 보기
                            </router-link>
                        </div>
                    </div>
                </div>
                <div id="darkmode-menu" ref="darkmodeMenuRef" class="nav-item dropdown">
                    <button 
                    class="btn btn-link nav-link dropdown-toggle
                    d-flex align-items-center" type="button"
                    data-bs-toggle="dropdown" data-bs-display="static" 
                    :aria-expanded="isDarkmodeMenuExpanded.toString()"
                    @click="toggleDarkmodeMenu">
                        <i :class="themeStore.isDarkmode ? 'fa-solid fa-moon' : 'fa-regular fa-sun'"></i>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end" 
                    :class="isDarkmodeMenuExpanded ? 'show' : ''">
                        <li>
                            <button class="dropdown-item d-flex align-items-center" 
                            :class="themeStore.isDarkmode ? '' : 'active'"
                            type="button" value="false"
                            @click="setDarkMode($event)">
                                <i class="fa-regular fa-sun"></i>
                                Light
                                <div class="check-wrapper d-flex justify-content-end flex-grow-1">
                                    <i class="fa-solid fa-check" v-if="!themeStore.isDarkmode"></i>
                                </div>
                            </button>
                        </li>
                        <li>
                            <button class="dropdown-item d-flex align-items-center" 
                            :class="themeStore.isDarkmode ? 'active' : ''"
                            type="button" value="true"
                            @click="setDarkMode($event)">
                            <i class="fa-solid fa-moon"></i>
                            Dark
                            <div class="check-wrapper d-flex justify-content-end flex-grow-1">
                                <i class="fa-solid fa-check" v-if="themeStore.isDarkmode"></i>
                            </div>
                            </button>
                        </li>
                    </ul>
                </div>
            </div>
            
        </div>
    </nav>
</template>
<script setup>
import { useSideBarStore } from '@/stores/SideBarManager';
import { useThemeStore } from '@/stores/ThemeManager';

import { useNotificationManagerStore } from '@/stores/NotificationManager';

import { ref, onMounted, onBeforeUnmount } from 'vue';
import { storeToRefs } from 'pinia';

//SideBar 관리를 위함
const sidebarStore = useSideBarStore();

//For manage Theme
const themeStore = useThemeStore();

//For manage notifications
const notificationStore = useNotificationManagerStore();

//반응형을 유지하기 위해 destructuring 해서 받아옴 
const { notifications } = storeToRefs(notificationStore)

//darkmode-menu template ref
const darkmodeMenuRef = ref(null);

//notification-menu template ref
const notiMenuRef = ref(null);

//다크 모드 메뉴 확장 여부 
const isDarkmodeMenuExpanded = ref(false);

//알림 메뉴 확장 여부
const isNotiMenuExpanded = ref(false);

function toggleDarkmodeMenu() {
    isDarkmodeMenuExpanded.value = !isDarkmodeMenuExpanded.value;
}

function toggleNotifiMenu() {
    isNotiMenuExpanded.value = !isNotiMenuExpanded.value;
}

//선택 후, 드롭다운을 닫는다.
function setDarkMode(event) {
    themeStore.setDarkmode(event.target.value);
    toggleDarkmodeMenu();
}

//클릭된 지점이 드롭다운 메뉴가 아닐 경우, 모든 드롭다운을 닫는다.
function onDocumentClick(event) {
    if(darkmodeMenuRef.value && !darkmodeMenuRef.value.contains(event.target)) {
        isDarkmodeMenuExpanded.value = false
    }

    if(notiMenuRef.value && !notiMenuRef.value.contains(event.target)) {
        isNotiMenuExpanded.value = false;
    }
}

//마운트 시점에 따라 이벤트 추가/삭제
onMounted(() => {
    document.addEventListener('click', onDocumentClick);

    //Load notifications 
    notificationStore.loadNotifications();
    notificationStore.sortNotifications()
})

onBeforeUnmount(()=>{
    document.removeEventListener('click', onDocumentClick);
})

</script>
<style lang="scss">
@import '../style.scss';

#sidebar-toggler {
    border: none;
}

//상단바를 뷰포트 상단에 고정하기 위함
#topbar {
    position: sticky;
    top: 0;
}

#darkmode-menu {
    button[data-bs-toggle] {
        font-size: 1.2rem;
    }

    //align dropdown menu to right side
    .dropdown-menu-end {
        right: 0;
        left: auto;
    }

    //give margin to dropdown items
    .dropdown-menu li {
        margin-left: 0.5rem;
        margin-right: 0.5rem;
        
        &:not(:first-child) {
            margin-top: 0.2rem;
        }
    }

    //active color
    .dropdown-item {
        border-radius: var(--bs-dropdown-border-radius);

        &.active, &:active {
            background-color: var(--dropdown-active-bg);
        }
        
        i {
            margin-right: 0.5rem;
        }
    }
}

//알림 메뉴
#notification-menu {

    //알림 표시용 우상단 붉은 점
    .notify-dot {
        background-color: red;
        //부모에 대해 절대 위치 
        position: absolute;
        width: 9px;
        height: 9px;
        border-radius: 50%;
        margin-right: 1rem;
        margin-top: 0.4rem;
        top: 0;
        right: 0;
    }

    .dropdown-menu-end {
        right: 0;
        left: auto;
    }

    //드롭다운 메뉴 너비 설정
    .dropdown-menu {
        width: 50vw;
        min-width: 200px;
        max-width: 480px;
    }

    //알림 리스트 래퍼
    .notification-wrapper {
        //y축 방향으로 늘 경우 스크롤 
        overflow-y: auto;
        
        padding-left: 0;

        min-height: 30vh;
    }

    //알림 요소 
    .notification-item {
        //margin-top: 0.25rem;

        //.dropdown-item의 white-space: nowrap; 을 덮어쓴다.
        white-space: normal;

        //알림 본문
        .content {
            color: inherit;
            text-decoration: none;
            word-break: break-all;
        }

        .time-ago {
            color: var(--bs-secondary-color);
            font-size: 0.9rem;
        }

    }

    .notification-header {

        //Setting 버튼 커스터마이징
        button {
            background: transparent;
            border: none;

            color: var(--bs-secondary-color);

            &:hover {
                background: var(--bs-dropdown-link-hover-bg);
                border-radius: 0.5rem;
            }
        }
    }

    .notification-footer {
        a {
            color: inherit;
            text-decoration: none;
        }
    }
}

//두 줄을 넘는 콘텐츠 텍스트는 ...으로 truncated 한다.
//webkit을 사용한다. 
.text-clip-2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-clamp: 2;
    overflow: hidden;
    text-overflow: ellipsis;
}

</style>