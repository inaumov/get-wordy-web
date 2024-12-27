<script>
export default {
  props: ['previewData'],
  methods: {
    add(explanation) {
      // transformation logic to go further
      let transformed = {
        word: {
          value: this.previewData.value,
          partOfSpeech: explanation.partOfSpeech,
          transcription: this.previewData.transcription,
          meaning: explanation.meaning
        },
        sentences: explanation.inContext,
        collocations: explanation.collocations
      };
      this.$emit('add-to-wordsheet', transformed);
      console.log('Add to wordsheet clicked for:', transformed);
      this.remove(explanation)
    },
    remove(explanation) {
      const index = this.previewData.explanations.indexOf(explanation);
      if (index !== -1) {
        this.previewData.explanations.splice(index, 1); // remove the item from the list
      }
    },
  },
};
</script>

<template>
  <div class="p-4">
    <div v-if="previewData" class="word-row">
      <div class="word-preview"
           v-for="explanation in previewData.explanations"
           :key="previewData.value"
      >
        <div class="word-info">
          <p><strong>{{ previewData.value }}</strong> ({{ explanation.partOfSpeech }})</p>
          <p>{{ previewData.transcription }}</p>
          <p class="meaning"><strong>Meaning:</strong> {{ explanation.meaning }}</p>
          <div v-if="explanation.inContext && explanation.inContext.length > 0" class="inContext">
            <strong>In Context:</strong>
            <ul>
              <li v-for="(sentence, index) in explanation.inContext" :key="index">
                {{ sentence }}
              </li>
            </ul>
          </div>
        </div>
        <div class="actions">
          <button class="btn btn-lg remove-btn" @click="remove(explanation)" title="Close">
            <i class="bi bi-x-circle close-icon"></i>
          </button>
          <button class="btn btn-lg add-btn" @click="add(explanation)" title="Add to word sheet">
            <i class="bi bi-check-circle add-icon"></i>
          </button>
        </div>
      </div>
    </div>
    <div v-else class="text-center">
      <p>Type any word to get your search preview</p>
    </div>
  </div>
</template>

<style scoped>
.word-row {
  display: flex;
  gap: 1rem;
  justify-content: space-evenly; /* automatic spacing between items */
  width: 100%; /* take full width of the parent container */
}

.word-preview {
  flex: 1; /* allows cards to grow equally to fill available space */
  max-width: calc(25% - 1rem); /* if 4 items, each card is ~25% minus the gap */
  min-width: 0; /* prevent cards from breaking the layout */
  border: 1px solid #ddd;
  padding: 1rem;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column; /* ensures buttons are at the bottom */
  justify-content: space-between; /* push actions to the bottom */
  text-align: center;
}

.word-info {
  margin-bottom: 1rem;
}

.meaning,
.inContext {
  text-align: left;
}

.inContext ul {
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

.actions .close-icon:hover {
  color: #dc3545;
}

.actions .add-icon:hover {
  color: #28a745;
}

.actions .btn:hover {
  background: none;
}

</style>
