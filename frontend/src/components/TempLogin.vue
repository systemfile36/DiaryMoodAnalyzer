<template>
  <div class="tempLogin-menu d-flex">
    <h4 class="menu-content" style="cursor: pointer" @click="goToSignIn">로그인</h4>
    <h4 class="menu-content" style="cursor: pointer" @click="goToSignUp">회원가입</h4>
  </div>

  <div class="tempLogin-main d-lg-flex">
    <div class="tempLogin-introduction d-flex flex-column">
      <h1 class = "user_guide_title">오늘도</h1>
      <h1 class = "user_guide_title">우리 이웃의 안녕을 묻습니다.</h1>
      <h4>"오늘의 기분은 어떠세요?"</h4>
      <h4>"오늘 햇살은 참 좋더라구요"</h4>
      <h4>사소한 안부 인사로 우리 이웃의 안녕을 묻는</h4>
      <h3>광주광역시 외톨이지원센터입니다.</h3>
    </div>

    <div id="carouselExampleAutoplaying" class="carousel slide" data-bs-ride="carousel">
      <div class="carousel-inner">
        <div class="carousel-item active">
          <img src="../assets/images/loner_image_1.png" class="d-block w-100" alt="image_1">
        </div>
        <div class="carousel-item">
          <img src="../assets/images/loner_image_2.png" class="d-block w-100" alt="image_2">
        </div>
        <div class="carousel-item">
          <img src="../assets/images/loner_image_3.png" class="d-block w-100" alt="image_3">
        </div>
        <div class="carousel-item">
          <img src="../assets/images/loner_image_4.png" class="d-block w-100" alt="image_4">
        </div>
        <div class="carousel-item">
          <img src="../assets/images/loner_image_5.png" class="d-block w-100" alt="image_5">
        </div>
        <div class="carousel-item">
          <img src="../assets/images/loner_image_6.png" class="d-block w-100" alt="image_6">
      </div>
        <div class="carousel-item">
          <img src="../assets/images/loner_image_7.png" class="d-block w-100" alt="image_7">
      </div>
      </div>
    </div>
  </div>

  <UserGuide />

</template>

<script>

import UserGuide from "./UserGuide.vue";

export default {
  components : {
    UserGuide
  },
  mounted() {
    this.$nextTick(() => {
      const carouselElement = document.querySelector('#carouselExampleAutoplaying');
      if (carouselElement) {
        new bootstrap.Carousel(carouselElement, {
          interval: 1000,
          ride: 'carousel', // 자동 재생 활성화
        });
      }
    })
  },
  methods: {
    goToSignIn(){
      this.$router.push('/signin')
          .catch(err => {
            console.error('라우팅 에러:', err);
          });
    },
    goToSignUp(){
      this.$router.push('/SignUp')
          .catch(err => {
            console.error('라우팅 에러:', err);
          });
    }
  }
}
</script>

<style>

.tempLogin-main {
  width: 100%;
  height: 70vh;
}

.tempLogin-introduction {
  width: 50%;
  height: 50vh;
  margin: 4rem;
  padding: 10px;
}

#carouselExampleAutoplaying {
  width: 50%;
  height: 50vh;
  margin: 4rem;
}

@media (max-width: 991px) {
  .tempLogin-introduction,
  #carouselExampleAutoplaying
  {
    width: 100%;
  }
}

.tempLogin-menu{
  float: right;
  margin: 1rem;
  gap: 2rem;
}

.menu-content{
  font-weight: bold;
  margin: 1rem;
}

.menu-content:hover {
  color: blue;
}

</style>
