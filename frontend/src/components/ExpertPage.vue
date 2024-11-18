<template>
  <div id = "header">
    <h3 class = "user_name_txt">홍길동님의 차트입니다.</h3>
    <button type="button" class="counselWriteBtn" @click="goToCounselWritePage">상담 내역 작성</button>
  </div>

  <div id = "chart_list">
    <div v-if="data_line.datasets[0].data.length === 0" class="chart-empty-msg">
      데이터가 없습니다.
    </div>
    <div id = "MyChart_line">
      <canvas
          ref="MyChart_line"/>
    </div>
    <div id = "MyChart_pie">
      <canvas
          ref="MyChart_pie"/>
    </div>
  </div>
  <div id="user_list">
    <h3 id = "user_list_txt">상담자 리스트</h3>
    <table class="table table-striped">
      <thead>
      <tr>
        <th scope="col">No</th>
        <th scope="col">이름</th>
      </tr>
      </thead>
      <tbody>
      <!--<tr v-for="(user, index) in userList" :key="index">-->
      <tr v-for="(user, index) in testList" :key="index" @click="getDiaryList(user.email)" style="cursor: pointer;">
        <td>{{ index + 1 }}</td>
        <td>{{ user.email }}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <div id="diary_list">
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
        <td>{{index}}</td>
        <td>{{diary.title}}</td>
        <td>{{diary.userEmail}}</td>
        <td class="truncate_cell_content">{{diary.content}}</td>
        <td>{{formatDate(diary.createdAt)}}</td>
      </tr>
      <tr v-if="diaries.length === 0">
        <td colspan="5" class="text-center">작성된 일기가 없습니다.</td>
      </tr>
      </tbody>
    </table>
  </div>

 <div id="apply_counsel_list">
   <h2>상담 신청 내역</h2>
 </div>

</template>

<script>
import { Chart, registerables } from 'chart.js'
import axios from "axios";
import dayjs from "dayjs";
Chart.register(...registerables)

const dataset_mood = [ '기쁨', '우울', '슬픔', '화남']
const dataset_date = [ '11-01', '11-02', '11-03', '11-04', '11-05', '11-06', '11-07', '11-08', '11-09', '11-10']

