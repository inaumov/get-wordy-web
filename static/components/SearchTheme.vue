<script>
import {searchThemeData} from "@/js/search-api.js";

export default {
  name: "ThemeSearch",
  emits: ["save-theme", "save-selected"],
  props: {
    vocabWordIds: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      searchQuery: "",
      themeResults: null, // themeName + words[]
      showMore: {}, // expanding context examples per explanation
      selectedWords: [], // selected wordIds
      loading: false, // SEARCH loading
      toast: null // { type: "success" | "error", message: string }
    };
  },
  methods: {
    async onSearch() {
      const input = this.searchQuery.trim();
      if (!input) return;

      try {
        this.loading = true;
        const response = await searchThemeData(input);
        const result = await response.json();

        if (response.ok && Array.isArray(result.words) && result.words.length > 0) {
          this.themeResults = result;
          this.showMore = {};
          this.selectedWords = [];
          this.searchQuery = "";
        } else {
          this.showToast("error", "No results found.");
        }
      } catch (e) {
        this.showToast("error", "Search failed.");
      } finally {
        this.loading = false;
      }
    },
    resetSearch() {
      this.themeResults = null;
      this.selectedWords = [];
      this.showMore = {};
    },
    toggleWordSelection(word) {
      const id = word.wordId;
      const idx = this.selectedWords.indexOf(id);

      if (idx >= 0) this.selectedWords.splice(idx, 1);
      else this.selectedWords.push(id);
    },
    /**
     * Save only selected words
     */
    async saveSelectedWords() {
      if (!this.themeResults) {
        return;
      }
      const selected = this.themeResults.words
          .filter(w => this.selectedWords.includes(w.wordId))
          .map(w => w.wordId);
      try {
        // trigger parent save
        this.$emit(
            "save-selected",
            this.themeResults.themeId,
            this.themeResults.themeName,
            selected
        );
        this.showToast("success", "Selected words saved!");
      } catch (e) {
        this.showToast("error", "Saving failed.");
      }
    },
    /**
     * Save the whole theme
     */
    async saveWholeTheme() {
      if (!this.themeResults) {
        return;
      }
      const allIds = this.themeResults.words.map(w => w.wordId);
      try {
        this.$emit(
            "save-theme",
            this.themeResults.themeId,
            this.themeResults.themeName,
            allIds
        );
        this.showToast("success", "Whole theme saved!");
      } catch (e) {
        this.showToast("error", "Saving failed.");
      }
    },
    /**
     * Toast popup
     */
    showToast(type, message) {
      this.toast = {type, message};

      setTimeout(() => {
        this.toast = null;
      }, 3000);
    }
  }
};
</script>

