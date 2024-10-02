<template>
<div class="container mt-5">
    <div class="mb-3">
        <label for="diary-title" class="form-label">Diary Title</label>
        <input type="email" class="form-control" 
        id="diary-title" placeholder="Title"
        v-model="title">
    </div>
    <div class="mb-3">
        <label for="diary-content" class="form-label">Content</label>
        <textarea class="form-control" id="diary-content" 
        rows="3" v-model="content"></textarea>
    </div>
    <div class="mb-3">
        <button type="button" class="btn btn-primary"
            @click="onSubmit">Submit</button>
    </div>
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