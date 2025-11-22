<script>
import WordsheetTable from "@/components/classes/WordsheetTable.vue";
import Search from "@/components/Search.vue";

import {fetchTheme, addToTheme, updateThemeName, removeFromTheme, deleteTheme} from '@/js/themes-api.js';

export default {
  components: {WordsheetTable, Search},
  props: ['themeId'],
  data() {
    return {
      name: '',
      words: [],
      deleted: false
    }
  },
  methods: {
    async getData() {
      const response = await fetchTheme(this.themeId);
      const template = await response.json();
      this.name = template['name'];
      this.words = template['words'] || [];
    },
    async onNameEdit() {
      const editedText = this.$refs.editableEl.innerText.trim();
      if (editedText && editedText !== this.name) {
        let response = await updateThemeName(this.themeId, editedText);
        if (response.ok) {
          this.name = editedText; // update model
          console.log('Name has been changed to:', editedText, ', for vocabulary id =', this.vocabId);
          // todo success notification
        } else {
          alert('Failed')
          // todo failure notification
        }
      } else {
        this.$refs.editableEl.innerText = this.name // restore original if cleared
      }
    },
    async handleAddToTheme(wordExplanation) {
      if (!this.vocabularyContains(wordExplanation)) {
        let response = await addToTheme(this.themeId, wordExplanation.wordId)
        if (response.ok) {
          let itemAdded = await response.json();
          if (itemAdded.wordId) {
            console.log(itemAdded)
            this.wordsList.push(itemAdded);
          }
        } else {
          alert('Error');
        }
      } else {
        alert('This word is already added.');
      }
    },
    async handleRemoveItemAction(wordId) {
      let response = await removeFromTheme(this.themeId, wordId);
      if (response.ok) {
        const index = this.words.findIndex(obj => obj['wordId'] === wordId)
        this.words.splice(index, 1)
      } else {
        alert('Error');
      }
    },
    async deleteThemeAction() {
      let response = await deleteTheme(this.themeId);
      if (response.ok) {
        this.deleted = true;
      } else {
        alert('Error');
      }
    },
    vocabularyContains(wordExplanation) {
      return this.words.some((item) => {
        return item.lemma === wordExplanation.lemma
            && item.explanation.partOfSpeech === wordExplanation.explanation.partOfSpeech
      });
    }
  },
  mounted() {
    this.getData()
  },
  computed: {
    ids() {
      return this.words.map(word => word.wordId);
    }
  }
};

</script>

<template>
  <div class="d-flex justify-content-start m-4">
    <router-link :to="{name: 'themes'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>

  <div class="p-4">

    <!-- name left / words total right -->
    <div class="d-flex justify-content-between align-items-center pt-2 pb-4">
      <span class="h5 d-inline-flex align-items-center editable-name p-1"
            ref="editableEl"
            contenteditable="true"
            @blur="onNameEdit">
        {{ name }}
      </span>
      <p class="fw-light mb-0">Words total: {{ words.length }}</p>
    </div>

    <search @add-to-vocabulary="handleAddToTheme" :vocab-word-ids="this.ids"/>

    <wordsheet-table class="pt-5" v-bind="{items: this.words}">
      <template #actions="{ row }">
        <button
            class="btn btn-lg"
            @click="handleRemoveItemAction(row?.wordId)"
            title="Delete"
        >
          <i class="bi bi-x-lg"></i>
        </button>
      </template>
    </wordsheet-table>

    <div class="d-flex justify-content-end mt-4 gap-2">
      <button
          class="btn btn-sm btn-danger"
          :disabled="deleted"
          @click="deleteThemeAction"
      >
        <i class="bi bi-trash"></i> Delete
      </button>
    </div>
  </div>
</template>

<style>
.editable-name {
  font-size: 1.25rem;
  text-align: center;
  white-space: nowrap;
}
</style>