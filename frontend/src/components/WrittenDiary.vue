<template>
  <div class="board-list">
    <table class="table table-striped">
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
      <tr v-for="(diary, index) in diaries" :key="diary.id" @click="goToDiaryPage(diary.id)" style="cursor: pointer;">
        <td width="100px">{{index+1}}</td>
        <td class="truncate-cell-1">{{diary.title}}</td>
        <td width="200px">{{diary.userEmail}}</td>
        <td class="truncate-cell-2">{{diary.content}}</td>
        <td width="300px">{{formatDate(diary.createdAt)}}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios'
import dayjs from "dayjs";

export default {
  data() {
    return {
      diaries: {},
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
            this.diaries = response.data.content;
            console.log(response.data.content)
            console.log(this.diaries);
          })
          .catch((error) => {
            console.log('다이어리 목록을 가져오는 중 오류 발생:', error);
          });
    },
    formatDate(date) {
      return dayjs(date).format('YYYY-MM-DD HH:mm:ss'); // 날짜 포맷 설정
    },
    goToDiaryPage(diaryId) {
      // 해당 다이어리의 작성 페이지로 이동
      this.$router.push({ name: 'UpdateDiary', params: { id: diaryId } });
    }
  }
}

</script>
<style lang="scss">

.truncate-cell-1 {
  max-width: 100px; /* 셀의 최대 너비 */
  white-space: nowrap; /* 텍스트를 한 줄로 표시 */
  overflow: hidden; /* 넘치는 텍스트를 숨김 */
  text-overflow: ellipsis; /* 넘치는 텍스트 부분을 ...으로 표시 */
}

.truncate-cell-2 {
  max-width: 300px; /* 셀의 최대 너비 */
  white-space: nowrap; /* 텍스트를 한 줄로 표시 */
  overflow: hidden; /* 넘치는 텍스트를 숨김 */
  text-overflow: ellipsis; /* 넘치는 텍스트 부분을 ...으로 표시 */
}
</style>