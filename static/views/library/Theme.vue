<script>
import WordsheetTable from "@/components/classes/WordsheetTable.vue";
import Search from "@/components/Search.vue";

import {
  fetchTheme,
  addToTheme,
  updateThemeName,
  removeFromTheme,
  deleteTheme,
  getWords
} from "@/js/themes-api.js";

export default {
  components: {
    WordsheetTable,
    Search
  },

  props: ["themeId"],

  data() {
    return {
      name: "",
      words: [],
      deleted: false,
      loadingWords: false
    };
  },

  computed: {
    ids() {
      return this.words.map(word => word.wordId);
    },

    hasWords() {
      return this.words.length > 0;
    }
  },

  methods: {

    async getData() {
      const response = await fetchTheme(this.themeId);
      const theme = await response.json();

      this.name = theme.name;

      const wordsResponse = await getWords(this.themeId);
      const words = await wordsResponse.json();

      this.words = theme.words || words || [];
    },

    async onNameEdit() {
      const editedText =
          this.$refs.editableEl.innerText.trim();

      if (
          editedText &&
          editedText !== this.name
      ) {

        const response =
            await updateThemeName(
                this.themeId,
                editedText
            );

        if (response.ok) {

          this.name = editedText;

          console.log(
              "Name changed:",
              editedText
          );

        } else {

          alert("Failed");

        }

      } else {

        this.$refs.editableEl.innerText =
            this.name;

      }
    },

    async handleAddToTheme(wordExplanation) {

      if (this.themeContains(wordExplanation)) {
        alert("This word already exists");
        return;
      }

      const response =
          await addToTheme(
              this.themeId,
              wordExplanation.wordId
          );

      if (response.ok) {

        const added =
            await response.json();

        this.words.push(added);

      } else {

        alert("Error");

      }
    },

    async handleRemoveItemAction(wordId) {

      const response =
          await removeFromTheme(
              this.themeId,
              wordId
          );

      if (response.ok) {

        this.words =
            this.words.filter(
                w => w.wordId !== wordId
            );

      } else {

        alert("Error");

      }
    },

    async deleteThemeAction() {

      const response =
          await deleteTheme(
              this.themeId
          );

      if (response.ok) {

        this.deleted = true;

      } else {

        alert("Error");

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

    async findWords() {

      this.loadingWords = true;

      try {

        // TODO:
        // Replace later with search engine request

        await new Promise(resolve =>
            setTimeout(resolve, 2000)
        );

        /*
        Example later:

        const response =
            await searchWords(
                this.themeId
            );

        this.words =
            await response.json();
        */

      } finally {

        this.loadingWords = false;

      }
    }
  },

  mounted() {
    this.getData();
  }
};
</script>

<template>

  <div class="d-flex justify-content-start m-4">

    <router-link
        :to="{name:'themes'}"
        class="btn btn-secondary">

      Back

    </router-link>

  </div>

  <div class="p-4">

    <!-- Name / total -->

    <div
        class="d-flex justify-content-between align-items-center pb-4">

      <span
          class="h5 editable-name p-1"
          ref="editableEl"
          contenteditable="true"
          @blur="onNameEdit">

        {{ name }}

      </span>

      <p class="fw-light mb-0">

        Words total:
        {{ words.length }}

      </p>

    </div>

    <!-- Populated state -->

    <template v-if="hasWords">

      <search
          @add-to-vocabulary="handleAddToTheme"
          :vocab-word-ids="ids"
      />

      <wordsheet-table
          class="pt-5"
          :items="words">

        <template #actions="{ row }">

          <button
              class="btn btn-lg"
              @click="
                handleRemoveItemAction(
                  row.wordId
                )
              "
              title="Delete">

            <i class="bi bi-x-lg"></i>

          </button>

        </template>

      </wordsheet-table>

    </template>

    <!-- Empty draft state -->

    <template v-else>

      <div class="empty-state">

        <i
            class="bi bi-journal-text empty-icon">
        </i>

        <h5 class="mt-3">

          This theme has no words yet

        </h5>

        <p class="text-muted">

          Search and populate this
          theme automatically

        </p>

        <button
            class="btn btn-primary"
            :disabled="loadingWords"
            @click="findWords">

          <span
              v-if="loadingWords"
              class="
                spinner-border
                spinner-border-sm
                me-2
              ">
          </span>

          {{
            loadingWords
                ? "Searching..."
                : "Find words"
          }}

        </button>

      </div>

    </template>

    <!-- Footer -->

    <div
        class="d-flex justify-content-end mt-4">

      <button
          class="btn btn-danger"
          :disabled="deleted"
          @click="deleteThemeAction">

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