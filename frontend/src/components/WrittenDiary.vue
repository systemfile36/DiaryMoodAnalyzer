<template>
  <el-table border :data="diaries">
    <el-table-column prop="no" label="no" width="120"></el-table-column>
    <el-table-column prop="title" label="title"></el-table-column>
  </el-table>
</template>

<script>
import axios from 'axios'

export default {
  name: 'dairies',
  data() {
    return {
      diaries: [],
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
          .then((response) =>{
            if(response.status === 200) {
              console.log(response.data);
              this.diaries = response.data.content.map((diary, index) => ({
                no: index+1,
                title: diary.title
              }));
            }
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