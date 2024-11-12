<template>
  <!-- card 사용 -->
  <div class="card m-3 p-4 shadow-sm">
    <div class="card-header">
      <h2 class="display-5 mb-2">상담 내역 작성</h2>
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
          <button type="button" class="btn btn-outline-primary w-100 mt-3">상담 내역 작성</button>
        </div>
      </form>
    </div>

  </div>

</template>
<script setup>

</script>
<style lang="scss">

</style>
