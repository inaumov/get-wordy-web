<script>
import {toReadableStatus, deleteCard, resetScore} from "@/js/cards.js";

export default {
  props: ['dictionaryId', 'cards'],
  data() {
    return {
      // cards: []
    }
  },
  methods: {
    toReadableStatus: toReadableStatus,
    canBeReset: function (card) {
      return card.status === 'POSTPONED' || card.status === 'LEARNT';
    },
    resetScore(dictionaryId, card) {
      const cardId = card['cardId'];
      resetScore(dictionaryId, cardId)
          .then(response => {
            if (response.ok) {
              card.score = 0;
              card.status = '';
            }
            console.log("PUT reset score has been requested. Response.status =", response.status);
          });
    },
    deleteCard(dictionaryId, card) {
      const cardId = card['cardId'];
      deleteCard(dictionaryId, cardId)
          .then(response => {
            if (response.ok) {
              const index = this.cards.findIndex(obj => obj['cardId'] === cardId)
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
        <th>Context</th>
        <th>Collocations</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="card in cards">
        <td>{{ card.word.value }} ({{ card.word.partOfSpeech }})</td>
        <td>{{ card.word.transcription }}</td>
        <td>{{ card.word.meaning }}</td>
        <td class="text-nowrap">{{ toReadableStatus(card.status) }}</td>
        <td>{{ card.score }}</td>
        <td>
          <ul class="list-unstyled" v-if="card.sentences">
            <li v-for="sentence in card.sentences">
              {{ sentence }}
            </li>
          </ul>
        </td>
        <td>
          <ul class="list-unstyled" v-if="card.collocations">
            <li v-for="collocation in card.collocations">
              {{ collocation }}
            </li>
          </ul>
        </td>
        <td>
          <div id="actions" class="float-end">
            <button class="btn btn-lg" @click="resetScore(this.dictionaryId, card)" v-if="canBeReset(card)">
              <i class="bi bi-arrow-repeat"></i>
            </button>
            <button class="btn btn-lg" @click="deleteCard(this.dictionaryId, card)">
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
