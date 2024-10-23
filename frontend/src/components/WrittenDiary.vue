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
      <tr v-for="(diary, index) in currentDiaries" :key="diary.id" @click="goToDiaryPage(diary.id)" style="cursor: pointer;">
        <td width="100px">{{ (currentPage - 1) * itemsPerPage + index + 1 }}</td>
        <td class="truncate-cell-1">{{diary.title}}</td>
        <td width="200px">{{diary.userEmail}}</td>
        <td class="truncate-cell-2">{{diary.content}}</td>
        <td width="300px">{{formatDate(diary.createdAt)}}</td>
      </tr>
      <tr v-if="currentDiaries.length === 0">
        <td colspan="5" class="text-center">작성된 일기가 없습니다.</td>
      </tr>
      </tbody>
    </table>

    <div class="pagination">
      <button :disabled="currentPage === 1" @click="changePage(currentPage - 1)">이전</button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button :disabled="currentPage === totalPages || totalPages === 0" @click="changePage(currentPage + 1)">다음</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import dayjs from "dayjs";

export default {
  data() {
    return {
      diaries: [],
      currentPage: 1,
      itemsPerPage: 10,
      totalPages: 0,
      totalElements: 0,
    };
  },
  props: {
    msg: String,
  },
  created() {
    this.getDiaryList()
  },
  computed: {
    currentDiaries() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      console.log(this.currentPage)
      console.log(this.itemsPerPage)
      return this.diaries.slice(start, start + this.itemsPerPage);
    },
  },
  methods: {
    getDiaryList() {
      axios.get('/api/diaries', {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
          "Content-Type": 'application/json'
        },
        params: {
          page: this.currentPage - 1,
          size: this.itemsPerPage,
          sortBy: 'createdAt',
          ascending: false
        }
      })
          .then((response) => {
            console.log('API Response:', response.data); // 전체 응답 로그 출력
            this.diaries = response.data.content;
            this.totalElements = response.data.totalElements; // totalElements 가져오기
            this.totalPages = Math.ceil(response.data.totalElements / this.itemsPerPage); // 총 페이지 수 계산
          })
          .catch((error) => {
            console.log('다이어리 목록을 가져오는 중 오류 발생:', error);
          });
    },
    changePage(newPage) {
      if (newPage < 1 || newPage > this.totalPages) {
        return; // 유효하지 않은 페이지는 무시합니다
      }
      this.currentPage = newPage; // 현재 페이지 업데이트
      this.getDiaryList(); // 새로운 페이지의 목록을 가져옵니다
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