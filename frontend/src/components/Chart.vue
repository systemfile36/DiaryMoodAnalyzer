<template>
  <div id = "MyChart_pie">
    <canvas
            ref="MyChart_pie"/>
  </div>

  <div id = "MyChart_line">
    <canvas
        ref="MyChart_line"/>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js'
Chart.register(...registerables)

const dataset_mood = [ '기쁨', '우울', '슬픔', '화남']
const dataset_date = [ '11-01', '11-02', '11-03', '11-04', '11-05', '11-06', '11-07', '11-08', '11-09', '11-10']

export default {
  data:() => ({
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

    }
  }

}
</script>

<style>
 #MyChart_pie{
   width : 400px;
   height : 400px;
 }

 #MyChart_line{
   width : 400px;
   height : 400px;
 }
</style>