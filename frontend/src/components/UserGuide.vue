<template>
  <h3>이용 안내</h3>

  <div id = "user_guide_image">
    <h2>이미지</h2>
  </div>

  <div id = "user_guide_description">
    <h2 class = "user_guide_title">이용 안내</h2>
    <h5>웹사이트 이름 이용방법을 안내드립니다.</h5>
    <h5 class = "user_guide_txt">자세히</h5>
  </div>

  <div id = "directions">
    <div id = "directions_text">
      <h2>오시는 길</h2>
      <h5>경상남도 진주시 진주대로 501</h5>
      <h5>경상국립대하교 심리상담센터</h5>
    </div>
    <div id = "kakao_map">
    </div>
  </div>

</template>

<script>
export default {
}
</script>

<style>

#user_guide_image{
  position: absolute;
  width : 650px;
  height : 350px;
  border: 1px solid black;
  border-radius: 30px;
  margin-top: 20px;
  margin-left: 20px;
}

#user_guide_description{
  position: absolute;
  top: 10%;
  left: 55%;
  width : 650px;
  height : 350px;
  border: 1px solid black;
  border-radius: 30px;
  margin-left: 20px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.user_guide_title{
  font-weight: bolder;
}

.user_guide_txt{
  padding: 10px;
  font-weight: bolder;
}

#directions_text{
  position: absolute;
  top: 55%;
  width : 800px;
  height : 400px;
  border: 1px solid black;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

#kakao_map{
  position: absolute;
  top: 55%;
  left: 55%;
  width : 860px;
  height : 400px;
  border: 1px solid black;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}


</style>
