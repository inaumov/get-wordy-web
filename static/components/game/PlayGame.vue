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
  <div class="container text-center" id="play-game-panel">
    <p class="mb-3">
      You wanna play? let's play
    </p>
    <div class="col pb-5" v-if="limitSettings['exerciseLimitSelection'] === 'ROLL_DICE'">
      <button class="btn btn-lg" v-on:click="roll_dice()">
        <i class="bi bi-dice-5"></i> Roll Dice
      </button>
    </div>
    <div class="col pb-5" v-else-if="limitSettings['exerciseLimitSelection'] === 'DEFAULT_LIMIT'">
      <button v-on:click="print_cards" class="btn btn-lg">
        <i class="bi bi-arrow-right-square"></i> Pick cards
      </button>
    </div>
  </div>
</template>

<style>
div#play-game-panel i {
  color: rgb(185, 87, 84)
}

</style>
