<script>

export default {
  props: ['cards'],
  data() {
    return {
      totalCards: 0,
      displayed: {
        cardId: '',
        word: {}
      },
      letters: [],
      nextIndex: 0,
      nextCardNumber: 1,
      isExerciseDone: false,
      answeredLetters: [],
      correctAnswers: [],
      appraisal: 'Good'
    }
  },
  watch: {
    answeredLetters: {
      handler(inputLetters) {
        console.log(inputLetters);
        if (inputLetters.length === this.letters.length) {
          disableAllLetters(this.letters);
          let inputWord = inputLetters
              .join('')
              .replace('\xa0', ' ');
          let isAnswerCorrect = inputWord === this.displayed.word.value;
          // pre save card id
          if (isAnswerCorrect) {
            this.correctAnswers.push(this.displayed['cardId']);
          }
          enableNextButton();
          highlightAnswer(isAnswerCorrect);

          function disableAllLetters(letters) {
            for (let i = 0; i < letters.length; i++) {
              let button = document.getElementById("letterBtn_" + i);
              button.setAttribute('disabled', 'true');
            }
          }

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
      this.answeredLetters = [];
      this.prepareLetters();
      this.nextCardNumber++;
    },
    onClick: function (letter) {
      console.log(letter);
      this.answeredLetters.push(letter);
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
    prepareLetters() {
      let str = this.displayed.word['value'];
      str = str.replace(/\s/g, '\xa0');
      this.letters = [...str].sort(() => Math.random() - 0.5);
    },
  },
  mounted() {
    this.displayed = this.cards[this.nextIndex];
    this.prepareLetters();
    this.nextIndex++;
    this.totalCards = this.cards.length;
  }
}
</script>

<template>
  <div class="container" id="unscramble-exercise" :key="displayed.cardId" v-if="!isExerciseDone">
    <p class="text-center mb-3">
      Exercise 2: Unscramble the word by selecting characters in correct sequence
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
    <div class="container text-center" id="unscramble-panel">
      <div class="row justify-content-md-center">
        <div class="col">
          <button class="btn btn-light btn-lg border border-warning mx-2" v-on:click="onClick(letter)"
                  v-for="(letter, index) in letters"
                  v-bind:id="`letterBtn_${index}`">
            {{ letter }}
          </button>
        </div>
        <div class="row justify-content-md-center my-5">
          <div class="col">
            <p class="text-center fs-4" style="height: 36px" id="answer-output">
              < {{ answeredLetters.join('') }} >
            </p>
            <p class="text-center fs-4">
              <i id="answer-check-icon" class="bi bi-question-square"></i>
            </p>
          </div>
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

div#unscramble-exercise .card {
  background: rgba(255, 115, 0, 0.10);
}

div#unscramble-exercise button i {
  color: rgb(185, 87, 84)
}

</style>
