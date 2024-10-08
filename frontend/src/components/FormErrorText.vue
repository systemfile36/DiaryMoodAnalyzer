<template>
    <div class="error form-text" v-if="errors[fieldName]"> 
        <div v-for="(message, i) in errors[fieldName]" :key="i">
            {{ message }}
        </div>
    </div>
</template>
<script setup>
import { defineProps } from 'vue'

const props = defineProps({
    errors: Array,
    fieldName: String
})

</script>
<style scoped lang="scss">
    .error {
        color: red;
    }
</style>