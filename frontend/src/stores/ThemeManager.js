import { defineStore } from "pinia";
import { ref, watch } from "vue";

/**
 * 시스템 테마를 관리하는 매니저 
 */
export const useThemeStore = defineStore('themeManager', () => {
    const isDarkmode = ref(false);

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
        if(saved !== null) {
            isDarkmode.value = saved === DARK_MODE_VALUE;
        } else {
            isDarkmode.value = prefersDark;
        }
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
        isDarkmode.value = value;
    }

    //다크 모드 변수 감시
    watch(isDarkmode, (newVal) => {
        //변경된 값을 참조하여 다크 모드 적용
        const currentTheme = newVal ? DARK_MODE_VALUE : LIGHT_MODE_VALUE;

        htmlNode.setAttribute(THEME_ATTR, currentTheme);
        localStorage.setItem(LOCAL_STORAGE_KEY, currentTheme);
        
        //즉시 반영. 앱 시작시 적용하기 위함
    }, { immediate: true })

    return {
        isDarkmode,
        toggleDarkmode,
        setDarkmode, 
        initTheme
    }

})