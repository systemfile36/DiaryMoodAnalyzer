<template>
  <div class="m-3">
    <h2 class="display-5 mb-4">일기 수정</h2>
    <form class="p-2 d-flex flex-column">
      <div class="mb-3">
        <label for="InputTitle" class="form-label">Diary Title</label>
        <input
            type="text"
            class="form-control"
            id="InputTitle"
            v-model="title"
        />
      </div>
      <div class="mb-3">
        <label for="InputContent" class="form-label">Diary Content</label>
        <textarea
            class="form-control"
            id="InputContent"
            rows="15"
            v-model="content"
        ></textarea>
      </div>

      <div class="d-flex">
        <button type="button" class="btn btn-primary" @click="onUpdate">
          일기 수정
        </button>
      </div>

      <div class="d-flex">
        <button type="button" class="btn btn-primary" @click="onDelete">
          일기 삭제
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router'; // useRoute 추가
import axios from 'axios';

// router와 route를 사용
const router = useRouter();
const route = useRoute(); // URL의 파라미터를 받아오는 데 사용

const title = ref("");
const content = ref("");
const id = route.params.id; // URL에서 id 파라미터를 추출

// 컴포넌트가 마운트되면 실행되는 onMounted 훅
onMounted(() => {
  if (id) {
    fetchDiary(id); // id가 있을 경우 해당 id로 일기 데이터를 가져옴
  }
});

function fetchDiary(diaryId) {

  const headers = {
    'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
    "Content-Type": 'application/json'
  };

  // 해당 id로 일기 데이터를 가져옴
  axios
      .get(`/api/diary/${diaryId}`, { headers })
      .then((res) => {
        title.value = res.data.title;
        content.value = res.data.content;
      })
      .catch((error) => {
        if (error.response) {
          console.error(`응답 오류: ${error.response.status}, ${error.response.data}`);
        } else if (error.request) {
          console.error("요청이 전송되었지만 응답을 받지 못했습니다.", error.request);
        } else {
          console.error("설정 중 오류가 발생했습니다:", error.message);
        }
      });
}

function onUpdate() {
  const diary = {
    title: title.value,
    content: content.value,
  };

  const headers = {
    Authorization: 'Bearer ' + localStorage.getItem('accessToken'),
    "Content-Type": 'application/json',
  };
  console.log(diary, headers);
  axios.put(`/api/diary/${id}`, diary, { headers })
      .then((res)=>{
        console.log(res.data);
        console.log(res.status);
        alert('일기를 수정하였습니다.');
        router.push('/writtenDiary');
      }).catch((error)=>{
    if (error.response) {
      console.error(`응답 오류: ${error.response.status}, ${error.response.data}`);
    } else if (error.request) {
      console.error("요청이 전송되었지만 응답을 받지 못했습니다.", error.request);
    } else {
      console.error("설정 중 오류가 발생했습니다:", error.message);
    }
  })
}

function onDelete(){
  console.log(id)
  const headers = {
    'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
    "Content-Type": 'application/json'
  }
  console.log(headers)
  axios.delete(`/api/diary/${id}`, { headers })
      .then(()=>{
        alert('일기를 삭제하였습니다.');
        router.push('/writtenDiary');
      }).catch((error)=>{
    if (error.response) {
      console.error(`응답 오류: ${error.response.status}, ${error.response.data}`);
    } else if (error.request) {
      console.error("요청이 전송되었지만 응답을 받지 못했습니다.", error.request);
    } else {
      console.error("설정 중 오류가 발생했습니다:", error.message);
    }
  })
}

</script>

<style lang="scss"></style>