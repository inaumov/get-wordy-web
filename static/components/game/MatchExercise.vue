<script>
import {shuffle} from "@/js/utils.js"

export default {
  props: ['cards'],
  data() {
    return {
      totalCards: 0,
      displayed: {
        cardId: '',
        word: {}
      },
      shuffledCards: [],
      nextIndex: 0,
      nextCardNumber: 1,
      isExerciseDone: false,
      correctAnswers: [],
      appraisal: 'Good'
    }
  },
  methods: {
    next: function () {
      if (this.isFinish()) {
        this.finishExercise();
        return;
      }
      this.displayed = this.cards[this.nextIndex++];
      this.shuffleCards();
      this.nextCardNumber++;
    },
    onAnswered: function (answered) {
      const isAnswerCorrect = answered['cardId'] === this.displayed.cardId;
      // pre save card id
      if (isAnswerCorrect) {
        this.correctAnswers.push(answered['cardId']);
      }
      highlightAnswer();
      disableAllAnswers(this.cards.map((card) => card['cardId']));
      enableNextButton();

      function highlightAnswer() {
        let elementId = "answerBtn_" + answered['cardId'];
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

      function enableNextButton() {
        let nextCardButton = document.getElementById("nextCardBtn");
        nextCardButton.removeAttribute('disabled');
      }

    },
    shuffleCards() {
      this.shuffledCards = shuffle(this.cards);
    },
    isFinish() {
      return this.nextIndex >= this.totalCards;
    },
    setAppraisalCaption() {
      if (this.correctAnswers.length === this.totalCards) {
        this.appraisal = 'Excellent';
      } else if (this.correctAnswers.length === this.totalCards - 1) {
        this.appraisal = 'Great';
      } else if (this.correctAnswers.length === 0) {
        this.appraisal = 'Pity';
      }
    },
    finishExercise() {
      this.isExerciseDone = true;
      this.setAppraisalCaption();
    },
    nextExercise: function () {
      this.$emit("nextStep", 'UnscrambleExercise', this.cards);
    }
  },
  mounted() {
    this.displayed = this.cards[this.nextIndex];
    this.shuffleCards();
    this.nextIndex++;
    this.totalCards = this.cards.length;
  }
}
</script>

<template>
  <div class="container" id="match-exercise" :key="displayed.cardId" v-if="!isExerciseDone">
    <p class="text-center mb-3">
      Exercise 1: Match the meaning with a word
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
        <div class="col" v-for="card in shuffledCards">
          <button class="btn btn-lg border-0" v-on:click="onAnswered(card)"
                  v-bind:id="`answerBtn_${card['cardId']}`">
            {{ card.word['value'] }}
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
  <div class="col pb-5 text-center" v-else-if="isExerciseDone">
    <p class="text-center mb-4">
      {{ appraisal }}
    </p>
    <p class="text-center mb-4">
      Your answer is {{ correctAnswers.length }} out of {{ cards.length }}
    </p>
    <div class="col pb-5 text-center">
      <button type="button" class="btn btn-lg" v-on:click="nextExercise()">
        <i class="bi bi-fast-forward-circle"></i> Roll on
      </button>
    </div>
  </div>
</template>

<style scoped>

div#match-exercise .card {
  background: rgba(255, 115, 0, 0.10);
}

div#match-exercise button i {
  color: rgb(185, 87, 84)
}

</style>