<template>
  <div class="word-search-panel">

    <!-- toast notification -->
    <div
        v-if="toast"
        class="toast-box"
        :class="toast.type === 'success' ? 'toast-success' : 'toast-error'"
    >
      {{ toast.message }}
    </div>

    <!-- search input -->
    <div class="d-flex justify-content-center">
      <div class="col-md-4" style="padding-top: 7px;">
        <form id="search-theme-form" class="form-inline" @submit.prevent="onSearch">

          <div class="input-group">

            <input
                v-model="searchQuery"
                id="search-input"
                type="text"
                class="form-control"
                placeholder="Search for a vocabulary theme…"
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

            <button
                v-if="themeResults"
                type="button"
                class="btn btn-md btn-outline-secondary border"
                @click="resetSearch"
            >
              <i class="bi bi-x-lg"></i>
            </button>

          </div>
        </form>
      </div>
    </div>

    <!-- results -->
    <div class="py-4" v-if="themeResults">

      <!-- header -->
      <div class="text-center mb-4">
        <h2 class="fw-bold">{{ themeResults.themeName }}</h2>
        <p class="text-muted">Found {{ themeResults.words.length }} words</p>
      </div>

      <!-- word list -->
      <div class="word-list">
        <div
            v-for="word in themeResults.words"
            :key="word.wordId"
            class="word-item"
            :class="{ 'selected-picked': selectedWords.includes(word.wordId) }"
        >

          <!-- top bar: lemma + checkbox -->
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h4 class="mb-1">{{ word.lemma }}</h4>
              <span class="text-muted">{{ word.transcription }}</span>
            </div>

            <!-- status / checkbox -->
            <div>
              <input
                  type="checkbox"
                  class="form-check-input"
                  :checked="selectedWords.includes(word.wordId)"
                  @change="toggleWordSelection(word)"
              />
            </div>
          </div>

          <hr/>

          <!-- explanations -->
          <div v-for="(exp, idx) in word.explanations" :key="idx" class="explanation-section">

            <div class="d-flex gap-1 mb-1">
              <span class="chip pos">{{ exp.partOfSpeech }}</span>
              <span v-if="exp.register" class="chip register">{{ exp.register }}</span>
              <span v-if="exp.domain" class="chip domain">{{ exp.domain }}</span>
            </div>

            <p class="meaning">{{ exp.meaning }}</p>

            <!-- collocations -->
            <div v-if="exp.collocations?.length" class="collocations mb-1">
              <span class="label me-1">Collocations:</span>
              <span>{{ exp.collocations.join(", ") }}</span>
            </div>

            <!-- context -->
            <div v-if="exp.inContext?.length" class="context mb-1">
              <span class="label me-1">In Context:</span>
              <em>"{{ exp.inContext[0] }}"</em>

              <button
                  v-if="exp.inContext.length > 1"
                  class="btn-link small"
                  @click="showMore[word.wordId + '_' + idx] = !showMore[word.wordId + '_' + idx]"
              >
                {{ showMore[word.wordId + '_' + idx] ? 'Hide' : '+ More' }}
              </button>

              <ul
                  v-if="showMore[word.wordId + '_' + idx]"
                  class="extra-examples"
              >
                <li
                    v-for="(s, i) in exp.inContext.slice(1)"
                    :key="i"
                >
                  "{{ s }}"
                </li>
              </ul>
            </div>

          </div>
        </div>
      </div>

      <!-- save selected -->
      <div class="d-flex justify-content-end mt-4 gap-2">
        <button
            class="btn btn-sm btn-success"
            :disabled="selectedWords.length === 0"
            @click="saveSelectedWords"
        >
          <i class="bi bi-check-circle"></i> Save selected words
        </button>
        <button
            class="btn btn-sm btn-primary"
            @click="saveWholeTheme"
        >
          <i class="bi bi-save"></i> Save whole list
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped>
.word-list {
  display: grid;
  gap: 1rem;
}

.word-item {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 1rem;
  transition: 0.2s ease;
}

.word-item:hover {
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.selected-picked {
  border-color: #28a745;
  background: #f6fff8;
}

.explanation-section {
  margin-bottom: 0.75rem;
}

.chip {
  font-size: 0.75rem;
  padding: 2px 6px;
  border-radius: 4px;
  background: #f1f1f1;
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

.meaning {
  font-weight: 500;
}

/* collocations section */
.collocations .label {
  color: #555;
}

/* context section */
.context em {
  color: #555;
}

.context .btn-link.small {
  margin-left: 6px;
}

.label {
  font-weight: 600;
}

.extra-examples {
  margin-top: 0.3rem;
  padding-left: 1.25rem;
  list-style: disc;
  font-style: italic;
  color: #666;
}

.btn-link.small {
  background: none;
  border: none;
  color: #0a58ca;
  padding: 0;
  font-size: 0.8rem;
  cursor: pointer;
}

/* toast notifications */
.toast-box {
  padding: 10px 16px;
  border-radius: 6px;
  margin-bottom: 1rem;
  text-align: center;
  font-weight: 500;
  animation: fadein 0.3s ease;
}

.toast-success {
  background: #e7f8ee;
  color: #2e8b57;
  border: 1px solid #b6e2c0;
}

.toast-error {
  background: #fdecea;
  color: #cc1f1a;
  border: 1px solid #f5c2c0;
}

@keyframes fadein {
  from {
    opacity: 0
  }
  to {
    opacity: 1
  }
}

</style>