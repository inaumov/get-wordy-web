<script>
import {formatDateTime} from "@/js/utils.js";
import WordsheetTable from "@/components/classes/WordsheetTable.vue";
import Search from "@/components/Search.vue";
import CandidateWords from "@/views/library/CandidateWords.vue";

import {
  fetchTheme,
  addToTheme,
  updateThemeName,
  removeFromTheme,
  deleteTheme,
  getWords,
  generateTheme,
  getDraft,
  confirmTheme,
  removeCandidateWord
} from "@/js/themes-api.js";

export default {
  components: {
    CandidateWords,
    WordsheetTable,
    Search
  },

  props: ["themeId"],

  data() {
    return {
      theme: {},
      words: [],
      candidateWords: [],
      confirmed: false,
      deleted: false
    };
  },

  computed: {
    ids() {
      return this.words.map(word => word.wordId);
    },

    isEmpty() {
      return this.theme.status === "NEW" //|| this.theme.wordsTotal === 0;
    },

    isGenerating() {
      return this.theme.status === "GENERATING";
    },

    isDraft() {
      return this.theme.status === "DRAFT";
    },

    isConfirmed() {
      return this.theme.status === "CONFIRMED";
    },

    isProcessing() {
      return this.theme.status === "PROCESSING";
    },

    isReady() {
      return this.theme.status === "READY";
    },

    isFailed() {
      return this.theme.status === "FAILED";
    }
  },

  methods: {

    formatDateTime,

    async getData() {
      const response = await fetchTheme(this.themeId);
      this.theme = await response.json();

      if (this.theme.status === "READY") {
        const wordsResponse = await getWords(this.themeId);
        this.words = await wordsResponse.json();
      }

      if (this.theme.status === "DRAFT") {
        const candidateWordsResponse = await getDraft(this.themeId);
        this.candidateWords = await candidateWordsResponse.json();
      }
    },

    async generateThemeAction(candidatesLimit) {
      const response = await generateTheme(this.themeId, candidatesLimit);
      if (response.ok) {
        this.theme.status = "GENERATING" // to show spinner immediately
        await this.pollThemeStatus();
      } else {
        alert("Error");
      }
    },

    async confirmDraftAction() {
      const response = await confirmTheme(this.themeId);
      if (response.ok) {
        this.confirmed = true;
        this.theme.status = "CONFIRMED" // to show message immediately
        await this.pollThemeStatus();
      } else {
        alert("Error");
      }
    },

    async onNameEdit() {
      const editedText = this.$refs.editableEl.innerText.trim();
      if (editedText && editedText !== this.theme.name) {
        const response = await updateThemeName(this.themeId, editedText);
        if (response.ok) {
          this.theme = await response.json();
          console.log("Name changed:", editedText);
        } else {
          alert("Failed");
        }
      } else {
        this.$refs.editableEl.innerText = this.theme.name;
      }
    },

    async handleAddToTheme(wordExplanation) {
      if (this.themeContains(wordExplanation)) {
        alert("This word already exists");
        return;
      }

      const response = await addToTheme(this.themeId, wordExplanation.wordId);
      if (response.ok) {
        const added = await response.json();
        this.words.push(added);
      } else {
        alert("Error");
      }
    },

    async handleRemoveItemAction(wordId) {

      const response = await removeFromTheme(this.themeId, wordId);
      if (response.ok) {
        this.words = this.words.filter(w => w.wordId !== wordId);
      } else {
        alert("Error");
      }
    },

    async deleteThemeAction() {

      const response = await deleteTheme(this.themeId);
      if (response.ok) {
        this.deleted = true;
      } else {
        alert("Error");
      }
    },

    async removeCandidateWord(index, lemma, partOfSpeech) {
      let response = await removeCandidateWord(this.themeId, lemma, partOfSpeech);
      if (response.ok) {
        this.candidateWords.splice(index, 1);
      }
    },

    themeContains(wordExplanation) {
      return this.words.some(item => {
        return (
            item.lemma ===
            wordExplanation.lemma &&
            item.explanation.partOfSpeech ===
            wordExplanation.explanation.partOfSpeech
        );
      });
    },

    async pollThemeStatus() {
      const interval = setInterval(async () => {
        const response = await fetchTheme(this.themeId);
        this.theme = await response.json();
        if (this.theme.status !== "GENERATING") {
          clearInterval(interval);
          await this.getData();
        }
      }, 3000);
    }
  },

  mounted() {
    this.getData();
  }
};
</script>

