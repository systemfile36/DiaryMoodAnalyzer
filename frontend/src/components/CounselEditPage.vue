<template>
  <!-- card 사용 -->
  <div class="card m-3 p-4 shadow-sm">
    <div class="card-header">
      <h2 class="display-5 mb-2">상담 내역 관리</h2>
      <p>상담자 이름: {{ counselId }}</p>
    </div>
    <div class="card-body">
      <form class="d-flex flex-column">
        <div class="mb-3">
          <label for="InputTitle" class="form-label">Counsel Content Title</label>
          <input
              type="text" class="form-control"
              id="InputTitle"
              maxlength="256">
          <!-- Composable을 통한 에러 메시지-->
          <FormErrorText
              :errors="errors"
              :fieldName="'title'"/>
        </div>
        <div class="mb-3">
          <label for="InputContent" class="form-label">Counsel Content</label>
          <textarea class="form-control" id="InputContent"
                    rows="15" maxlength="1500"></textarea>
          <!-- Composable을 통한 에러 메시지-->
          <FormErrorText
              :errors="errors"
              :fieldName="'content'"/>
        </div>

        <div class="d-flex ">
          <button type="button" class="btn btn-outline-primary w-100 mt-3">상담 내역 수정</button>
        </div>

        <div class="d-flex ">
          <button type="button" class="btn btn-outline-primary w-100 mt-3">상담 내역 삭제</button>
        </div>
      </form>
    </div>

  </div>

</template>
<script>
export default {
  props: ['counselId'], // counselId를 prop으로 받습니다.
  mounted() {
    // 'counselId'를 사용해 필요한 데이터를 가져오거나 화면에 표시할 수 있습니다.
    console.log('상담자 ID:', this.counselId);
  },
};

</script>
<style lang="scss">

</style>
