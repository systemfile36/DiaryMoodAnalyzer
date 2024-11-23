<template>
    <div class="user-main d-flex flex-column align-items-center">
        <div class="chart-wrapper">
            <canvas ref="recentMoodChart"></canvas>
        </div>


        <div class="ex-link-list d-lg-flex mt-5">
          <div class="card ex-link">
                <div class="card-body d-flex flex-column align-items-center">
                    <h5 class="card-title fw-bold">고립/은둔 자가진단 테스트</h5>
                    <p class="card-text text-body-secondary">자가 테스트</p>
                    <a href="http://seoulymind.org/hq-25/"
                    class="btn btn-outline-primary">
                        이동
                    </a>
                </div>
            </div>
            <div class="card ex-link">
                <div class="card-body d-flex flex-column align-items-center">
                    <h5 class="card-title fw-bold">고립/은둔 자가진단 테스트</h5>
                    <p class="card-text text-body-secondary">자가 테스트</p>
                    <a href="http://seoulymind.org/hq-25/"
                    class="btn btn-outline-primary">
                        이동
                    </a>
                </div>
            </div>
            <div class="card ex-link">
                <div class="card-body d-flex flex-column align-items-center">
                    <h5 class="card-title fw-bold">고립/은둔 자가진단 테스트</h5>
                    <p class="card-text text-body-secondary">자가 테스트</p>
                    <a href="http://seoulymind.org/hq-25/"
                    class="btn btn-outline-primary">
                        이동
                    </a>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import TempDiaryList from './TempDiaryList.vue';

import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router';
import { useAuthManagerStore } from '../stores/AuthManager';

import { Chart } from 'chart.js'

//템플릿 참조 
const recentMoodChart = ref(null);

const authManager = useAuthManagerStore();

const router = useRouter();

onMounted(()=>{

    //실제로는 최근의 7일간의 감정 수치를 로드해야 함 
    const data = [];

    //테스트용 랜덤 값 
    for(let i = 0; i < 7; i++) {
        data.push(parseFloat((Math.random()*10).toFixed(1)));
    }

    const labels = getLast7Days();

    new Chart(recentMoodChart.value, {
        type: "line", // 라인 차트
        data: {
          labels: labels,
          datasets: [
            {
              label: "감정",
              data: data,
              borderColor: "rgba(75, 192, 192, 1)",
              backgroundColor: "rgba(75, 192, 192, 0.2)",
              borderWidth: 2,
            },
          ],
        },
        options: {
          responsive: true,
          plugins: {
            title: {
              display: true,
              text: "최근 감정 변화량", // 그래프 제목
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
                text: "변화량",
              },
              min: 0.0,
              max: 10.0,
            },
          },
        },
      });
});

function logoutHandler() {
    authManager.logout();
    
}

function getLast7Days() {
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
}

</script>
<style scoped lang="scss">

    //높이를 가득 채운다. (vh)
    .user-main {
        width: 100%;
        height: 100vh;
    }

    //차트 최대 크기 제한 
    .chart-wrapper {
        width: 90%;
    }

    .btn {
        margin: 0px;
        width: 50%;
    }

    .ex-link { 
      margin: 2rem;
    }

</style>