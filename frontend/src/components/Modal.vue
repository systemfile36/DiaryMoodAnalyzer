<template>
    <!-- Bootstrap의 Modal 사용 -->
    <div class="modal fade" id="Modal" tabindex="-1" aria-labelledby="ModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="ModalLabel">{{ modalTitle }}</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"
                        @click="onClose"></button>
                </div>
                <div class="modal-body">
                  <p>
                    {{ modalBody }}
                  </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"
                        @click="onClose">{{ closeBtnText }}</button>
                    <button type="button" class="btn btn-primary"
                        @click="onConfirm">{{ confirmBtnText  }}</button>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import { useModalManagerStore } from '../stores/ModalManager';
import { storeToRefs } from 'pinia';

const modalManager = useModalManagerStore();

const { modalTitle, modalBody, closeBtnText, confirmBtnText } = storeToRefs(modalManager);

function onConfirm() {
    modalManager.executeOnConfirm();
}

function onClose() {
    modalManager.executeOnClose();
}

</script>
<style scoped lang="scss">
    .modal-footer {
        flex-wrap: nowrap;
    }
</style>