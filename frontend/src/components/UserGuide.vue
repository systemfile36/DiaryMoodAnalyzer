<template>
  <div class="userGuide-top d-lg-flex align-items-center">
    <div class="userGuide-image d-flex">
      <img src="../assets/images/loner_image.png"/>
    </div>

    <div class="userGuide-description d-flex flex-column">
      <h2 class = "user_guide_title">이용 안내</h2>
      <h5>1. 다이어리 작성 후에 감정 분석을 진행해 주세요.</h5>
      <h5>2. 다이어리를 작성 후 상담을 신청해주세요.</h5>
      <h5>3. 상담이 확정되면 지정된 장소로 방문하여 상담을 받으시면 됩니다.</h5>
    </div>

    <div class="userGuide-target d-flex flex-column">
      <h2 class = "user_guide_title">지원 대상</h2>
      <h5>- 진주시 거주 은둔형외톨이 당사자 및 가족</h5>
      <h5>- 연령제한 없음</h5>
      <h2 class = "user_guide_title">문의</h2>
      <h5>- 전화 : 010-9480-2749</h5>
      <h5>- email : chan941027@gnu.ac.kr</h5>
    </div>
  </div>

  <div class="userGuide-bottom d-lg-flex mt-3 align-items-center">
      <div class = "destination-description d-flex flex-column align-items-center justify-content-center">
        <h2 style="font-weight: bolder">오시는 길</h2>
        <h5>경상남도 진주시 진주대로 501</h5>
        <h5>경상국립대학교 심리상담센터</h5>
      </div>
      <div class = "destination-map d-flex">
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
    }
  }
}
</script>

<style>

.userGuide-top{
  width: 100%;
  height: 50vh;
}

.userGuide-image{
  width: 30%;
  height: 30vh;
  margin: 2rem;
  border: 2px solid black;
  border-radius: 10px;
}

.userGuide-description{
  width: 30%;
  height: 30vh;
  margin: 2rem;
  padding: 10px;
  border: 1px solid black;
  border-radius: 10px;
}

.userGuide-target {
  width: 30%;
  height: 30vh;
  margin: 2rem;
  padding: 10px;
  border: 1px solid black;
  border-radius: 10px;
}

.user_guide_title{
  font-weight: bolder;
  margin-bottom: 10px;
  color: black;
}

.userGuide-bottom{
  width: 100%;
  height: 45vh;
}

.destination-description{
  width: 50%;
  height: 49vh;
  border: 2px solid black;
}

.destination-map{
  width: 50%;
  height: 49vh;
  border: 2px solid black;
}

@media (max-width: 991px) {
  .userGuide-image,
  .userGuide-description,
  .userGuide-target{
    width: 70%;
  }
  .destination-description,
  .destination-map{
    width: 100%;
  }

  .userGuide-top{
    height: 50%;
  }
}

</style>
