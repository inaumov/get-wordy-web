<script>
import {store} from '@/js/store.js';

export default {
  props: ['vocabId'],
  setup: function () {
    return {
      limitSettings: store
    }
  },
  methods: {
    print_cards: function () {
      this.$emit("nextStep", 'CardsList');
    }
  },
  mounted() {
    console.log('PlayGame view: 1st step mounted. vocabId = ', this.vocabId);
  }
}
</script>

<template>
  <div class="container text-center" id="play-game-panel">
    <p class="mb-3">
      You wanna play? let's play
    </p>
    <button v-on:click="print_cards" class="btn btn-sm btn-secondary text-white">
      <i class="bi bi-arrow-right-square"></i> Pick cards
    </button>
  </div>
</template>

<style>

</style>