<template>

  <div class="d-flex justify-content-start m-4">
    <router-link :to="{name:'themes'}" class="btn btn-secondary">Back</router-link>
  </div>

  <div class="p-4">

    <!-- Name / total -->

    <div class="d-flex justify-content-between align-items-center pb-2">

      <span
          class="h5 editable-name p-1"
          ref="editableEl"
          contenteditable="true"
          @blur="onNameEdit">
        {{ theme.name }}
      </span>

      <p v-if="isReady" class="fw-light mb-0">
        Words total:
        {{ theme.wordsTotal }}
      </p>
      <p v-if="isDraft" class="fw-light mb-0">
        Candidate words total:
        {{ candidateWords.length || theme.wordsTotal }}
      </p>

    </div>

    <div class="d-flex justify-content-end pb-4">
        <small v-if="theme.lastModifiedAt" class="text-success">
            {{ `Last Modified: ${formatDateTime(theme.lastModifiedAt)}` }}
        </small>
        <small v-else class="text-muted">
            {{ `Generated: ${formatDateTime(theme.generatedAt)}` }}
        </small>
    </div>

    <!-- Populated state -->

    <template v-if="isReady">

      <search @add-to-vocabulary="handleAddToTheme" :vocab-word-ids="ids"/>

      <wordsheet-table class="pt-5" :items="words">
        <template #actions="{ row }">
          <button class="btn"
              @click="handleRemoveItemAction(row.wordId)"
              title="Remove"
          >
            <i class="bi bi-x-lg"></i>
          </button>
        </template>
      </wordsheet-table>

    </template>

    <!-- Empty draft state -->

    <template v-if="isEmpty">

      <div class="empty-state">

        <i class="bi bi-stars empty-icon"></i>
        <h5 class="mt-3">
          This theme has no words yet
        </h5>
        <p class="text-muted">
          Generate candidate words for this theme
        </p>

        <div class="d-flex justify-content-between align-items-center gap-2 pt-2">
          <button class="btn btn-primary"
                  @click="generateThemeAction(5)">
            <i class="bi bi-search"></i>
            Get 5 words
          </button>
          <button class="btn btn-primary"
                  @click="generateThemeAction(10)">
            <i class="bi bi-search"></i>
            Get 10 words
          </button>
          <button class="btn btn-primary"
                  @click="generateThemeAction(15)">
            <i class="bi bi-search"></i>
            Get 15 words
          </button>
          <button class="btn btn-danger"
                  :disabled="deleted"
                  @click="deleteThemeAction">
            <i class="bi bi-trash"></i>
            Delete
          </button>
        </div>
      </div>

    </template>

    <!-- AI generation -->
    <template v-if="isGenerating">
      <div class="empty-state">
        <div
            class="spinner-border"
            role="status">
        </div>
        <h5 class="mt-3">
          Generating words...
        </h5>
        <p class="text-muted">
          This may take a few seconds
        </p>
      </div>
    </template>

    <template v-if="isDraft">
      <candidate-words
          :words="candidateWords"
          @remove-word="removeCandidateWord"
      />
    </template>

    <!-- Confirmed / waiting for service -->
    <template v-if="isConfirmed">
      <div class="empty-state">
        <i class="bi bi-check-circle text-success empty-icon"></i>
        <h5 class="mt-3">
          Theme confirmed
        </h5>
        <p class="text-muted">
          Preparation will start soon
        </p>
      </div>
    </template>

    <!-- Working process -->
    <template v-if="isProcessing">
      <div class="empty-state">
        <div
            class="spinner-border"
            role="status">
        </div>
        <h5 class="mt-3">
          Processing theme...
        </h5>
        <p class="text-muted">
          <span>
            We are preparing your vocabulary cards
          </span>
          <br>
          <span>
            This may take up to a minute
          </span>
        </p>
      </div>
    </template>

    <template v-if="isFailed">
      <div class="empty-state">
        <i
            class="bi bi-exclamation-octagon text-danger empty-icon">
        </i>
        <h5 class="mt-3">
          Generation failed
        </h5>
        <p class="text-muted text-center">
          We couldn't generate candidate words for this theme.
          Please try again.
        </p>
        <div class="d-flex gap-2 mt-2">
          <button
              class="btn btn-primary"
              @click="generateThemeAction">
            <i class="bi bi-arrow-clockwise"></i>
            Try again
          </button>
          <button
              class="btn btn-outline-danger"
              @click="deleteThemeAction">
            <i class="bi bi-trash"></i>
            Delete
          </button>
        </div>
      </div>
    </template>

    <!-- Action buttons -->

    <div v-if="isReady" class="d-flex justify-content-end mt-4">
      <button class="btn btn-danger" :disabled="deleted || confirmed" @click="deleteThemeAction">
        <i class="bi bi-trash"></i>
        Delete
      </button>
    </div>

    <div v-if="isDraft" class="d-flex justify-content-end gap-2 mt-4">
      <button class="btn btn-primary" :disabled="deleted || confirmed" @click="confirmDraftAction">
        <i class="bi bi-check"></i>
        Confirm
      </button>
      <button class="btn btn-danger" :disabled="deleted || confirmed" @click="deleteThemeAction">
        <i class="bi bi-trash"></i>
        Delete
      </button>
    </div>

  </div>

</template>

<style>

.editable-name {
  font-size: 1.25rem;
  white-space: nowrap;
}

.empty-state {
  min-height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.empty-icon {
  font-size: 3rem;
}

</style>