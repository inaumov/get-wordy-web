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
  mounted() {
    this.cards = fetchCardsForExercise(this.dictionaryId, this.limit);
    console.log('PlayGame view: 3rd step mounted. dictionaryId = ', this.dictionaryId);
  }
}
</script>

<template>
  <div class="container" id="game-cards-list">
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
