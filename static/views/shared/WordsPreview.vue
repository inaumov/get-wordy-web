<script>
import {fetchWordsheet} from "@/js/classes-api.js";
import {applyCaption} from "@/js/utils.js";

export default {
  props: ['classId', 'wordsheetId'],
  data() {
    return {
      name: 'Wordsheet',
      words: [],
      isAddedToLearn: false,
    }
  },
  methods: {
    async getData() {
      const response = await fetchWordsheet(this.classId, this.wordsheetId);
      const wordsheet = await response.json();
      this.name = wordsheet['name'];
      this.words = wordsheet['items'] || [];
    },
    remove(item) {
      this.$emit('stop-to-learn', item);
      this.isAddedToLearn = false;
    },
    add(item) {
      this.$emit('add-to-learn', item);
      this.isAddedToLearn = true;
    },
  },
  mounted() {
    this.getData()
    applyCaption(this.name || 'Wordsheet')
    console.log("Selected wordsheet: id = ", this.wordsheetId, ", name = ", this.name)
  }
};
</script>

<template>
  <div class="p-4">
    <div v-if="words" class="word-list">
      <h4 class="pb-4" style="vertical-align: middle;">{{this.name}}</h4>

      <div class="row">
        <div class="col">
          <div class="d-flex flex-column align-items-end">
            <button class="btn btn-lg" v-on:click="window.print()" title="Download word sheet">
              <i class="bi bi-filetype-pdf"></i>
            </button>
          </div>
        </div>
      </div>

      <div v-for="item in words" class="word-card">
        <div class="word-info">
          <p><strong>Word:</strong> {{ item.word.value }} ({{ item.word.partOfSpeech }})</p>
          <p><strong>Transcription:</strong> {{ item.word.transcription }}</p>
          <p><strong>Meaning:</strong> {{ item.word.meaning }}</p>
          <div v-if="item.sentences && item.sentences.length > 0" class="sentences">
            <strong>In Context:</strong>
            <ul>
              <li v-for="(sentence, index) in item.sentences" :key="index">
                {{ sentence }}
              </li>
            </ul>
          </div>
          <div v-if="item.collocations && item.collocations.length > 0" class="collocations">
            <strong>Collocations:</strong>
            <ul>
              <li v-for="(collocation, index) in item.collocations" :key="index">
                {{ collocation }}
              </li>
            </ul>
          </div>
        </div>
        <div class="actions">
          <button v-if="isAddedToLearn" class="btn remove-btn" @click="remove(item)" title="Stop learning">
            <i class="bi bi-journal-minus"></i>
            Stop learning
          </button>
          <button v-else class="btn add-btn" @click="add(item)" title="Add to learn">
            <i class="bi bi-journal-plus"></i>
            Pick to learn
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.word-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.word-card {
  border: 1px solid #ddd;
  padding: 1rem;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.word-info {
  margin-bottom: 1rem;
}

.sentences ul {
  list-style-type: disc;
  padding-left: 1.5rem;
  margin: 0.5rem 0;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  margin-top: auto; /* ensures actions stay at the bottom */
}

.actions .btn {
  padding: 0 5px !important;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  transition: color 0.3s, background-color 0.3s; /* smooth hover effects */
}

.actions .btn:hover {
  background: none;
}
</style>