export default {
  data:() => ({
    testList : [
      {email: 'test@gmail.com'},
      {email: 'test12@gmail.com'},
      {email: 'test34@gmail.com'}
    ],
    userList: [],
    diaries: [],
    counselList : [
      { name: '홍길동', counselTime: '2024.11.12.14:30', Duration_of_time: '30', addCounsel_YN: 'Y', counselContent: '상담내역은 가나다라마바사아자차타가나다라마바사아자차타' },
      { name: '홍길동', counselTime: '2024.11.12.15:00', Duration_of_time: '40', addCounsel_YN: 'Y', counselContent: '상담내역은 가나다라마바사아자차타가나다라마바사아자차타' },
      { name: '홍길동', counselTime: '2024.11.12.16:00', Duration_of_time: '20', addCounsel_YN: 'N', counselContent: '상담내역은 가나다라마바사아자차타가나다라마바사아자차타' }
    ],

    data_pie: {
      labels: dataset_mood,
      datasets: [{
        data: [ 12, 19, 3, 5],
        backgroundColor: [
          'rgba(255, 99, 132, 0.2)',
          'rgba(54, 162, 235, 0.2)',
          'rgba(255, 206, 86, 0.2)',
          'rgba(75, 192, 192, 0.2)',
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
        ],
        borderWidth: 1
      }]
    },

    data_line: {
      labels: dataset_date,
      datasets: [{
        label: '11월',
        data: [1,3,4,2,4,1,3,2,1,1],
        backgroundColor: [
          'rgba(255, 99, 132, 0.2)'
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)'
        ],
        borderWidth: 1
      }]
    },
    options_pie: {
      responsive: true,
      plugins: {
        legend: {
          position: "top"
        },
        title: {
          display: true,
          text: "기분 통계(Month)",
        }
      },
    },
    options_line: {
      responsive: true,
      plugins: {
        legend: {
          position: "top"
        },
        title: {
          display: true,
          text: "기분 통계(Day)",
        }
      },
    }
  }),
  mounted(){
    this.getManagedUsers()
    this.createChart_pie()
    this.createChart_line()
  },
  methods:{
    getManagedUsers() {
      axios.get('/api/expert/managedUsers', {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
          "Content-Type": 'application/json'
        },
      })
          .then((response) => {
            console.log('API Response:', response.data);
            this.userList = response.data.content;
            console.log(response.data)
            console.log(response.data.content)
          })
          .catch((error) => {
            console.error('사용자 목록을 가져오는 중 오류 발생:', error.response || error.message);
          });
    },
    getDiaryList(userEmail) {
      axios.get('/api/expert/diaries', {
        headers: {
          'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
          "Content-Type": 'application/json'
        },

        params: {
          ownerEmail: userEmail, // 조회하려는 다이어리 주인의 이메일
          page: 0,
          size: 10,
        },
      })
          .then((response) => {
            console.log('API Response:', response.data); // 전체 응답 로그 출력
            this.diaries = response.data.content;
            console.log(this.diaries)
          })
          .catch((error) => {
            console.error('다이어리 목록을 가져오는 중 오류 발생:', error.response || error.message);
          });
    },
    createChart_pie(){
      if (this.data_pie.datasets[0].data.length > 0) {
        new Chart(this.$refs.MyChart_pie, {
          type: 'pie',
          data: this.data_pie,
          options: this.options_pie
        })
      }
    },
    createChart_line(){
      if (this.data_line.datasets[0].data.length > 0) {
        new Chart(this.$refs.MyChart_line, {
          type: 'line',
          data: this.data_line,
          options: this.options_line
        })
      }
    },
    goToCounselWritePage() {
      this.$router.push({ name: 'CounselWritePage' })
          .catch(err => {
            console.error('라우팅 에러:', err);
          });
    },
    goToDiaryPage(diaryId) {
      // 해당 다이어리의 작성 페이지로 이동
      this.$router.push({ name: 'UpdateDiary', params: { id: diaryId } });
    },
    formatDate(date) {
      return dayjs(date).format('YYYY-MM-DD HH:mm:ss'); // 날짜 포맷 설정
    }
  }

}
</script>

<style>

#header{
  display: flex;
  justify-content: space-between;
}

.user_name_txt{
  margin-top: 20px;
  margin-left: 20px;
}

.counselWriteBtn{
  width: 140px;
  height: 40px;
  border-radius: 10px;
  margin-top: 20px;
  margin-right: 20px;
}

#user_list_txt{
  margin-bottom: 20px;
}

#chart_list{
  position: absolute;
  width : 850px;
  height : 400px;
  margin: 20px;
  padding: 15px;
  border: 1px solid black;
  border-radius: 30px;
}

.chart-empty-msg {
  position: absolute;
  top: 45%;
  left: 35%;
  height: 100%;
  font-size: 28px;
  font-weight: bold;
}

#MyChart_line{
  width : 450px;
  height : 350px;
  float: left;
  margin-right: 10px;
}

#MyChart_pie{
  width : 350px;
  height : 350px;
  float: left;
}

#user_list{
  position: absolute;
  left: 60%;
  width : 750px;
  height : 400px;
  padding: 20px;
  border: 1px solid black;
  border-radius: 20px;
  margin-top: 20px;
}

#diary_list{
  position: absolute;
  top: 60%;
  width: 1100px;
  height: 300px;
  padding: 20px;
  margin-top: 30px;
}
.truncate_cell_content{
  max-width: 250px; /* 셀의 최대 너비 */
  white-space: nowrap; /* 텍스트를 한 줄로 표시 */
  overflow: hidden; /* 넘치는 텍스트를 숨김 */
  text-overflow: ellipsis; /* 넘치는 텍스트 부분을 ...으로 표시 */
}

#apply_counsel_list{
  position: absolute;
  top: 60%;
  left: 73%;
  width: 500px;
  height: 300px;
  padding: 10px;
  border: 1px solid black;
  border-radius: 30px;
  margin-top: 20px;
}
</style>
