<script>
import WordsheetTable from "@/components/classes/WordsheetTable.vue";
import SearchInput from "@/components/search/SearchInput.vue";
import SearchResultsPreview from "@/components/search/SearchResultsPreview.vue";

import {getVocabulary, updateReadiness, updateVocabularyName, addToVocabulary} from '@/js/classes-api.js';

export default {
  components: {SearchResultsPreview, WordsheetTable, SearchInput},
  props: ['classId', 'vocabId'],
  data() {
    return {
      name: '',
      wordsList: [],
      searchResult: {
        explanations: []
      }
    }
  },
  methods: {
    async getData() {
      const response = await getVocabulary(this.classId, this.vocabId);
      const vocabulary = await response.json();
      this.name = vocabulary['name'];
      this.wordsList = vocabulary['words'] || [];
    },
    onNameEdit(event) {
      let currVal = event.target.innerText.trim();
      const actualVal = this.name;
      if (currVal !== actualVal) {
        updateVocabularyName(this.classId, this.vocabId, currVal)
            .then(response => {
              if (response.ok) {
                this.name = currVal; // update model
                console.log('Property [name] has been changed to:', currVal, ', for vocabulary id =', this.vocabId);
              }
              console.log("PATCH vocabulary has been requested. Response.status =", response.status);
            });
        return;
      }
      console.log('No changes detected in property [name] for vocabularies id =', this.vocabId);
    },
    onSearch(result) {
      this.searchResult = result;
    },
    onReady() {
      updateReadiness(this.classId, this.vocabId, true)
          .then(response => {
            if (response.ok) {
              console.log('Property [isShared] has been changed to:', true, ', for vocabulary id =', this.vocabId);
            }
            console.log("PATCH vocabularies has been requested. Response.status =", response.status);
          });
    },
    handleAddWord(word) {
      if (!this.wordsList.some((item) => item === word)) { // todo maybe more narrow check
        addToVocabulary(this.classId, this.vocabId, word)
            .then(response => {
              if (response.ok) {
                let itemAdded = response.json();
                this.wordsList.push(itemAdded);
                console.log("POST new word has been requested. Response.id =", itemAdded['id']);
              }
            })
      } else {
        alert('This word is already added.');
      }
    },
  },
  mounted() {
    this.getData()
  }
};

</script>

<template>
  <div class="d-flex justify-content-start p-4">
    <router-link :to="{name: 'class-details'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>

  <div class="container">

    <div class="d-flex justify-content-center py-2">
      <span contenteditable="true" class="h4 p-1" v-text="name" v-on:blur="onNameEdit">
      </span>
    </div>

    <div class="d-flex justify-content-center p-4">
      <div style="padding-top:7px;" class="col-md-4 form-group pull-right">
        <search-input @wordsheet-search-submit="onSearch" v-bind="{onSearchEventName: 'wordsheet-search-submit'}"/>
      </div>
    </div>

    <search-results-preview @add-to-wordsheet="handleAddWord" v-bind="{previewData: this.searchResult}"/>

    <wordsheet-table v-bind="{classId: this.classId, vocabId: this.vocabId, items: this.wordsList}"/>

    <!-- submit -->
    <div class="d-flex justify-content-end p-4">
        <button type="button" class="btn btn-primary border btn-md" v-on:click="onReady">
          Ready
        </button>
    </div>

  </div>
</template>

<style>
</style>