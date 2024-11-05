import { defineStore } from 'pinia';
import { ref } from 'vue';

//import bootstrap API
import { Modal } from 'bootstrap';

/**
 * 모달 창을 컨트롤하는 매니저. Modal.vue와 연계된다. 
 * 모달의 버튼 클릭 이벤트 리스너를 설정하거나, 텍스트를 설정할 수 있다. 
 */
export const useModalManagerStore = defineStore('ModalManager', () => {

    /**
     * 모달 요소의 참조
     * @type {HTMLElement}
     */
    const modalElement = ref(null);

    /**
     * Bootstrap API의 Modal 클래스 인스턴스 
     * @type {Modal}
     */
    const modalInstance = ref(null);


    //각 버튼이 클릭되었을 때 실행될 이벤트 
    const onClickConfirm = ref(null);
    const onClickClose = ref(null);

    /**
     * 모달 창이 닫힐 때 실행될 이벤트 
     */
    const onModalClose = ref(null);

    //모달 창에 표시될 텍스트 
    const modalTitle = ref("default");
    const modalBody = ref("default");

    //모달 창의 버튼에 표시될 텍스트 
    const confirmBtnText = ref("Confirm");
    const closeBtnText = ref("Close");

    /**
     * 모달에 대한 참조를 설정한다. 
     */
    function init() {
        //이미 초기화된 상태가 아니면 참조를 초기화한다. 
        if(!isInitialized()) {
            modalElement.value = document.querySelector('#Modal');
            modalInstance.value = new Modal(modalElement.value, 
                { backdrop: 'static'});
        }
    }

    /**
     * 모달에 대한 참조가 초기화 되었는 지 여부를 반환한다. 
     * null 체크를 통해 수행한다. 
     * @returns - 모달에 대한 참조가 null이 아니라면 true, otherwise return false.
     */
    function isInitialized() {
        return modalElement.value !== null && modalInstance.value !== null
    }

    /**
     * 모달 창을 연다. 
     * @param {*} cbConfirm Confirm 버튼을 누를 시 실행할 이벤트
     * @param {*} cbClose Close 버튼을 누를 시 실행할 이벤트
     */
    function openModal(cbConfirm, cbClose) {

        init();

        //모달을 연다. 
        modalInstance.value.show();
        
        onClickConfirm.value = cbConfirm;
        onClickClose.value = cbClose;
    }

    /**
     * 모달 창을 닫는다. 이벤트들을 null로 초기화 한다.
     */
    function closeModal() {
        
        init();

        //모달을 숨긴다.(닫는다.)
        modalInstance.value.hide();

        onClickConfirm.value = null;
        onClickClose.value = null;

        resetOnModalClose();
    }

    /**
     * 모달 창이 닫힐 때(hidden.bs.modal) 실행할 이벤트를 설정한다. 
     * @param {*} cbModalClose 모달 창이 닫힐 때 실행될 이벤트
     */
    function setOnModalClose(cbModalClose) {
        init();

        onModalClose.value = cbModalClose;

        modalElement.value.addEventListener('hidden.bs.modal', onModalClose.value);
    }

    /**
     * 모달 창이 닫힐 때(hidden.bs.modal)의 이벤트를 제거한다.
     */
    function resetOnModalClose() {
        init();

        onModalClose.value = null;

        modalElement.value.removeEventListener('hidden.bs.modal', onModalClose.value);
    }

    /**
     * 모달 창의 텍스트를 설정한다. 
     * @param {string} title 모달 창의 제목
     * @param {string} body 모달 창의 본문
     * @param {string} confirm 확인 버튼의 텍스트, 기본 값 "Confirm"
     * @param {string} close 닫기 버튼의 텍스트, 기본 값 "Close"
     */
    function setModalText(title, body, confirm="Confirm", close="Close") {
        modalTitle.value = title;
        modalBody.value = body;
        confirmBtnText.value = confirm;
        closeBtnText.value = close;
    }

    /**
     * onClickConfirm을 실행한 후, 모달 창을 닫는다.
     */
    function executeOnConfirm() {
        if(onClickConfirm.value) onClickConfirm.value();
        closeModal();
    }

    /**
     * onClickClose를 실행한 후, 모달 창을 닫는다.
     */
    function executeOnClose() {
        if(onClickClose.value) onClickClose.value();
        closeModal();
    }

    return {
        modalElement,
        modalInstance,
        onClickConfirm,
        onClickClose,
        modalTitle,
        modalBody,
        confirmBtnText,
        closeBtnText,
        init,
        openModal,
        closeModal,
        executeOnClose,
        executeOnConfirm,
        setModalText,
        setOnModalClose,
    }
})