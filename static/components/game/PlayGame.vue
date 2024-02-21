<script>

export default {
  methods: {
    roll_dice: function () {
      this.$emit("nextStep", 'RollDice');
    }
  },
  mounted() {
  }
}
</script>

<template>
  <div class="container text-center" id="play-game-panel">
    <p class="mb-3">You wanna play? let's play</p>
    <div class="col pb-5">
      <a href="#" class="btn btn-default btn-lg" @click="roll_dice()">
        <i class="bi bi-dice-5"></i> Roll Dice
      </a>
    </div>
  </div>
</template>
