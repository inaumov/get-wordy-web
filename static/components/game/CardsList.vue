<script>
import {fetchCardsForExercise} from "@/js/cards.js";

export default {
  props: ['dictionaryId'],
  data() {
    return {
      cards: [{
        word: {
          value: '',
          meaning: ''
        }
      }],
      limit: 5, // default for now
    }
  },
  methods: {
    async getData() {
      const response = await fetchCardsForExercise(this.dictionaryId, this.limit);
      this.cards = await response.json();
    },
    exercise: function () {
      this.$emit("nextStep", 'Exercise');
    }
  },
  mounted() {
    this.getData();
    console.log('PlayGame view: 3rd step mounted. dictionaryId = ', this.dictionaryId);
  }
}
</script>

<template>
  <div class="container" id="game-cards-list">
    <p class="mb-4 text-center">
      Read carefully the following definitions and prepare yourself to exercise
    </p>
    <div class="row row-cols-5 gx-5 gy-2">
      <div class="col" v-for="card in cards">
        <div class="card">
          <div class="card-body">
            <h4 class="card-title">{{ card.word.value }}</h4>
            <p class="card-text">{{ card.word.meaning }}</p>
          </div>
        </div>
      </div>
    </div>
    <div class="col pb-5 text-center">
      <a href="#" class="btn btn-default btn-lg" @click="exercise()">
        <i class="bi bi-arrow-right-square"></i> Exercise
      </a>
    </div>
  </div>
</template>

<style scoped>

div#game-cards-list.card {
  padding: 75px;
  margin-top: -40px;
  margin-bottom: -40px;
  transition: 0.4s ease;
  cursor: pointer;
  height: auto;
}
</style>
