<template>
  <div id = "header">
    <h3 class = "brand_name">웹사이트 이름</h3>
    <button type="button" class="counselWriteBtn" @click="goToCounselWritePage">상담 예약 신청</button>
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
  <div id="counsel_res_list">
    <h3 id = "counsel_res_list_txt">상담 예약 현황</h3>
    <table class="table table-striped">
      <thead>
      <tr>
        <th scope="col">No</th>
        <th scope="col">상담예약장소</th>
        <th scope="col">상담예약시간</th>
        <th scope="col">기타</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(counsel, index) in counselList" :key="index">
        <td>{{ index + 1 }}</td>
        <td>{{ counsel.reservationLocation }}</td>
        <td>{{ counsel.reservationTime }}</td>
        <td>{{ counsel.etc }}</td>
      </tr>
      </tbody>
    </table>
  </div>

  <div id = "user_guide_click" style="cursor: pointer;" @click="goToUserGuidePage">
    <h2 class = "user_guide_txt">이용 안내</h2>
    <h5>웹사이트 이름 이용방법을 안내드립니다.</h5>
    <h5 class = "user_guide_txt">자세히</h5>
  </div>

  <div id = "webSiteLink">
    <h2 onclick = "window.location.href='https://www.gyeongnam.go.kr/board/list.gyeong?boardId=BBS_0000057&menuCd=DOM_000000144001000000&contentsSid=7247&cpath='"
        class="goToWebSite">경상남도 보건복지 사이트</h2>
    <h2 onclick = "window.location.href='https://youth.gyeongnam.go.kr/youth/#close'" class="goToWebSite">경상남도 청년정보플랫폼</h2>
    <h2 onclick = "window.location.href='https://www.jinju.go.kr/00134/00614/02690.web'" class="goToWebSite">진주시 보건복지 사이트</h2>
  </div>

  <div id = "self_test" onclick = "window.location.href='http://seoulymind.org/hq-25/'" style="cursor: pointer;">
    <h2 style="font-weight: bold">고립/은둔 자가진단 테스트</h2>
  </div>

</template>

<script>
import { Chart, registerables } from 'chart.js'
Chart.register(...registerables)

const dataset_mood = [ '기쁨', '우울', '슬픔', '화남']
const dataset_date = [ '11-01', '11-02', '11-03', '11-04', '11-05', '11-06', '11-07', '11-08', '11-09', '11-10']

export default {
  data:() => ({

    counselList: [
      { reservationLocation: '경상국립대 심리상담소', reservationTime: '2024.11.12.14:30', etc: '기타 등등'},
      { reservationLocation: '경상국립대 심리상담소', reservationTime: '2024.11.12.15:00', etc: '기타 등등'},
      { reservationLocation: '경상국립대 심리상담소', reservationTime: '2024.11.12.16:00', etc: '기타 등등'},
      { reservationLocation: '경상국립대 심리상담소', reservationTime: '2024.11.12.16:30', etc: '기타 등등'},
      { reservationLocation: '경상국립대 심리상담소', reservationTime: '2024.11.12.17:00', etc: '기타 등등'}
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
    goToUserGuidePage(){
      this.$router.push({ name: 'UserGuide' })
          .catch(err => {
            console.error('라우팅 에러:', err);
          });
    },
  }

}
</script>

<style>

#header{
  display: flex;
  justify-content: space-between;
}

.brand_name{
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

#counsel_res_list_txt{
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
  margin-right: 10px;
  float: left;
}

#MyChart_pie{
  width : 350px;
  height : 350px;
  float: left;
}

#counsel_res_list{
  position: absolute;
  left: 60%;
  width : 750px;
  height : 400px;
  padding: 20px;
  border: 1px solid black;
  border-radius: 30px;
  margin-top: 20px;
  margin-bottom: 50px;
}

#user_guide_click{
  position: absolute;
  top: 60%;
  width : 500px;
  height : 300px;
  border: 1px solid black;
  border-radius: 30px;
  margin-left: 20px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.user_guide_txt{
  padding: 10px;
  font-weight: bolder;
}

#webSiteLink{
  position: absolute;
  top: 60%;
  left: 44%;
  width : 500px;
  height : 300px;
  border: 1px solid black;
  border-radius: 30px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.goToWebSite{
  cursor: pointer;
  font-weight: bold;
  margin-top: 15px;
  margin-bottom: 15px;
}

#self_test{
  position: absolute;
  top: 60%;
  left: 73%;
  width : 500px;
  height : 300px;
  border: 1px solid black;
  border-radius: 30px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
