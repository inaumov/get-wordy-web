<script>
import Search from "@/components/Search.vue";
import RenameModal from "@/components/modal/RenameModal.vue";
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
    RenameModal,
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
    openRenameModal() {
      this.$refs.renameModal.open();
    },
    async renameVocabulary(newName) {
      let response = await updateVocabularyName(this.classId, this.vocabId, newName);
      if (response.ok) {
        this.name = newName; // update model
        console.log('Name has been changed to:', newName, ', for vocabulary id =', this.vocabId);
        // todo success notification
      } else {
        alert('Failed')
        // todo failure notification
      }
    },
    async deleteVocabulary() {
      // todo delete vocabulary implementation
    },
    async addToLibrary() {
      // todo save vocabulary as theme into library
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
    <router-link :to="{name: 'class-dashboard', params: {classId:this.classId}}" class="btn btn-secondary" title="Back">
      Back
    </router-link>
  </div>

  <div class="">

    <div class="p-4 py-3">

      <!-- vocab name left / actions dropdown right -->

      <div class="d-flex justify-content-between align-items-start pb-5 border-bottom">

        <div class="vocab-card p-3 rounded w-75">
          <div class="d-flex align-items-center justify-content-between">
            <!-- vocab name -->
            <span class="name">{{ name }}</span>
            <!-- shared date aligned right -->
            <span v-if="isShared" class="text-success">
              Shared<i class="bi bi-dot"></i>{{ formatDateTime(updateTime) }}
            </span>
            <span v-else class="text-secondary">
              <i class="bi bi-dot"></i>Not shared
            </span>
          </div>
          <!-- words total -->
          <div v-if="wordsList.length" class="my-1">
            <span class="text-muted">Words total: {{ wordsList.length }}</span>
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
              <a class="dropdown-item" href="#" @click.prevent="addToLibrary">
                Add to library
              </a>
            </li>

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
              <a class="dropdown-item" style="color: firebrick" href="#" @click.prevent="deleteVocabulary">
                Delete
              </a>
            </li>

          </ul>
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

    <RenameModal
        ref="renameModal"
        modal-name="vocabulary"
        :current-name="this.name"
        :on-submit="renameVocabulary"
    />

  </div>
</template>

<style scoped>
.vocab-card {
  border-radius: 0.5rem;
  background: lightgrey;
}

.vocab-card .name {
  font-size: 1.25rem;
  font-weight: bold;
}

.dropdown-toggle {
  background-color: #f8f9fa;
}

.dropdown-item:hover {
  background-color: #e9ecef;
}
</style>