<template>
    <aside id="sidebar" class="d-flex flex-column align-items-end">
        <header class="profile-wrapper">
            <router-link to="/" id="profile-image">
                <img src="../assets/images/default_profile_image.png"
                width="112" height="112" alt="profile-image">
            </router-link>
            <h2 id="userName" class="user-name">{{ authManager.userName }}</h2>
        </header>
        <nav class="flex-column flex-grow-1 w-100 ps-0">
            <ul class="nav">
                <li class="nav-item">
                    <router-link to="/" class="nav-link">
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
                  <router-link to="/writtenDiary" class="nav-link">
                    <i class="fas fa-book"></i>
                    <span>Written Diary</span>
                  </router-link>
                </li>
                <li class="nav-item">
                    <router-link to="/diaries" class="nav-link">
                        <i class="fa-solid fa-book-open"></i>
                        <span>Diaries</span>
                    </router-link>
                </li>
            </ul>
        </nav>
        <div class="d-flex flex-wrap align-item-center w-100">
            <button type="button" class="btn btn-primary"
                @click="logoutHandler">
                logout
            </button>
        </div>
    </aside>
</template>
<script setup>
import { useAuthManagerStore } from '../stores/AuthManager';

const authManager = useAuthManagerStore();

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

    background-color: var(--bs-tertiary-bg);
    border-right: var(--bs-border-color);
    scrollbar-width: none;

    //rem은 루트(html) 요소의 폰트 사이즈에 비례한다.
    .profile-wrapper {
        margin-top: 2.5rem;
        margin-bottom: 2.5rem;
        padding-left: 2.5rem;
        padding-right: 1.25rem;
        width: 100%;

        #userName {
            margin-top: 1.25rem;
        }
    }

    //이미지 둥글게 만들기 
    #profile-image {
        display: block;
        width: 7rem;
        height: 7rem;
        overflow: hidden;
        border-radius: 50%;
    }

    
    nav {
        .nav-item {
            width: 100%;
            padding-left: 1.5rem;
            padding-right: 1.5rem;

            //마우스 호버 시 컬러 파랗게
            :hover {
                color: var(--bs-primary);
            }
        }

        .nav-link {
            padding: .6rem;
            display: flex;
            align-items: center;

            //텍스트 컬러 조정 
            color: var(--bs-emphasis-color);

            //아이콘 위치 미세 조정 
            i {
                margin-right: 1.5rem;
            }

        }
    }
}
</style>