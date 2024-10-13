<template>
<div class="m-3">
        <h2 class="display-5 mb-4">일기 작성</h2>
        <form class="p-2 d-flex flex-column">
            <div class="mb-3">
                <label for="InputTitle" class="form-label">Diary Title</label>
                <input 
                     type="text" class="form-control"
                     id="InputTitle"
                     v-model="title">
            </div>
            <div class="mb-3">
                <label for="InputContent" class="form-label">Diary Content</label>
                <textarea class="form-control" id="InputContent" 
                rows="15" v-model="content"></textarea>
             </div>

            <div class="d-flex ">
                <button type="button" class="btn btn-primary"
                @click="onSubmit">일기 작성</button>
            </div>
        </form>
    </div>

</template>
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter();

const title = ref("");
const content = ref("");

function onSubmit() {
    const diary = {
        title: title.value,
        content: content.value
    }
    const headers = {
        'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
        "Content-Type": 'application/json'
    }
    console.log(diary, headers);
    axios.post('/api/diaries', diary, {headers})
    .then((res)=>{
        console.log(res.data);
        console.log(res.status);
    }).catch((error)=>{
        console.log(error);
    })
}

</script>
<style lang="scss">
    
</style>