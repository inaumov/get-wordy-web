<script>
export default {
  props: {
    words: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {};
  },
  methods: {
    removeWord(index, lemma, partOfSpeech) {
      this.$emit("remove-word", index, lemma, partOfSpeech);
    }
  },
  emits: [
    "remove-word"
  ]
};
</script>

<template>
  <!-- results -->
  <div class="candidate-area">
    <div
        v-for="(word, index) in words"
        :key="`${word.lemma}-${index}`"
        class="word-bubble"
    >
      <div class="bubble-header">
        <strong>
          {{ word.lemma }}
        </strong>
        <button
            class="btn btn-sm"
            title="Remove"
            @click="removeWord(index, word.lemma, word.partOfSpeech)"
        >
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="bubble-tags">
        <span class="chip pos">
          {{ word.partOfSpeech }}
        </span>
        <span v-if="word.level" class="chip level">
          {{ word.level }}
        </span>
      </div>

      <div class="meaning">
        {{ word.meaning }}
      </div>
    </div>
  </div>


</template>

<style scoped>

.candidate-area {
  display: grid;
  grid-template-columns:
      repeat(
          auto-fill,
          minmax(280px, 1fr)
      );
  gap: 16px;
  width: 100%;
}

.word-bubble {
  border: 1px solid #dee2e6;
  border-radius: 12px;
  padding: 12px;
  background: white;
  transition: 0.2s ease;
}

.word-bubble:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.bubble-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.bubble-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}

.chip {
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 12px;
}

.pos {
  background: #e8f3ff;
  color: #0a58ca;
}

.level {
  background: #fff4e0;
  color: #b36b00;
}

.meaning {
  color: #444;
}

</style>