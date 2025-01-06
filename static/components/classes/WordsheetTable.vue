<script>
import {removeFromVocabulary} from "@/js/classes-api.js";

export default {
  props: ['classId', 'vocabId', 'items'],
  data() {
    return {
      // items: []
    }
  },
  methods: {
    removeWord(word) {
      const wordId = word['wordId'];
      removeFromVocabulary(this.classId, this.vocabId, wordId)
          .then(response => {
            if (response.ok) {
              const index = this.items.findIndex(obj => obj['wordId'] === wordId)
              this.items.splice(index, 1)
            }
            console.log("DELETE a word has been requested. Response.status =", response.status);
          });
    },
  }
}

</script>

<template>
  <div class="p-4">

    <table class="table">
      <thead>
      <tr>
        <th>Word</th>
        <th>Transcription</th>
        <th>Meaning</th>
        <th>In Context</th>
        <th style="text-align: right">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in items">
        <td>{{ item.word.value }} ({{ item.word.partOfSpeech }})</td>
        <td>{{ item.word.transcription }}</td>
        <td>{{ item.word.meaning }}</td>
        <td>
          <ul class="list-unstyled" v-if="item.sentences">
            <li v-for="sentence in item.sentences">
              {{ sentence }}
            </li>
          </ul>
        </td>
        <td style="text-align: right">
          <div id="actions">
            <router-link :to="{name: 'edit-explanation', params: {vocabId: this.vocabId, wordId: item['wordId']}}"
                         class="btn btn-lg" title="Edit word explanation">
              <i class="bi bi-pencil-square"></i>
            </router-link>
            <button class="btn btn-lg" @click="removeWord(item)" title="Delete">
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
