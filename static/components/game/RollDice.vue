<script>

export default {
  props: ['dictionaryId'],
  data() {
    return {
      die1: 0,
      die2: 0,
      total: 5
    }
  },
  mounted() {
    this.die1 = this.roll();
    this.die2 = this.roll();
    this.total = this.die1 + this.die2;
    console.log('PlayGame view: 2nd step mounted. dictionaryId = ', this.dictionaryId);
  },
  methods: {
    roll() {
      const min = 1;
      const max = 6;
      return Math.floor(Math.random() * (max - min + 1)) + min;
    },
    switchClass(n) {
      return `bi bi-dice-${n} h2`
    },
    re_roll: function () {
      this.die1 = this.roll();
      this.die2 = this.roll();
      this.total = this.die1 + this.die2;
    },
    print_cards: function () {
      this.$emit("nextStep", 'CardsList');
    }
  }
}
</script>

<template>
  <div class="container text-center" id="roll-dice-panel">
    <div class="col mb-3">
      <i v-bind:class="switchClass(this.die1)" style="margin-right: 3px"></i>
      <i v-bind:class="switchClass(this.die2)" style="margin-left: 3px"></i>
    </div>
    <p class="mb-3">You Rolled {{ total }}</p>
    <div class="col pb-5">
      <button v-on:click="re_roll" class="btn btn-default btn-lg">
        <i class="bi bi-arrow-repeat"></i> Re roll
      </button>
      <button v-on:click="print_cards" class="btn btn-default btn-lg">
        <i class="bi bi-arrow-right-square"></i> Pick cards
      </button>
    </div>
  </div>
</template>