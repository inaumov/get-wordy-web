<script>
import {deleteCard, resetProgress} from "@/js/cards.js";
import {toReadableStatus} from "@/js/utils.js";

export default {
  props: ['vocabId', 'cards'],
  data() {
    return {
      // cards: []
    }
  },
  methods: {
    toReadableStatus,
    canBeReset: function (card) {
      return card.status === 'POSTPONED' || card.status === 'LEARNT';
    },
    resetScore(card) {
      const wordId = card['wordId'];
      resetProgress(this.vocabId, wordId)
          .then(response => {
            if (response.ok) {
              card.score = 0;
              card.status = '';
            }
            console.log("PUT reset score has been requested. Response.status =", response.status);
          });
    },
    deleteCard(card) {
      const wordId = card['wordId'];
      deleteCard(this.vocabId, wordId)
          .then(response => {
            if (response.ok) {
              const index = this.cards.findIndex(obj => obj['wordId'] === wordId)
              this.cards.splice(index, 1)
            }
            console.log("DELETE card has been requested. Response.status =", response.status);
          });
    },
  }
}

</script>

<template>
  <div class="container p-4" id="cards-table-panel">
    <table class="table">
      <thead>
      <tr>
        <th>Word</th>
        <th>Transcription</th>
        <th>Meaning</th>
        <th>Status</th>
        <th>Score</th>
        <th>In Context</th>
        <th>Collocations</th>
        <th style="text-align: right">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="card in cards">
        <td>{{ card.value }} ({{ card.explanation.partOfSpeech }})</td>
        <td>{{ card.transcription }}</td>
        <td>{{ card.explanation.meaning }}</td>
        <td class="text-nowrap">{{ toReadableStatus(card.status) }}</td>
        <td>{{ card.score }}</td>
        <td>
          <ul class="list-unstyled" v-if="card.explanation.inContext">
            <li v-for="sentence in card.explanation.inContext">
              {{ sentence }}
            </li>
          </ul>
        </td>
        <td>
          <ul class="list-unstyled" v-if="card.explanation.collocations">
            <li v-for="collocation in card.explanation.collocations">
              {{ collocation }}
            </li>
          </ul>
        </td>
        <td style="text-align: right">
          <div id="actions">
            <button class="btn btn-lg" @click="resetScore(card)" v-if="canBeReset(card)" title="Reset score">
              <i class="bi bi-arrow-repeat"></i>
            </button>
            <button class="btn btn-lg" @click="deleteCard(card)" title="Delete">
              <i class="bi bi-x-lg"></i>
            </button>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>

table #actions {
  position: relative;
  display: inline-flex;
}

table #actions .btn {
  padding: 0 5px !important;
}

</style>
