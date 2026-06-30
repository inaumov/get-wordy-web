<script>
import Search from "@/components/Search.vue";
import WordsheetTable from "@/components/classes/WordsheetTable.vue";
import {
  getVocabulary,
  updateVocabularyName,
  addToVocabulary,
  removeFromVocabulary,
  updateActivation
} from '@/js/classes-api.js';
import {formatDateTime} from "@/js/utils.js";

export default {
  components: {
    Search,
    WordsheetTable
  },
  props: ['classId', 'vocabId'],
  data() {
    return {
      name: '',
      isShared: false,
      wordsList: [],
      updateTime: ''
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
    async onNameEdit() {
      const editedText = this.$refs.editableEl.innerText.trim();
      if (editedText && editedText !== this.name) {
        let response = await updateVocabularyName(this.classId, this.vocabId, editedText);
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
    async updateActivation() {
      const newVal = !this.isShared;
      let response = await updateActivation(this.classId, this.vocabId, newVal);
      if (response.ok) {
        this.isShared = newVal; // update model
        console.log('Is_shared flag has been changed to:', newVal, ', for vocabulary id =', this.vocabId);
        // todo success notification
      } else {
        alert('Error');
        // todo failure notification
      }
    },
    async handleAddToVocabulary(wordExplanation) {
      if (!this.vocabularyContains(wordExplanation)) {
        let response = await addToVocabulary(this.classId, this.vocabId, wordExplanation.wordId);
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
      let response = await removeFromVocabulary(this.classId, this.vocabId, wordId);
      if (response.ok) {
        const index = this.wordsList.findIndex(obj => obj['wordId'] === wordId)
        this.wordsList.splice(index, 1)
      } else {
        alert('Error');
      }
    },
    vocabularyContains(wordExplanation) {
      return this.wordsList.some((item) => {
        return item.lemma === wordExplanation.lemma
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
    },
    ids() {
      return this.wordsList.map(word => word.wordId);
    }
  }
};

</script>

<template>
  <div class="d-flex justify-content-start p-4">
    <router-link :to="{name: 'class-dashboard', params: {classId:this.classId}}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>

  <div class="">

    <div class="p-4 py-3">
      <!-- top row: shared date aligned right -->
      <div class="d-flex justify-content-end">
        <span :class="['p-1', 'd-inline-flex', 'align-items-center',
        isShared ? 'text-success' : 'text-secondary']">
          {{ isShared ? 'Shared on: ' + formatDateTime(updateTime) : 'Not shared' }}
        </span>
      </div>
      <!-- second row: name left / words total right -->
      <div class="d-flex justify-content-between align-items-center py-2">
        <span class="h5 d-inline-flex align-items-center editable-name p-1"
              ref="editableEl"
              contenteditable="true"
              @blur="onNameEdit">
          {{ name }}
        </span>
        <p class="fw-light mb-0">Words total: {{ wordsList.length }}</p>
      </div>
      <div class="py-2 border-bottom">
        <div class="d-flex justify-content-end gap-2">
          <button class="btn btn-md p-0 m-0" v-on:click="" title="Print pdf">
            <i class="bi bi-file-earmark-pdf me-1"></i>
            <span>Print pdf</span>
          </button>
          <button class="btn btn-md p-0 m-0" v-on:click="" title="Save as template">
            <i class="bi bi-plus-square me-1"></i>
            <span>Save as template</span>
          </button>
        </div>
      </div>
    </div>

    <search class="p-4" @add-to-vocabulary="handleAddToVocabulary" :vocab-word-ids="this.ids"/>
    <wordsheet-table class="p-4 pt-5" v-bind="{items: this.wordsList}">
      <template #actions="{ row }">
        <router-link
            :to="{ name: 'edit-explanation', params: { vocabId: vocabId, wordId: row?.wordId } }"
            class="btn btn-md"
            title="Edit word explanation"
        >
          <i class="bi bi-pencil-square"></i>
        </router-link>

        <button
            class="btn btn-md"
            @click="handleRemoveItemAction(row?.wordId)"
            title="Remove"
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