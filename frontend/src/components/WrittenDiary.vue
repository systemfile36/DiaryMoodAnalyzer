<template>
  <div class="board-list">
    <table class="table">
      <thead>
      <tr>
        <th scope="col">No</th>
        <th scope="col">제목</th>
        <th scope="col">작성자</th>
        <th scope="col">작성내역</th>
        <th scope="col">등록일시</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="diary in diaries" :key="diary">
        <td>{{diary.id}}</td>
        <td>{{diary.title}}</td>
        <td>{{diary.userEmail}}</td>
        <td>{{diary.content}}</td>
        <td>{{diary.createdAt}}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      diaries: {},
      no: '',
    };
  },
  props: {
    msg: String,
  },
  created() {
    this.getDiaryList()
  },
  methods: {
    getDiaryList() {
      axios.get('/api/diaries', {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
          "Content-Type": 'application/json'
        },
        params: {
          page: 0, // 첫 번째 페이지
          size: 10, // 페이지당 항목 수
          sortBy: 'createdAt', // 생성 날짜로 정렬
          ascending: false // 내림차순 정렬
        }
      })
          .then((response) => {
            //console.log(response.data);
            //this.diaries = response.data.content;
            this.diaries = response.data.content;
            console.log(response.data.content)
            console.log(this.diaries);
          })
          .catch((error) => {
            console.log('다이어리 목록을 가져오는 중 오류 발생:', error);
          });
    }
  }
}

</script>
<style lang="scss">

</style>