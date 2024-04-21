<script>
import ShowResults from "@/components/game/ShowResults.vue";
import Exercise from "./Exercise.vue";
import {shuffle} from "@/js/utils.js";

export default {
  components: {ShowResults},
  extends: Exercise,
  data() {
    return {
      preparedMatches: []
    }
  },
  methods: {
    next: function () {
      this.nextCard();
      this.prepareMatches();
    },
    onAnswered: function (answeredCardId) {
      let currentCardId = this.displayed['cardId'];
      const isAnswerCorrect = answeredCardId === currentCardId;
      // pre save card id
      if (isAnswerCorrect) {
        this.preSaveAnswer(currentCardId);
      }
      highlightAnswer();
      disableAllAnswers(this.cards.map((card) => card['cardId']));
      this.enableNextButton();

      function highlightAnswer() {
        let elementId = "answerBtn_" + answeredCardId;
        let answerBtn = document.getElementById(elementId);
        let icon = document.createElement("i");
        if (isAnswerCorrect) {
          icon.className = "bi bi-check-square-fill";
          icon.style = "color:limegreen"; // green
        } else {
          icon.className = "bi bi-x-square-fill";
          icon.style = "color:crimson"; // red
        }
        answerBtn.prepend(icon, document.createTextNode(" "));
      }

      function disableAllAnswers(cardIds) {
        cardIds.forEach(function (cardId) {
          let button = document.getElementById("answerBtn_" + cardId);
          button.setAttribute('disabled', 'disabled');
        });
      }
    },
    prepareMatches: function () {
      const arr = shuffle(this.cards);
      // simplify to key value
      this.preparedMatches = arr.map(i => ({id: i.cardId, value: i.word.value}));
    },
    nextExercise: function () {
      this.$emit("nextStep", 'UnscrambleExercise', this.cards);
    }
  },
  mounted() {
    this.init();
    this.prepareMatches()
  }
}
</script>

<template>
  <div class="container" id="match-exercise" :key="displayed['cardId']" v-if="!isExerciseDone">
    <p class="text-center mb-3">
      Exercise 1: Match each meaning with its corresponding word
    </p>
    <div class="card mb-4">
      <div class="card-body">
        <p class="text-center mb-3 text-uppercase text-muted">
          Card {{ nextCardNumber }}
        </p>
        <p class="card-text text-center" style="font-size: larger">
          {{ displayed.word['meaning'] }}
        </p>
      </div>
    </div>
    <div class="container text-center mb-4" id="answer-buttons">
      <div class="row justify-content-md-center">
        <div class="col" v-for="answer in preparedMatches">
          <button class="btn btn-lg border-0" v-on:click="onAnswered(answer['id'])"
                  v-bind:id="`answerBtn_${answer['id']}`">
            {{ answer['value'] }}
          </button>
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
      v-bind="{nextAction: {caption: 'Next', onAction: nextExercise}, result: [appraisal, correctAnswers.length, totalCards]}"
      v-else-if="isExerciseDone"/>
</template>

<style scoped>

div#match-exercise .card {
  background: rgba(255, 115, 0, 0.10);
}

div#match-exercise button i {
  color: rgb(185, 87, 84)
}

</style>
