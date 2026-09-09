<script>
import {formatDateTime} from "@/js/utils.js";
import RenameModal from "@/components/modal/RenameModal.vue";
import WordsheetTable from "@/components/classes/WordsheetTable.vue";
import Search from "@/components/Search.vue";
import DraftWords from "@/views/library/DraftWords.vue";
import DeleteModal from "@/components/modal/DeleteModal.vue";

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
    RenameModal,
    DraftWords,
    WordsheetTable,
    DeleteModal,
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

    openRenameModal() {
      this.$refs.renameModal.open();
    },
    openDeleteModal() {
      this.$refs.deleteModal.open();
    },

    async renameTheme(newName) {
      let response = await updateThemeName(this.themeId, newName);
      if (response.ok) {
        this.name = newName; // update model
        console.log('Name has been changed to:', newName, ', for theme id =', this.themeId);
        // todo success notification
      } else {
        alert('Failed')
        // todo failure notification
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

    <div class="d-flex justify-content-between align-items-start pb-5">

      <div class="theme-card p-3 rounded w-75">
        <div class="d-flex align-items-center justify-content-between">
          <!-- theme name -->
          <span class="name">{{ theme.name }}</span>
          <!-- theme status -->
          <span v-if="theme.status === 'READY'" class="text-success">
            <i class="bi bi-dot"></i>{{ theme.status }}
          </span>
          <span v-else class="text-secondary">
            <i class="bi bi-dot"></i>{{ theme.status }}
          </span>
        </div>
        <!-- words total -->
        <div v-if="isReady" class="my-1">
          <span class="text-muted">
            Words total: {{ theme.wordsTotal }}
          </span>
        </div>
        <div v-if="isDraft" class="my-1">
          <span class="text-muted">
            Candidate words total: {{ candidateWords.length || theme.wordsTotal }}
          </span>
        </div>
      </div>

      <div class="ms-auto">
        <button
            class="btn btn-light dropdown-toggle"
            type="button"
            data-bs-toggle="dropdown"
        >
          Actions
        </button>

        <ul class="dropdown-menu">

          <li>
            <a class="dropdown-item" href="#" @click.prevent="openRenameModal">
              Rename
            </a>
          </li>

          <li>
            <a class="dropdown-item" href="#" @click.prevent="">
              <span class="me-1">Print PDF</span>
              <i class="bi bi-file-earmark-pdf"></i>
            </a>
          </li>

          <li>
            <a class="dropdown-item" style="color: firebrick" href="#" @click.prevent="openDeleteModal">
              Delete
            </a>
          </li>

        </ul>
      </div>

    </div>

    <div class="pb-5">
      <div class="d-flex justify-content-end pb-2 border-bottom">
        <small v-if="isEmpty" class="text-muted">
            {{ `Created: ${formatDateTime(theme.createdAt)}` }}
        </small>
        <small v-if="theme.lastModifiedAt" class="text-success">
            {{ `Last Modified: ${formatDateTime(theme.lastModifiedAt)}` }}
        </small>
      </div>
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
      <draft-words
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
      <button class="btn btn-danger" :disabled="deleted || confirmed" @click="openDeleteModal">
        <i class="bi bi-trash"></i>
        Delete
      </button>
    </div>

    <RenameModal
        ref="renameModal"
        modal-name="theme"
        :current-name="this.theme.name"
        :on-submit="renameTheme"
    />

    <DeleteModal
        ref="deleteModal"
        modal-name="theme"
        :item-name="theme.name"
        :on-submit="deleteThemeAction"
    />

  </div>

</template>

<style scoped>

.theme-card {
  border-radius: 0.5rem;
  background: lightgrey;
}

.theme-card .name {
  font-size: 1.25rem;
  font-weight: bold;
}

.dropdown-toggle {
  background-color: #f8f9fa;
}

.dropdown-item:hover {
  background-color: #e9ecef;
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