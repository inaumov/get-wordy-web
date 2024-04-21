<script>

export default {
  props: ['nextAction', 'result'],
}
</script>

<template>
  <div class="col pb-5 text-center">
    <p class="text-center mb-4">
      {{ result[0] }}
    </p>
    <p class="text-center mb-4">
      Your answer is {{ result[1] }} out of {{ result[2] }}
    </p>
    <div class="col pb-5 text-center">
      <button type="button" class="btn btn-lg" v-on:click="nextAction.onAction()">
        <i class="bi bi-box-arrow-right"></i> {{ nextAction['caption'] }}
      </button>
    </div>
  </div>
</template>
