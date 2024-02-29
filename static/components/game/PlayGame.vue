<script>
import {store} from '@/js/store.js';

export default {
  props: ['dictionaryId'],
  setup: function () {
    return {
      limitSettings: store
    }
  },
  methods: {
    roll_dice: function () {
      this.$emit("nextStep", 'RollDice');
    },
    print_cards: function () {
      this.$emit("nextStep", 'CardsList');
    }
  },
  mounted() {
    console.log('PlayGame view: 1st step mounted. dictionaryId = ', this.dictionaryId);
  }
}
</script>

<template>
  <div class="container text-center" id="play-game-panel" v-if="limitSettings.exerciseLimitSelection === 'ROLL_DICE'">
    <p class="mb-3">
      You wanna play? let's play
    </p>
    <div class="col pb-5">
      <a href="#" class="btn btn-default btn-lg" @click="roll_dice()">
        <i class="bi bi-dice-5"></i> Roll Dice
      </a>
    </div>
  </div>
  <div class="container text-center" id="play-game-panel" v-else-if="limitSettings.exerciseLimitSelection === 'DEFAULT_LIMIT'">
    <p class="mb-3">
      You wanna play? let's play
    </p>
    <div class="col pb-5">
      <button v-on:click="print_cards" class="btn btn-default btn-lg">
        <i class="bi bi-arrow-right-square"></i> Pick cards
      </button>
    </div>
  </div>
</template>
