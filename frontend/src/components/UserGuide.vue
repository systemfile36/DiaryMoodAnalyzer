<template>
<div id = headerDiv>
  <div id = "user_guide_image">
    <img src="../assets/images/loner_image.png"/>
  </div>

  <div id = "user_guide_description">
    <h2 class = "user_guide_title">이용 안내</h2>
    <h5 class = "diaryPage_clickLink" @click="goToDiaryPage" >다이어리 작성 페이지 이동 (클릭)</h5>
    <h5 class = "user_guide_content">1. 다이어리 작성 후에 감정 분석을 진행해 주세요.</h5>
    <h5 class = "user_guide_content">2. 다이어리를 작성 후 상담을 신청해주세요.</h5>
    <h5 class = "user_guide_content">- 신청 시 다이어리 내역과 분석 결과가 전문가에게 제공됩니다.</h5>
    <h5 class = "user_guide_content">3. 상담이 확정되면 지정된 장소로 방문하여 상담을 받으시면 됩니다.</h5>
  </div>

  <div id = "user_guide_target">
    <h2 class = "user_guide_title">지원 대상</h2>
    <h5 class = "user_guide_content">- 진주시 거주 은둔형외톨이 당사자 및 가족</h5>
    <h5 class = "user_guide_content">- 연령제한 없음</h5>
    <h2 class = "user_guide_title">문의</h2>
    <h5 class = "user_guide_content">- 전화 : 010-0000-0000</h5>
    <h5 class = "user_guide_content">- 카카오톡 : @test1111</h5>
    <h5 class = "user_guide_content">- email : test@gmail.com</h5>
  </div>
</div>

<div id ="footerDiv">
    <div id = "destination_text">
      <h2 style="font-weight: bolder">오시는 길</h2>
      <h5>경상남도 진주시 진주대로 501</h5>
      <h5>경상국립대학교 심리상담센터</h5>
    </div>
    <div id = "map_div">
      <div id="naverMap" style="width: 100%; height: 100%;"></div>
    </div>
</div>

</template>

<script>
export default {
  mounted() {
    this.loadNaverMap();
  },
  methods: {
    loadNaverMap() {
      const script = document.createElement("script");
      script.src = `https://openapi.map.naver.com/openapi/v3/maps.js?ncpClientId=zcc8z9vig1`;
      script.onload = () => {
        // 스크립트 로드 후 지도 생성
        const map = new naver.maps.Map("naverMap", {
          center: new naver.maps.LatLng(35.154542, 128.098707), // 경상국립대 가좌캠퍼스 중심 좌표
          zoom: 16,
        });

        // 마커 추가
        new naver.maps.Marker({
          position: new naver.maps.LatLng(35.154542, 128.098707),
          map: map,
          title: "경상국립대학교 심리상담센터",
        });
      };
      document.head.appendChild(script);
    },
    goToDiaryPage() {
      this.$router.push({name: 'WriteDiary'})
          .catch(err => {
            console.error('라우팅 에러:', err);
          });
    }
  }
}
</script>

<style>

#headerDiv{
  width: 100%;
  height: 48vh;
  background: bisque;
}

#footerDiv{
  width: 100%;
  height: 45.5vh;
}

#user_guide_image{
  position: absolute;
  top: 10%;
  width : 500px;
  height : 330px;
  margin-top: 20px;
  margin-left: 20px;
  border: 2px solid black;
  border-radius: 20px;
  background: coral;
}

#user_guide_image img{
  width: 500px;
  height: 350px;
}


#user_guide_description{
  position: absolute;
  top: 10%;
  left: 41%;
  width : 550px;
  height : 330px;
  border: 1px solid black;
  border-radius: 30px;
  margin-left: 20px;
  padding: 20px;
  margin-top: 20px;
  background: white;
}

#user_guide_target {
  position: absolute;
  top: 10%;
  left: 71%;
  width : 500px;
  height : 330px;
  border: 1px solid black;
  border-radius: 30px;
  margin-left: 20px;
  padding: 20px;
  margin-top: 20px;
  background: white;
}

.user_guide_title{
  font-weight: bolder;
  margin-bottom: 10px;
  color: orange;
}

.diaryPage_clickLink{
  font-weight: bolder;
  margin-top: 20px;
  margin-bottom: 20px;
  cursor: pointer;
}

.user_guide_content{
  margin-top: 15px;
  margin-bottom: 15px;
}

#destination_text{
  position: absolute;
  top: 54%;
  width : 800px;
  height : 415px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: orange;
}

#map_div{
  position: absolute;
  top: 54%;
  left: 55%;
  width : 860px;
  height : 415px;
}


</style>
