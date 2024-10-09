<template>
    <nav id="topbar" class="navbar bg-body-tertiary">
        <div class="container-fluid">
            <button id="sidebar-toggler" class="navbar-toggler" type="button"
                @click="sidebarStore.toggleSideBar();">
                <span class="navbar-toggler-icon"></span>
            </button>
            <a id="topbar-title" class="navbar-brand" href="#">Brand</a>
        </div>
    </nav>
</template>
<script setup>
import { useSideBarStore } from '../stores/SidebarManager';

//SideBar 관리를 위함
const sidebarStore = useSideBarStore();

</script>
<style lang="scss">
@import '../style.scss';

#sidebar-toggler {
    border: none;
}

</style>