<template>
  <div class="expert-top d-lg-flex mt-3">
     <div class="user-chart d-flex">
         <canvas ref="userMoodChart"></canvas>
     </div>
     <div class="user-list d-flex">
        <table class="table table-striped">
          <caption>담당 사용자 리스트</caption>
          <thead>
          <tr>
            <th scope="col">No</th>
            <th scope="col">이름</th>
          </tr>
          </thead>
          <tbody>
          <!--<tr v- for="(user, index) in userList" :key="index" @click="getDiaryList(user.email)" style="cursor: pointer;">-->
<!--      <tr v-for="(user, index) in testList" :key="index">
            <td>{{ index + 1 }}</td>
            <td>{{ user.email }}</td>
          </tr> -->
          <!-- 테스트용으로 하드 코딩. 나중에 수정 예정 -->
          <tr>
            <td>1</td>
            <td>
              <router-link to="/expert/diaries/test@email.com"
              style="text-decoration: none; color: black;"
              data-bs-toggle="tooltip" data-bs-placement="top"
              data-bs-title="해당 사용자의 Diary 리스트를 보려면 클릭하세요">
                test@email.com
              </router-link>
            </td>
          </tr>
          </tbody>
        </table>
     </div>
   </div>

<!--    <div class="expert-bottom d-lg-flex flex-column mt-5 align-items-center">
     <div class = "user-diary"></div>
   </div> -->
</template>

<script>
import { Chart, registerables } from 'chart.js'
import axios from "axios";
import dayjs from "dayjs";

import { Tooltip } from 'bootstrap';

Chart.register(...registerables)

const getLast7Days = () => {
    const dates = [];

    //현재 날짜 
    const today = new Date();

    //현재 - 6일에서 현재 - 0일 까지 
    for(let i = 6; i >= 0; i--) {
        const date = new Date();
        //날짜 세팅 
        date.setDate(today.getDate()-i);

        //날짜를 나타내는 문자열 추가 (MM-DD 형식)
        dates.push(`${date.getMonth() + 1}-${date.getDate()}`);
    }

    return dates;
    };

const generateRandomValue = () => {
    let data = [];
    for(let i = 0; i < 7; i++) {
        data.push(parseFloat((Math.random()*10).toFixed(1)));
    }
    return data;
}

const dataset_date = getLast7Days();

const random_data = generateRandomValue();



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
        data: random_data,
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
          text: "test@email.com 님의 최근 감정 통계(Day)",
        }
      },
    }
  }),
  mounted(){
    this.getManagedUsers()
    this.createChart_line()

    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new Tooltip(tooltipTriggerEl))
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