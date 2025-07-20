<script>
import {shuffle} from "@/js/utils.js";
import {saveProgress} from "@/js/cards.js";

export default {
  name: 'base-exercise',
  props: ['vocabId', 'cards'],
  data() {
    return {
      totalCards: 0,
      displayed: {
        wordId: '',
        explanation: {}
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
    init: function () {
      this.totalCards = this.cards.length;
      this.shuffleCards();
      this.displayed = this.shuffledCards[this.nextIndex];
      this.nextIndex++;
    },
    nextCard: function () {
      if (this.isFinish()) {
        this.finishExercise();
        return;
      }
      this.displayed = this.shuffledCards[this.nextIndex++];
      this.nextCardNumber++;
    },
    shuffleCards() {
      this.shuffledCards = shuffle(this.cards);
    },
    preSaveAnswer(wordId) {
      // pre save word id
      if (this.correctAnswers.indexOf(wordId) === -1) {
        this.correctAnswers.push(wordId);
      }
    },
    highlightAnswer(isAnswerCorrect) {
      let icon = document.getElementById("answer-check-icon");
      if (isAnswerCorrect) {
        icon.className = "bi bi-check-square-fill";
        icon.style = "color:limegreen"; // green
      } else {
        icon.className = "bi bi-x-square-fill";
        icon.style = "color:crimson"; // red
      }
    },
    enableNextButton() {
      let nextCardButton = document.getElementById("nextCardBtn");
      nextCardButton.removeAttribute('disabled');
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
      if (this.correctAnswers === 0) {
        return;
      }
      saveProgress(this.vocabId, this.correctAnswers)
          .then(response => {
            if (!response.ok) {
              // todo notification
            }
            this.isExerciseDone = true;
            this.setAppraisalCaption();
            console.log("Submitting exercise results. Response.status =", response.status);
          });
    },
  },
}
</script>
