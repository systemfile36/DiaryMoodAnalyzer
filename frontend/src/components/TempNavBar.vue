<template>
    <nav id="topbar" class="navbar bg-body-tertiary">
        <div class="container-fluid">
            <button id="sidebar-toggler" class="navbar-toggler" type="button"
                @click="sidebarStore.toggleSideBar();">
                <span class="navbar-toggler-icon"></span>
            </button>
            <a id="topbar-title" class="navbar-brand" href="#">DiaryMoodAnalyzer</a>
            <div id="darkmode-menu" ref="dropdownRef" class="nav-item dropdown">
                <button 
                class="btn btn-link nav-link dropdown-toggle
                d-flex align-items-center" type="button"
                data-bs-toggle="dropdown" data-bs-display="static" 
                :aria-expanded="isDropdownExpanded.toString()"
                @click="toggleDropdown">
                    <i class="fa-solid fa-moon"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end" 
                :class="isDropdownExpanded ? 'show' : ''">
                    <li>
                        <button class="dropdown-item d-flex align-items-center" 
                        type="button" value="light"
                        @click="setDarkMode($event)">Light</button>
                    </li>
                    <li>
                        <button class="dropdown-item d-flex align-items-center" 
                        type="button" value="dark"
                        @click="setDarkMode($event)">Dark</button>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</template>
<script setup>
import { useSideBarStore } from '@/stores/SideBarManager';
import { ref } from 'vue';

//SideBar 관리를 위함
const sidebarStore = useSideBarStore();

//Dropdown template ref
const dropdownRef = ref(null);

//드롭다운 확장 여부 
const isDropdownExpanded = ref(false);

const htmlElement = document.querySelector('html');

function toggleDropdown() {
    isDropdownExpanded.value = !isDropdownExpanded.value;
}

function setDarkMode(event) {
    console.log(event.target.value);
    htmlElement.setAttribute("data-bs-theme", event.target.value); 
}

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

    .dropdown-menu-end {
        right: 0;
        left: auto;
    }
}


</style>