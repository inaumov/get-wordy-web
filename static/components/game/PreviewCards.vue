<script>
import {fetchCardsForExercise} from "@/js/cards.js";
import {store} from "@/js/store.js";

export default {
  props: ['vocabId'],
  setup: function () {
    return {
      limitSettings: store
    }
  },
  data() {
    return {
      cards: []
    }
  },
  methods: {
    async getData() {
      const limit = this.limitSettings['exerciseLimit'];
      const response = await fetchCardsForExercise(this.vocabId, limit);
      this.cards = await response.json();
    },
    start: function () {
      this.$emit("nextStep", 'MatchExercise', this.cards);
    }
  },
  mounted() {
    this.getData();
  }
}
</script>

<template>
  <div class="container" id="game-cards-list" v-if="cards.length > 0">
    <p class="mb-4 text-center">
      Read carefully the following definitions and prepare yourself to exercise
    </p>
    <div class="row justify-content-md-center mb-4">
      <div class="col" v-for="card in cards">
        <div class="card">
          <div class="card-body">
            <h4 class="card-title">{{ card.value }}</h4>
            <p class="card-text">{{ card.explanation.meaning }}</p>
          </div>
        </div>
      </div>
    </div>
    <div class="col pb-5 text-center">
      <button type="button" class="btn btn-sm btn-secondary text-white" v-on:click="start()">
        <i class="bi bi-arrow-right-square"></i> Start
      </button>
    </div>
  </div>
  <div v-else>
    <p class="mb-4 text-center">
      No cards available at this time
    </p>
  </div>
</template>

<style>

div#game-cards-list .card {
  background: rgba(255, 115, 0, 0.10);
}

</style>
