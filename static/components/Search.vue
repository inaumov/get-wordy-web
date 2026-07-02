<script>
import {search} from "@/js/words-search-api.js";

export default {
  name: "Search",
  props: {
    vocabWordIds: {
      type: Array,
      required: false,
      default: () => []
    }
  },
  emits: ["add-to-vocabulary"],
  data() {
    return {
      searchInput: "",
      foundExplanations: null,
      selectedIndexes: [],
      showMore: {},
      loading: false, // SEARCH loading
    };
  },
  methods: {
    async onSearch() {
      const input = this.searchInput.trim();
      if (!input) return;

      this.loading = true;
      const response = await search(input);
      const result = await response.json();

      if (response.ok && result?.explanations?.length > 0) {
        this.searchInput = "";
        this.foundExplanations = result;
        this.selectedIndexes = [];
        this.showMore = {};
        this.loading = false;
      }
    },
    resetSearch() {
      this.foundExplanations = null;
      this.selectedIndexes = [];
      this.showMore = {};
    },
    // check whether the explanation is already part of vocab or newly added
    getStatus(idx) {
      const explanation = this.foundExplanations?.explanations?.[idx];
      const wordId = explanation?.wordId ?? explanation?.id ?? null;

      if (wordId && this.vocabWordIds.includes(wordId)) return "existing";
      if (this.selectedIndexes.includes(idx)) return "added";
      return "available";
    },

    addExplanation(explanation, idx) {
      if (this.getStatus(idx) !== "available") return;

      this.selectedIndexes.push(idx);
      const copy = {
        wordId: explanation.wordId ?? explanation.id ?? null,
        lemma: this.foundExplanations.lemma,
        transcription: this.foundExplanations.transcription,
        explanation: {...explanation}
      };
      this.$emit("add-to-vocabulary", copy);
    }
  }
};
</script>

<template>
  <div id="word-search-panel">

    <!-- search input -->
    <div class="d-flex justify-content-center">
      <div class="col-md-4">
        <form id="search-words-form" class="form-inline" @submit.prevent="onSearch">
          <div class="form-group">
            <div class="input-group">
              <input
                  v-model="searchInput"
                  id="search-input"
                  type="text"
                  class="form-control"
                  placeholder="Search for a word or phrase..."
                  autocomplete="off"
                  :disabled="loading"
                  required
              />

              <button
                  type="submit"
                  class="btn btn-md btn-default border"
                  :disabled="loading"
              >
              <span v-if="!loading">
                <i class="bi bi-search"></i>
              </span>
                <span v-else>
                <span class="spinner-border spinner-border-sm"></span>
              </span>
              </button>

              <!-- reset button (only shown when results exist) -->
              <button
                  v-if="foundExplanations"
                  type="button"
                  class="btn btn-md btn-outline-secondary border"
                  @click="resetSearch"
              >
                <i class="bi bi-x-lg"></i>
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <!-- results preview -->
    <div class="pt-4" v-if="foundExplanations?.lemma">
      <div class="word-header text-center mb-3">
        <h2 class="word-title">
          {{ foundExplanations.lemma }}
        </h2>
        <h2 class="word-title">
          <span class="transcription">
            {{ foundExplanations.transcription }}
          </span>
        </h2>
        <p class="choose-text mb-3">Choose one or more explanations:</p>
      </div>

      <div class="explanation-list" role="list">
        <div
            v-for="(explanation, idx) in foundExplanations.explanations"
            :key="idx"
            :class="[
            'explanation-item',
            {
              'selected-added': getStatus(idx) === 'added',
              'selected-existing': getStatus(idx) === 'existing'
            }
          ]"
        >
          <div class="d-flex gap-1">
            <span class="chip pos">{{ explanation.partOfSpeech }}</span>
            <span v-if="explanation.register" class="chip register">{{ explanation.register }}</span>
            <span v-if="explanation.domain" class="chip domain">{{ explanation.domain }}</span>
            <span v-if="explanation.level" class="chip level">{{ explanation.level }}</span>
          </div>

          <p class="meaning">{{ explanation.meaning }}</p>

          <div v-if="explanation.collocations?.length" class="collocations mb-1">
            <span class="label me-1">Collocations:</span>
            <span>{{ explanation.collocations.join(', ') }}</span>
          </div>

          <div v-if="explanation.inContext?.length" class="context mb-1">
            <span class="label me-1">In Context:</span>
            <em>"{{ explanation.inContext[0] }}"</em>
            <button
                v-if="explanation.inContext.length > 1"
                class="btn-link small"
                @click="showMore[idx] = !showMore[idx]"
            >
              {{ showMore[idx] ? 'Hide' : '+ More' }}
            </button>

            <ul v-if="showMore[idx]" class="extra-examples">
              <li v-for="(s, i) in explanation.inContext.slice(1)" :key="i">"{{ s }}"</li>
            </ul>
          </div>

          <!-- action buttons -->
          <div class="right-actions">
            <button
                v-if="getStatus(idx) === 'available'"
                class="btn btn-success btn-sm"
                @click="addExplanation(explanation, idx)"
            >
              <i class="bi bi-plus-circle"></i> Add
            </button>

            <button
                v-else-if="getStatus(idx) === 'added'"
                class="btn btn-outline-success btn-sm"
                disabled
            >
              <i class="bi bi-check-circle"></i> Added
            </button>

            <button
                v-else-if="getStatus(idx) === 'existing'"
                class="btn btn-outline-secondary btn-sm"
                disabled
            >
              <i class="bi bi-check2-all"></i> Already in vocab
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.word-header {
  text-align: center;
}

.word-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #222;
}

.transcription {
  color: #888;
  font-size: 0.95rem;
  margin-left: 0.4rem;
}

.explanation-list {
  display: grid;
  gap: 0.75rem;
}

.explanation-item {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 0.9rem 1rem;
  display: flex;
  flex-direction: column;
  transition: all 0.2s ease;
}

.explanation-item:hover {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.selected-added {
  border-color: #28a745;
  box-shadow: 0 0 0 3px rgba(40, 167, 69, 0.15);
}

.selected-existing {
  border-color: #6c757d;
  background: #f7f7f7;
  box-shadow: none;
}

.chip {
  font-size: 0.85rem;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
  background: #f4f4f4;
  color: #333;
}

.chip.pos {
  background: #e8f3ff;
  color: #0a58ca;
}

.chip.register {
  background: #fff4e0;
  color: #b36b00;
}

.chip.domain {
  background: #eef2ff;
  color: #4455aa;
}

.chip.level {
  background: #eafaf1;
  color: #198754;
}

.meaning {
  font-weight: 500;
  color: #222;
}

.collocations,
.context {
  font-size: 0.9rem;
  color: #555;
}

.label {
  font-weight: 600;
}

.extra-examples {
  margin-top: 0.3rem;
  padding-left: 1.2rem;
  list-style: disc;
  color: #666;
  font-style: italic;
}

.btn-link.small {
  background: none;
  border: none;
  color: #0a58ca;
  padding: 0;
  font-size: 0.8rem;
  cursor: pointer;
  margin-left: 0.3rem;
}

.right-actions {
  align-self: flex-end;
}

</style>
