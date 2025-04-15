<template>
    <nav id="topbar" class="navbar bg-body-tertiary">
        <div class="container-fluid">
            <button id="sidebar-toggler" class="navbar-toggler" type="button"
                @click="sidebarStore.toggleSideBar();">
                <span class="navbar-toggler-icon"></span>
            </button>
            <a id="topbar-title" class="navbar-brand" href="#">DiaryMoodAnalyzer</a>
            <div id="features-wrapper" class="d-flex align-items-center">
                <div id="notification-menu" class="nav-item dropdown">
                    <button class="btn btn-link nav-link dropdown-toggle d-flex align-items-center"
                    type="button" data-bs-toggle="dropdown" data-bs-display="static">
                        <i class="fa-solid fa-bell"></i>
                    </button>
                    <div class="dropdown-menu dropdown-menu-end show">
                        <div id="notification-header" 
                        class="d-flex justify-content-between align-items-center 
                        border-bottom pb-2 px-3">
                            <span class="">알림</span>
                            <button class="" type="button">
                                <i class="fa-solid fa-gear"></i>
                            </button>
                        </div>
                        <div class="dropdown-item">
                            <span>test</span>
                        </div>
                    </div>
                </div>
                <div id="darkmode-menu" ref="dropdownRef" class="nav-item dropdown">
                    <button 
                    class="btn btn-link nav-link dropdown-toggle
                    d-flex align-items-center" type="button"
                    data-bs-toggle="dropdown" data-bs-display="static" 
                    :aria-expanded="isDropdownExpanded.toString()"
                    @click="toggleDropdown">
                        <i :class="themeStore.isDarkmode ? 'fa-solid fa-moon' : 'fa-regular fa-sun'"></i>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end" 
                    :class="isDropdownExpanded ? 'show' : ''">
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
import { ref, onMounted, onBeforeUnmount } from 'vue';

//SideBar 관리를 위함
const sidebarStore = useSideBarStore();

//For manage Theme
const themeStore = useThemeStore();

//Dropdown template ref
const dropdownRef = ref(null);

//드롭다운 확장 여부 
const isDropdownExpanded = ref(false);

function toggleDropdown() {
    isDropdownExpanded.value = !isDropdownExpanded.value;
}

//선택 후, 드롭다운을 닫는다.
function setDarkMode(event) {
    themeStore.setDarkmode(event.target.value);
    toggleDropdown();
}

//클릭된 지점이 드롭다운 메뉴가 아닐 경우, 드롭다운을 닫는다.
function onDocumentClick(event) {
    if(dropdownRef.value && !dropdownRef.value.contains(event.target)) {
        isDropdownExpanded.value = false
    }
}

//마운트 시점에 따라 이벤트 추가/삭제
onMounted(() => {
    document.addEventListener('click', onDocumentClick);
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

        &.active {
            background-color: var(--dropdown-active-bg);
        }
        
        i {
            margin-right: 0.5rem;
        }
    }
}

//알림 메뉴
#notification-menu {

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

    #notification-header {

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
}


</style>