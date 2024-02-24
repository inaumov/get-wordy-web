<script>
import {toReadableStatus} from "@/js/cards.js";

export default {
  props: ['cards'],
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
    resetStatus(id) {
      this.$emit('resetStatus', id)
    },
    deleteCard(id) {
      this.$emit('deleteCard', id)
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
          <ul class="list-unstyled" v-if="card.contexts">
            <li v-for="context in card.contexts">
              {{ context }}
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
            <button class="btn btn-lg" @click="resetStatus(card.id)" v-if="canBeReset(card)">
              <i class="bi bi-arrow-repeat"></i>
            </button>
            <button class="btn btn-lg" @click="deleteCard(card.id)">
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
