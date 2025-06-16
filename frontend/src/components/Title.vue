<template>
    <div class="user-main d-flex flex-column align-items-center">

        <div class="chart-wrapper d-flex flex-column align-items-center">
          <select ref="selectDailyRange"
          class="form-select mb-2" aria-label="SelectDailyRange"
          @change="onChanged">
            <option value="7" selected>최근 7일</option>
            <option value="14">최근 14일</option>
            <option value="30">최근 30일</option>
          </select>
            <canvas ref="recentMoodChart"></canvas>
        </div>

        <div class="ex-link-list d-lg-flex mt-5">
          <div class="card ex-link">
                <div class="card-body d-flex flex-column align-items-center">
                    <h5 class="card-title fw-bold">경상남도청 보건복지 사이트</h5>
                    <p class="card-text text-body-secondary">복지 정보</p>
                    <a href="https://www.gyeongnam.go.kr/board/list.gyeong?boardId=BBS_0000057&menuCd=DOM_000000144001000000&contentsSid=7247&cpath="
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
                    <h5 class="card-title fw-bold">경상남도 청년정보플랫폼</h5>
                    <p class="card-text text-body-secondary">복지 정보</p>
                    <a href="https://youth.gyeongnam.go.kr/youth/"
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
import { useAuthManagerStore } from '@/stores/AuthManager';
import { useDiaryManagerStore } from '@/stores/DiaryManager';

import { Chart } from 'chart.js'
import dayjs from 'dayjs';

//템플릿 참조 
const recentMoodChart = ref(null);

//범위 선택 select 요소 템플릿 참조
const selectDailyRange = ref(null);

const chartInstance = ref(null);

const authManager = useAuthManagerStore();

const diaryManager = useDiaryManagerStore();

const router = useRouter();

onMounted(()=>{
  drawRecentDepressionChart();
});

async function drawRecentDepressionChart() {
  const data = await diaryManager.getRecentDailyAvgDepressionLevel(selectDailyRange.value.value);
  
  //key(날짜) 기준으로 정렬 
  const labels = Object.keys(data).sort();

  //값들도 정렬된 key를 사용해서 매핑
  const values = labels.map((key) => data[key]);
  
  drawDepressionChart(labels, values);
}

function drawDepressionChart(labels, data) {

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
              borderColor: "rgba(75, 192, 192, 1)",
              backgroundColor: "rgba(75, 192, 192, 0.2)",
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
                text: "우울 수치",
              },
              min: 0.0,
              max: 70.0,
            },
          },
        },
      });

}

function onChanged() {
  drawRecentDepressionChart();
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
        height: 60%;
    }

    .card .btn {
        margin: 0px;
        width: 50%;
    }

    .ex-link { 
      margin: 2rem;
    }

</style>