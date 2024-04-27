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
    resetScore(card) {
      const cardId = card['cardId'];
      resetScore(this.dictionaryId, cardId)
          .then(response => {
            if (response.ok) {
              card.score = 0;
              card.status = '';
            }
            console.log("PUT reset score has been requested. Response.status =", response.status);
          });
    },
    deleteCard(card) {
      const cardId = card['cardId'];
      deleteCard(this.dictionaryId, cardId)
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
        <th style="text-align: right">Actions</th>
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
        <td style="text-align: right">
          <div id="actions">
            <button class="btn btn-lg" @click="resetScore(card)" v-if="canBeReset(card)" title="Reset score">
              <i class="bi bi-arrow-repeat"></i>
            </button>
            <router-link :to="{name: 'edit-card', params: {dictionaryId: this.dictionaryId, cardId: card['cardId']}}" class="btn btn-lg" title="Edit card">
              <i class="bi bi-pencil-square"></i>
            </router-link>
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
