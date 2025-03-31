<template>
  <div class="expert-top d-lg-flex mt-3">
     <div class="user-chart">
      <select ref="selectDailyRange"
          class="form-select mb-2" aria-label="SelectDailyRange"
          @change="onChanged">
            <option value="7" selected>최근 7일</option>
            <option value="14">최근 14일</option>
            <option value="30">최근 30일</option>
          </select>
         <canvas ref="recentMoodChart"></canvas>
     </div>
     <div class="user-list d-flex">
        <table class="table table-striped">
          <caption>담당 사용자 리스트</caption>
          <thead>
          <tr>
            <th scope="col">No</th>
            <th scope="col">이름</th>
            <th scope="col">변화량</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(user, i) in managedUsers" :key="i" style="cursor: pointer;">
              <td>{{ i + 1 }}</td>
              <td>
                <router-link :to="'/expert/diaries/' + user.email"
              style="text-decoration: none; color: black;">
                {{ user.email }}
              </router-link>
              </td>
              <td>
                <a role="button" @click="onClickViewGraph(user.email)">
                  보기
                </a>
              </td>
          </tr>
          </tbody>
        </table>
     </div>
   </div>
</template>

<script setup>
import { Chart } from 'chart.js'
import axios from "axios";
import dayjs from "dayjs";

import { useDiaryManagerStore } from '../stores/DiaryManager';
import { useAuthManagerStore } from '../stores/AuthManager';
import { onMounted, ref } from 'vue';

const diaryManager = useDiaryManagerStore();

const authManager = useAuthManagerStore();

//차트 인스턴스 
const chartInstance = ref(null);

//템플릿 참조 
const recentMoodChart = ref(null);

//범위 선택 select 요소 템플릿 참조
const selectDailyRange = ref(null);

const managedUsers = ref([]);

const currentSelectedUser = ref("");

onMounted(async ()=>{
  managedUsers.value = await authManager.getManagedUsers();

  if(!managedUsers.value || managedUsers.value.length == 0) {
    console.log(managedUsers.value)
    console.log("managedUsers 요청 실패!");
    return;
  }

  currentSelectedUser.value = managedUsers.value[0]['email'];

  drawRecentDepressionChart(managedUsers.value[0]['email']);

})

async function drawRecentDepressionChart(targetEmail) {
  const data = await diaryManager.getRecentDailyAvgDepressionLevel(selectDailyRange.value.value, targetEmail);
  
  //key(날짜) 기준으로 정렬 
  const labels = Object.keys(data).sort();

  //값들도 정렬된 key를 사용해서 매핑
  const values = labels.map((key) => data[key]);
  
  drawDepressionChart(labels, values, targetEmail);
}

function drawDepressionChart(labels, data, targetEmail) {

//이미 Chart 인스턴스가 존재한다면, 명시적으로 destoy 한다.
if(chartInstance.value) {
  chartInstance.value.destroy();
}

chartInstance.value = new Chart(recentMoodChart.value, {
      type: "line", // 라인 차트
      data: {
        labels: labels,
        datasets: [
          {
            label: "우울 수치",
            data: data,
            borderColor: "rgba(255, 99, 132, 1)",
            backgroundColor: "rgba(255, 99, 132, 0.2)",
            borderWidth: 2,
          },
        ],
      },
      options: {
        responsive: true,
        aspectRatio: 1.2, //해상도 고정 
        plugins: {
          title: {
            display: true,
            text: targetEmail + "님의 최근 감정 변화량", // 그래프 제목
            font: {
              size: 18,
            },
          },
        },
        scales: {
          x: {
            title: {
              display: true,
              text: "날짜",
            },
          },
          y: {
            title: {
              display: true,
              text: "우울 수치",
            },
            min: 0.0,
            max: 10.0,
          },
        },
      },
    });

}

function onChanged() {
  drawRecentDepressionChart(currentSelectedUser.value);
}

function onClickViewGraph(userEmail) {
  currentSelectedUser.value = userEmail;
  drawRecentDepressionChart(currentSelectedUser.value);
}

</script>

<style scoped lang="scss">
.expert-top {
  width: 100%;
  height: 50vh;
}

.user-chart {
  width: 90%;
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