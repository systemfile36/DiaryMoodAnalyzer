<template>
<div class="notification-wrapper w-100">
    <div class="notification-title-wrapper px-4">
        <h2 class="notification-title mt-4">최근 알림</h2>
    </div>
    <!--알림 카테고리-->
    <nav class="notification-category navbar navbar-expand px-4">
        <ul class="navbar-nav">
            <li class="nav-item active">
                <a class="nav-link">전체</a>
            </li>
            <li class="nav-item">
                <a class="nav-link">새로운 글</a>
            </li>
            <li class="nav-item">
                <a class="nav-link">경고</a>
            </li>
        </ul>
    </nav>
    <div class="notification-list d-flex flex-column justify-content-center">
        <div class="notification-item">
            
        </div>
    </div>
</div>
</template>
<script setup>
import { useNotificationManagerStore } from '../stores/NotificationManager';
</script>
<style lang="scss">
@import '../style.scss';

.notification-wrapper {

    //알림 카테고리 스타일 
    .notification-category {
        .nav-item {
            margin-left: 0.6rem;
            margin-right: 0.6rem;

            //첫번째 요소는 왼쪽 여백 제외
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

}

</style>