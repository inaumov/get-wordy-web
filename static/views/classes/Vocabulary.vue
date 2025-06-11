<script>
import WordsheetTable from "@/components/classes/WordsheetTable.vue";
import SearchInput from "@/components/search/SearchInput.vue";
import SearchResultsPreview from "@/components/search/SearchResultsPreview.vue";

import {
  getVocabulary,
  updateVocabularyName,
  addToVocabulary,
  removeFromVocabulary,
  updateActivation
} from '@/js/classes-api.js';
import {formatDateTime} from "@/js/utils.js";

export default {
  components: {SearchResultsPreview, WordsheetTable, SearchInput},
  props: ['classId', 'vocabId'],
  data() {
    return {
      name: '',
      isShared: false,
      wordsList: [],
      updateTime: '',
      foundExplanations: {}
    }
  },
  methods: {
    formatDateTime,
    async getData() {
      const response = await getVocabulary(this.classId, this.vocabId);
      const vocabulary = await response.json();
      this.name = vocabulary['name'];
      this.isShared = vocabulary['isShared'];
      this.updateTime = vocabulary['updateTime'];
      this.wordsList = vocabulary['words'] || [];
    },
    onNameEdit() {
      const editedText = this.$refs.editableEl.innerText.trim();
      if (editedText && editedText !== this.name) {
        updateVocabularyName(this.classId, this.vocabId, editedText)
            .then(response => {
              if (response.ok) {
                this.name = editedText; // update model
                console.log('Name has been changed to:', editedText, ', for vocabulary id =', this.vocabId);
                // todo success notification
              }
              // todo failure notification
            });
      } else {
        this.$refs.editableEl.innerText = this.name // restore original if cleared
      }
    },
    onSearch(searchResult) {
      this.foundExplanations = searchResult;
    },
    reset() {
      this.foundExplanations = {}; // reset on success
    },
    updateActivation() {
      const newVal = !this.isShared;
      updateActivation(this.classId, this.vocabId, newVal)
          .then(response => {
            if (response.ok) {
              this.isShared = newVal; // update model
              console.log('Is_shared flag has been changed to:', newVal, ', for vocabulary id =', this.vocabId);
              // todo success notification
            }
            // todo failure notification
          });
    },
    handleAddToVocabulary(wordExplanation) {
      if (!this.vocabularyContains(wordExplanation)) {
        addToVocabulary(this.classId, this.vocabId, wordExplanation.wordId)
            .then(response => {
              if (response.ok) {
                let itemAdded = response.json();
                this.wordsList.push(itemAdded);
                this.reset();
              } else {
                alert('Error');
              }
            })
      } else {
        alert('This word is already added.');
      }
    },
    handleRemoveItemAction(wordId) {
      removeFromVocabulary(this.classId, this.vocabId, wordId)
          .then(response => {
            if (response.ok) {
              const index = this.wordsList.findIndex(obj => obj['wordId'] === wordId)
              this.wordsList.splice(index, 1)
            }
          });
    },
    vocabularyContains(wordExplanation) {
      return this.wordsList.some((item) => {
        return item.value === wordExplanation.value
            && item.explanation.partOfSpeech === wordExplanation.explanation.partOfSpeech
      });
    },
  },
  mounted() {
    this.getData()
  },
  computed: {
    hasNoWords() {
      return !(this.wordsList && this.wordsList.length > 0);
    }
  }
};

</script>

<template>
  <div class="d-flex justify-content-start p-4">
    <router-link :to="{name: 'class-dashboard', params: {classId:this.classId}}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>

  <div class="">

    <div class="d-flex justify-content-between p-4">
      <span class="d-inline-flex align-items-center p-1 editable-name"
            ref="editableEl"
            contenteditable="true"
            v-on:blur="onNameEdit">
        {{ name }}
      </span>
      <span :class="['p-1', 'd-inline-flex', 'align-items-center', isShared ? 'text-success' : 'text-secondary']">
        {{ isShared ? 'Shared: ' + formatDateTime(updateTime) : 'Not shared' }}
      </span>
    </div>

    <div class="d-flex justify-content-center p-4">
      <div style="padding-top:7px;" class="col-md-4 form-group pull-right">
        <search-input @wordsheet-search-submit="onSearch" v-bind="{onSearchEventName: 'wordsheet-search-submit'}"/>
        <small class="text-muted">Enter a word or phrase and click search</small>
      </div>
    </div>

    <search-results-preview class="p-4" @add-to-vocabulary="handleAddToVocabulary" v-bind="{previewData: this.foundExplanations}"/>

    <div class="p-4">
      <div class="d-flex flex-column align-items-end">
        <button class="btn" v-on:click="" title="Save as template">
          <i class="bi bi-plus-square me-1"></i>
          <span>Save as template</span>
        </button>
        <button class="btn" v-on:click="" title="Print pdf">
          <i class="bi bi-file-earmark-pdf me-1"></i>
          <span>Print pdf</span>
        </button>
      </div>
    </div>

    <wordsheet-table class="p-4" v-bind="{items: this.wordsList}">
      <template #actions="{ row }">
        <router-link
            :to="{ name: 'edit-explanation', params: { vocabId: vocabId, wordId: row?.wordId } }"
            class="btn btn-lg"
            title="Edit word explanation"
        >
          <i class="bi bi-pencil-square"></i>
        </router-link>

        <button
            class="btn btn-lg"
            @click="handleRemoveItemAction(row?.wordId)"
            title="Delete"
        >
          <i class="bi bi-x-lg"></i>
        </button>
      </template>
    </wordsheet-table>

    <!-- submit -->
    <div class="d-flex justify-content-end p-4">
        <button type="button" class="btn btn-primary border btn-md" v-on:click="updateActivation()"
        :disabled="hasNoWords">
          {{ this.isShared === false ? 'Share' : 'Stop sharing' }}
        </button>
    </div>

  </div>
</template>

<style scoped>
.editable-name {
  font-size: 1.25rem;
  text-align: center;
  white-space: nowrap;
}
</style>