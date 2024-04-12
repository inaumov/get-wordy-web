<script>
import {submitResultForExercise} from "@/js/cards.js"
import {shuffle} from "@/js/utils.js"

export default {
  props: ['dictionaryId', 'cards'],
  data() {
    return {
      totalCards: 0,
      displayed: {
        cardId: '',
        word: {}
      },
      aSentenceChunks: '',
      nextIndex: 0,
      nextCardNumber: 1,
      isExerciseDone: false,
      answer: '',
      correctAnswers: [],
      appraisal: 'Good'
    }
  },
  watch: {
    answer: {
      handler(input) {
        if (input.length === this.displayed.word.value.length || input.length === this.aSentenceChunks['matchedWord'].length) {
          console.log("All input letters:", input);
          let isAnswerCorrect = (input === this.displayed.word.value || input === this.aSentenceChunks['matchedWord']);
          // pre save card id
          if (isAnswerCorrect) {
            this.correctAnswers.push(this.displayed['cardId']);
          }
          enableNextButton();
          highlightAnswer(isAnswerCorrect);

          function enableNextButton() {
            let nextCardButton = document.getElementById("nextCardBtn");
            nextCardButton.removeAttribute('disabled');
          }

          function highlightAnswer(isAnswerCorrect) {
            let icon = document.getElementById("answer-check-icon");
            if (isAnswerCorrect) {
              icon.className = "bi bi-check-square-fill";
              icon.style = "color:limegreen"; // green
            } else {
              icon.className = "bi bi-x-square-fill";
              icon.style = "color:crimson"; // red
            }
          }

        }
      },
      deep: true,
    },
  },
  methods: {
    next: function () {
      if (this.isFinish()) {
        this.finishExercise();
        return;
      }
      this.displayed = this.cards[this.nextIndex++];
      const aSentence = this.getRandomSentence(this.displayed);
      this.aSentenceChunks = this.splitByClosestMatch(aSentence)
      this.answer = '';
      this.nextCardNumber++;
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
      submitResultForExercise(this.dictionaryId, this.correctAnswers)
          .then(response => {
            if (!response.ok) {
              // todo notification
            }
            this.isExerciseDone = true;
            this.setAppraisalCaption();
            console.log("Submitting exercise results. Response.status =", response.status);
          });
    },
    getRandomSentence(card) {
      let arr = card['sentences'];
      return shuffle(arr)[0];
    },
    splitByClosestMatch(sentence) {
      const fullSentence = sentence['fullSentence'];
      const matchedWord = sentence['matchedWord'];

      const chunks = fullSentence.split(matchedWord);
      const beforeMatch = chunks[0];
      const afterMatch = chunks.slice(1).join(matchedWord);
      // Replace the matched phrase with non-breaking spaces in the original sentence
      const replacedSentence = fullSentence.replace(new RegExp(matchedWord, 'i'), "_".repeat(matchedWord.length));

      return {
        originalSentence: fullSentence,
        beforeMatch: beforeMatch,
        matchedWord: matchedWord,
        afterMatch: afterMatch,
        replacedSentence: replacedSentence
      };
    }
  },
  mounted() {
    this.displayed = this.cards[this.nextIndex];
    const aSentence = this.getRandomSentence(this.displayed);
    this.aSentenceChunks = this.splitByClosestMatch(aSentence)
    this.nextIndex++;
    this.totalCards = this.cards.length;
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
          {{ aSentenceChunks.replacedSentence }}
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
  <div class="col pb-5 text-center" v-else-if="isExerciseDone">
    <p class="text-center mb-4">
      {{ appraisal }}
    </p>
    <p class="text-center mb-4">
      Your answer is {{ correctAnswers.length }} out of {{ cards.length }}
    </p>
  </div>
</template>

<style scoped>

div#spelling-exercise .card {
  background: rgba(255, 115, 0, 0.10);
}

div#spelling-exercise button i {
  color: rgb(185, 87, 84)
}

</style>
