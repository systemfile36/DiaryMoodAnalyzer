import { defineStore } from "pinia";
import { ref, watch } from "vue";

/**
 * 시스템 테마를 관리하는 매니저 
 */
export const useThemeStore = defineStore('themeManager', () => {
    const isDarkmode = ref(false);

    const isInit = ref(false);

    //Constants
    const THEME_ATTR = "data-bs-theme";
    const DARK_MODE_VALUE = "dark";
    const LIGHT_MODE_VALUE = "light";
    const LOCAL_STORAGE_KEY = 'theme'

    //시스템 테마의 다크 모드 여부 확인
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches

    const htmlNode = document.querySelector('html');

    /**
     * localStorage에 저장된 테마를 불러온다. 없을 경우 시스템 기본을 따른다.
     */
    function initTheme() {
        const saved = localStorage.getItem(LOCAL_STORAGE_KEY);
        console.log(saved);
        if(saved) {
            isDarkmode.value = saved === DARK_MODE_VALUE;
        } else {
            isDarkmode.value = prefersDark;
        }
        
        isInit.value = true;
    }

    /**
     * 다크 모드를 토글한다.
     */
    function toggleDarkmode() {
        isDarkmode.value = !isDarkmode.value;
    }

    /**
     * 다크 모드를 설정할지 여부를 받아서 반영한다.
     * @param {boolean} value - if true, on darkmode. otherwise, off
     */
    function setDarkmode(value) {
        isDarkmode.value = JSON.parse(value);
    }

    //다크 모드 변수 감시
    watch(isDarkmode, (newVal) => {
        //변경된 값을 참조하여 다크 모드 적용

        //immediate: true로 설정될 경우, useThemeSotre가 호출되는 시점에 즉시 본 함수가 실행되어 
        //localStorage에서 테마를 읽어오는 작업이 정상적으로 작동하지 않는다. 이를 막기 위함이다.
        if(!isInit.value) return;

        const currentTheme = newVal ? DARK_MODE_VALUE : LIGHT_MODE_VALUE;

        htmlNode.setAttribute(THEME_ATTR, currentTheme);
        localStorage.setItem(LOCAL_STORAGE_KEY, currentTheme);
        
        //즉시 반영. 앱 시작시 적용하기 위함
    }, { immediate: true })

    return {
        isDarkmode,
        isInit,
        toggleDarkmode,
        setDarkmode, 
        initTheme
    }

})