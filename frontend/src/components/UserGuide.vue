<template>
  <div class="userGuide-top d-lg-flex align-items-center">
    <div class="userGuide-image d-flex">
      <img src="../assets/images/loner_image.png"/>
    </div>

    <div class="userGuide-description d-flex flex-column">
      <h2 class = "user_guide_title">이용 안내</h2>
      <p>1. 매일 또는 필요한 순간에, 기분이나 생각을 기록해 보세요. 일기는 언제든지 수정할 수 있으며, 일기를 저장해두면 나중에 다시 볼 수 있습니다.</p>
      <p>2. 작성한 일기를 저장하면, AI 감정 분석이 자동으로 이루어집니다. AI는 일기에서 감지된 감정을 분석해 감정 분포를 그래프로 제공합니다.</p>
      <p>3. 작성한 일기와 AI 감정 분석 결과는 전문가에게 공유됩니다. 전문가가 이를 확인하고, 코멘트를 달아줄 것입니다.</p>
      <p> - 전문가의 피드백을 통해 자신의 감정을 더욱 잘 이해하고, 필요한 피드백을 받을 수 있습니다.</p>
      <p>4. 좀 더 깊은 상담이 필요하다면, 전문가의 이메일을 통해 상담을 신청할 수 있습니다.</p>
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

<style scoped>

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
  width: 70%;
  height: 30vh;
  margin: 2rem;
  padding: 10px;
  border: 1px solid black;
  border-radius: 10px;
}

p{
  font-size: clamp(8px, 2vw, 16px);  /* 최소 14px, 최대 24px로 글자 크기 자동 조정 */
  font-weight: bold;
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

  .userGuide-top{
    margin-top: 300px;
  }
  .userGuide-image,
  .userGuide-description{
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
