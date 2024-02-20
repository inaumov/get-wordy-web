<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";
import {rollDice} from "@/js/cards.js";

export default {
  components: {ActionButtons},
  props: ['dictionaryId'],
  data() {
    return {
      cards: []
    }
  },
  methods: {
    roll_dice: rollDice
  },
}

</script>

<template>

  <ActionButtons/>

  <div class="container text-center" id="play-game-panel">
    <p class="mb-3">You wanna play? let's play</p>
    <div class="d-flex align-items-center pb-5">
      <div class="col">
        <a href="#" class="btn btn-default btn-lg" @click="roll_dice(this.dictionaryId)">
          <i class="bi bi-dice-5"></i> Roll Dice
        </a>
      </div>
    </div>
  </div>

</template>
