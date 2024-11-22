<template>
  <div class="expert-top d-lg-flex mt-3">
     <div class="user-chart d-flex">
         <canvas ref="userMoodChart"></canvas>
     </div>
     <div class="user-list d-flex">
        <table class="table table-striped">
          <caption>상담자 리스트</caption>
          <thead>
          <tr>
            <th scope="col">No</th>
            <th scope="col">이름</th>
          </tr>
          </thead>
          <tbody>
          <!--<tr v- for="(user, index) in userList" :key="index" @click="getDiaryList(user.email)" style="cursor: pointer;">-->
          <tr v-for="(user, index) in testList" :key="index">
            <td>{{ index + 1 }}</td>
            <td>{{ user.email }}</td>
          </tr>
          </tbody>
        </table>
     </div>
   </div>

   <div class="expert-bottom d-lg-flex flex-column mt-5 align-items-center">
     <div class = "user-diary"></div>
   </div>
</template>

<script>
import { Chart, registerables } from 'chart.js'
import axios from "axios";
import dayjs from "dayjs";
Chart.register(...registerables)

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
    //getDiaryList(userEmail) {
    //  axios.get('/api/expert/diaries', {
    //    headers: {
    //      'Authorization': 'Bearer ' + localStorage.getItem('accessToken'),
    //      "Content-Type": 'application/json'
    //    },
//
    //    params: {
    //      ownerEmail: userEmail, // 조회하려는 다이어리 주인의 이메일
    //      page: 0,
    //      size: 10,
    //    },
    //  })
    //      .then((response) => {
    //        console.log('API Response:', response.data); // 전체 응답 로그 출력
    //        this.diaries = response.data.content;
    //        console.log(this.diaries)
    //      })
    //      .catch((error) => {
    //        console.error('다이어리 목록을 가져오는 중 오류 발생:', error.response || error.message);
    //      });
    //},
    createChart_line(){
      const canvas = this.$refs.userMoodChart;
      canvas.width = canvas.parentElement.offsetWidth;
      canvas.height = canvas.parentElement.offsetHeight;

      if (this.data_line.datasets[0].data.length > 0) {
        new Chart(canvas, {
          type: 'line',
          data: this.data_line,
          options: this.options_line
        })
      }
    },
    formatDate(date) {
      return dayjs(date).format('YYYY-MM-DD HH:mm:ss'); // 날짜 포맷 설정
    }
  }

}
</script>

<style scoped lang="scss">
.expert-top {
  width: 100%;
  height: 50vh;
}

.user-chart {
  width: 70%;
  height: auto;
  margin: 1rem;
}

canvas{
  width: 100%;
  height: 100%;
}

.user-list{
  width: 30%;
  height: auto;
  margin: 1rem;
  overflow: hidden;
}

table{
  width: 100%;
  height: 100%;
}

caption{
  caption-side: top;
  text-align: left;
  font-size: 24px;
  font-weight: bolder;
  color: black;
}

.user-diary{
  width: 95%;
  height: 35vh;
  border: 2px solid black;
  border-radius: 20px;
}

@media (max-width: 991px) {

  .user-chart,
  .user-list,
  .user-diary{
    width: 100%;
  }
}

</style>