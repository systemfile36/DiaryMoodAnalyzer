<template>
    <aside id="sidebar" class="d-flex flex-column align-items-end">
        <header class="sidebar-top">
            <div class="profile">
                <!--권한에 따라 홈 화면 링크 변경-->
                <router-link :to="authManager.role == Authority.EXPERT ? '/expertPage' : '/'" 
                class="image-wrapper">
                    <img src="../assets/images/default_profile_image.png" alt="profile-image">
                </router-link>
                <div class="user-info">
                    <span class="user-name">Default</span>
                    <span class="user-email">{{ authManager.userName }}</span>
                </div>
            </div>
        </header>
        <nav class="flex-column flex-grow-1 w-100 ps-0">
            <ul class="nav">
                <li class="nav-item">
                    <!--권한에 따라 홈 화면 링크 변경-->
                    <router-link :to="authManager.role == Authority.EXPERT ? '/expertPage' : '/'" class="nav-link">
                        <i class="fas fa-home"></i>
                        <span>HOME</span>
                    </router-link>
                </li>
                <li class="nav-item">
                    <router-link to="/diary" class="nav-link">
                        <i class="fas fa-pen"></i>
                        <span>Write Diary</span>
                    </router-link>
                </li>
                <li class="nav-item">
                    <router-link to="/diaries" class="nav-link">
                        <i class="fa-solid fa-book-open"></i>
                        <span>Diaries</span>
                    </router-link>
                </li>
                <li class="nav-item">
                    <router-link to="/notifications" class="nav-link">
                        <i class="fa-solid fa-bell"></i>
                        <span>Notifications</span>
                    </router-link>
                </li>
            </ul>
        </nav>
        <div class="sidebar-bottom d-flex flex-column flex-wrap align-item-center w-100 border-top">
            <div class="nav-item disabled">
                <a class="nav-link">
                    <i class="fa-solid fa-gear"></i>
                    <span>Setting</span>
                </a>
            </div>
            <div class="nav-item">
                <a class="nav-link" @click="logoutHandler">
                    <i class="fa-solid fa-arrow-right-from-bracket"></i>
                    <span>Logout</span>
                </a>
            </div>
        </div>
    </aside>
</template>
<script setup>
import { useAuthManagerStore } from '@/stores/AuthManager';
import { onMounted } from 'vue';
import Authority from '@/utils/Authority';

const authManager = useAuthManagerStore();

onMounted(()=>{
    console.log(authManager.role);
})

function logoutHandler() {
    authManager.logout();
}

</script>
<style lang="scss">

@import '../style.scss';

#sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100%;
    overflow-y: auto;

    width: $sidebar-width;
    z-index: 90;

    //background-color: var(--bs-tertiary-bg);
    background-color: var(--sidebar-background-color);
    border-right: var(--bs-border-color);
    scrollbar-width: none;

    //rem은 루트(html) 요소의 폰트 사이즈에 비례한다.

    //사이드바 상단 스타일
    .sidebar-top {
        margin-top: 2.5rem;
        margin-bottom: 2.5rem;
        padding-left: 1.5rem;
        padding-right: 1.25rem;
        width: 100%;

        //프로필 스타일
        .profile {
            display: flex;
            align-items: center;
            flex-direction: row;

            background-color: var(--bs-body-bg);

            padding: 0.5rem;
            border-radius: 1rem;

            //이미지 둥글게 만들기 
            .image-wrapper {
                display: block;
                width: 4rem;
                height: 4rem;
                overflow: hidden;
                border-radius: .7rem;

                margin-right: 1.0rem;

                //이미지 크기를 부모에 맞춤
                img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    display: block;
                }
            }   

            .user-info {

                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: start;

                color: var(	--bs-light-text-emphasis);

                .user-name {
                    width: 100%;
                    font-weight: bold;
                }

                .user-email {
                    margin-top: 0.8rem;
                    width: 100%;

                    //텍스트 초과 설정
                    overflow: hidden;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                }
            }


           
        }




    }

    //사이드바 메뉴
    nav {
        .nav-item {
            width: 100%;
            padding-left: 1.5rem;
            padding-right: 1.5rem;

            //첫번째 요소가 아닐 경우 상단 여백
            &:not(:first-child) {
                margin-top: .6rem;
            }
        }

        .nav-link {
            padding: .6rem;
            display: flex;
            align-items: center;

            border-radius: 0.75rem;

            //텍스트 컬러 조정 
            color: var(--bs-emphasis-color);

            //아이콘 위치 미세 조정 
            i {
                margin-right: 1.5rem;
            }

            //현재 URL과 일치하는 링크의 배경을 강조
            &.router-link-active {
                background-color: var(--bs-secondary-bg);
            }

                        //마우스 호버 시 컬러 파랗게
            &:hover {
                color: var(--bs-primary);
                background-color: var(--bs-secondary-bg);
            }

        }
    }

    //사이드바 하단
    .sidebar-bottom {
        .nav-item {
            
            margin-bottom: 0.8rem;
            width: 100%;
            padding-left: 1.5rem;
            padding-right: 1.5rem;

            //첫번째 요소일 경우 상단에 여백
            &:is(:first-child) {
                margin-top: 0.8rem;
            }

            .nav-link {
                padding: .6rem;
                display: flex;
                align-items: center;

                border-radius: 0.75rem;

                cursor: pointer;

                //텍스트 컬러 조정 
                color: var(--bs-emphasis-color);

                //아이콘 위치 미세 조정 
                i {
                    margin-right: 1.5rem;
                }

                &:hover {
                    background-color: var(--bs-secondary-bg);
                }
            }

            //비활성화일 경우 색 변경 
            &.disabled * {
                color: var(--bs-secondary-color);
            }
        }

    }
}
</style>