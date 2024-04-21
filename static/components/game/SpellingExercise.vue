<script>
import ShowResults from "@/components/game/ShowResults.vue";
import {shuffle} from "@/js/utils.js"
import Exercise from "./Exercise.vue";

export default {
  components: {ShowResults},
  extends: Exercise,
  data() {
    return {
      spellingExercise: {},
      answer: '',
    }
  },
  watch: {
    answer: {
      handler(input) {
        let currentCardId = this.displayed['cardId'];
        let allAnswers = new Set([this.displayed.word.value, this.spellingExercise['matchedWords']]);
        if (allAnswers.has(input)) {
          this.highlightAnswer(true);
          this.enableNextButton();
          this.preSaveAnswer(currentCardId);
        } else {
          this.highlightAnswer(false);
        }
      },
      deep: true,
    },
  },
  methods: {
    next: function () {
      this.nextCard();
      this.spellingExercise = this.getRandomSentence(this.displayed);
      this.answer = '';
    },
    getRandomSentence(card) {
      let arr = card['sentences'];
      return shuffle(arr)[0];
    },
    finish: function () {
      this.$emit("nextStep", 'PlayGame');
    }
  },
  mounted() {
    this.init();
    this.spellingExercise = this.getRandomSentence(this.displayed);
  }
}
</script>

<template>
  <div class="container" id="spelling-exercise" :key="displayed.cardId" v-if="!isExerciseDone">
    <p class="text-center mb-3">
      Exercise 3: Write a missing word using proper grammar to complete the sentence
    </p>
    <div class="card mb-4">
      <div class="card-body">
        <p class="text-center mb-3 text-uppercase text-muted">
          Card {{ nextCardNumber }}
        </p>
        <p class="card-text text-center" style="font-size: larger">
          {{ spellingExercise['replacedSentence'] }}
        </p>
      </div>
    </div>
    <div class="container text-center" id="spelling-panel">
      <div class="row justify-content-md-center my-4">
        <div class="col">
          <input class="text-center fs-4 mb-4" id="answer-input" v-model="answer" autocomplete="off"/>
          <p class="text-center fs-4">
            <i id="answer-check-icon" class="bi bi-question-square"></i>
          </p>
        </div>
      </div>
    </div>
    <div class="col pb-5 text-center">
      <button type="button" id="nextCardBtn" class="btn btn-lg" v-on:click="next()" disabled="disabled">
        <i class="bi bi-arrow-right-square"></i> Continue
      </button>
    </div>
  </div>
  <show-results
      v-bind="{nextAction: {caption: 'Finish', onAction: finish}, result: [appraisal, correctAnswers.length, totalCards]}"
      v-else-if="isExerciseDone"/>
</template>

<style scoped>

div#spelling-exercise .card {
  background: rgba(255, 115, 0, 0.10);
}

div#spelling-exercise button i {
  color: rgb(185, 87, 84)
}

</style>
