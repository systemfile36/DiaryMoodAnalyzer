<template>
  <div id = "header">
    <h3 id = "user_name_txt">홍길동님의 차트입니다.</h3>
    <button type="button" class="counselWriteBtn" @click="goToCounselWritePage">상담 내역 작성</button>
  </div>

  <div id = "chart_list">
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
        <th scope="col">나이</th>
        <th scope="col">성별</th>
        <th scope="col">상담자 상태</th>
        <th scope="col">상담예약시간</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(user, index) in userList" :key="index">
        <td>{{ index + 1 }}</td>
        <td>{{ user.name }}</td>
        <td>{{ user.age }}</td>
        <td>{{ user.gender }}</td>
        <td>{{ user.status }}</td>
        <td>{{ user.reservationTime }}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <div id="counsel_list">
    <table class="table table-striped">
      <thead>
      <tr>
        <th scope="col">No</th>
        <th scope="col">이름</th>
        <th scope="col">상담진행시간</th>
        <th scope="col">상담소요시간</th>
        <th scope="col">추가상담예약</th>
        <th scope="col">상담내역</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(counsel, index) in counselList" :key="index" @click="goToCounselEditPage" style="cursor: pointer;">
        <td>{{ index + 1 }}</td>
        <td>{{ counsel.name }}</td>
        <td>{{ counsel.counselTime }}</td>
        <td>{{ counsel.Duration_of_time}}</td>
        <td>{{ counsel.addCounsel_YN }}</td>
        <td>{{ counsel.counselContent }}</td>
      </tr>
      </tbody>
    </table>
  </div>

</template>

<script>
import { Chart, registerables } from 'chart.js'
Chart.register(...registerables)

const dataset_mood = [ '기쁨', '우울', '슬픔', '화남']
const dataset_date = [ '11-01', '11-02', '11-03', '11-04', '11-05', '11-06', '11-07', '11-08', '11-09', '11-10']

export default {
  data:() => ({

    userList: [
      { name: '홍길동', age: 28, gender: '남', status: '우울 하', reservationTime: '2024.11.12.14:30' },
      { name: '김철수', age: 35, gender: '남', status: '우울 중', reservationTime: '2024.11.12.15:00' },
      { name: '이영희', age: 24, gender: '여', status: '우울 중', reservationTime: '2024.11.12.16:00' },
      { name: '박지민', age: 40, gender: '여', status: '우울 상', reservationTime: '2024.11.12.16:30' },
      { name: '최수현', age: 30, gender: '남', status: '우울 하', reservationTime: '2024.11.12.17:00' }
    ],

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
    this.createChart_pie()
    this.createChart_line()
  },
  methods:{
    createChart_pie(){
      new Chart(this.$refs.MyChart_pie, {
        type:'pie',
        data:this.data_pie,
        options:this.options_pie
      })
    },
    createChart_line(){
      new Chart(this.$refs.MyChart_line, {
        type:'line',
        data:this.data_line,
        options:this.options_line
      })

    },
    goToCounselEditPage() {
      this.$router.push({ name: 'CounselEditPage' })
          .catch(err => {
            console.error('라우팅 에러:', err);
          });
    },
    goToCounselWritePage() {
      this.$router.push({ name: 'CounselWritePage' })
          .catch(err => {
            console.error('라우팅 에러:', err);
          });
    }
  }

}
</script>

<style>

#header{
  display: flex;
  justify-content: space-between;
}

#user_name_txt{
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
  width : 850px;
  height : 450px;
  margin: 20px;
  padding: 15px;
  border: 1px solid black;
  border-radius: 30px;
  float: left;
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
  width : 750px;
  height : 450px;
  padding: 20px;
  border: 1px solid black;
  border-radius: 30px;
  margin-top: 20px;
  margin-bottom: 50px;
  float: left;
}

#counsel_list{
  padding: 20px;
  margin-top: 30px;
}
</style>
