<script>
import WordsheetTable from "@/components/classes/WordsheetTable.vue";
import SearchInput from "@/components/search/SearchInput.vue";
import SearchResultsPreview from "@/components/search/SearchResultsPreview.vue";

import {applyCaption} from '@/js/utils.js'
import {fetchWordsheet, updateReadiness, updateName, addToWordsheet} from '@/js/classes-api.js';

export default {
  components: {SearchResultsPreview, WordsheetTable, SearchInput},
  props: ['classId', 'wordsheetId'],
  data() {
    return {
      name: 'Wordsheet',
      wordsList: [],
      searchResult: {
        explanations: []
      }
    }
  },
  methods: {
    async getData() {
      const response = await fetchWordsheet(this.classId, this.wordsheetId);
      const wordsheet = await response.json();
      this.name = wordsheet['name'];
      this.wordsList = wordsheet['items'] || [];
    },
    onNameEdit(event) {
      let currVal = event.target.innerText.trim();
      const actualVal = this.name;
      if (currVal !== actualVal) {
        updateName(this.classId, this.wordsheetId, currVal)
            .then(response => {
              if (response.ok) {
                this.name = currVal; // update model
                console.log('Property [name] has been changed to:', currVal, ', for wordsheet id =', this.wordsheetId);
              }
              console.log("PATCH wordsheet has been requested. Response.status =", response.status);
            });
        return;
      }
      console.log('No changes detected in property [name] for wordsheet id =', this.wordsheetId);
    },
    onSearch(result) {
      this.searchResult = result;
    },
    onReady() {
      updateReadiness(this.classId, this.wordsheetId, true)
          .then(response => {
            if (response.ok) {
              console.log('Property [isShared] has been changed to:', true, ', for wordsheet id =', this.wordsheetId);
            }
            console.log("PATCH wordsheet has been requested. Response.status =", response.status);
          });
    },
    handleAddWord(word) {
      if (!this.wordsList.some((item) => item === word)) { // todo maybe more narrow check
        addToWordsheet(this.classId, this.wordsheetId, word)
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
    applyCaption(this.name || 'Wordsheet')
    console.log("Selected wordsheet: id = ", this.wordsheetId, ", name = ", this.name)
  }
};

</script>

<template>

  <div class="container">

    <div class="d-flex justify-content-center pt-5">
      <span contenteditable="true" class="h4 p-1" v-text="name" v-on:blur="onNameEdit">
      </span>
    </div>

    <div class="d-flex justify-content-center p-4">
      <div style="padding-top:7px;" class="col-md-4 form-group pull-right">
        <search-input @wordsheet-search-submit="onSearch" v-bind="{onSearchEventName: 'wordsheet-search-submit'}"/>
      </div>
    </div>

    <search-results-preview @add-to-wordsheet="handleAddWord" v-bind="{previewData: this.searchResult}"/>

    <wordsheet-table v-bind="{classId: this.classId, wordsheetId: this.wordsheetId, items: this.wordsList}"/>

    <!-- submit / back Buttons -->
    <div class="d-flex justify-content-end p-4">
      <div class="col-6 text-start">
        <router-link :to="{name: 'class-wordsheet-list'}" class="btn btn-secondary" title="Back">Back</router-link>
      </div>
      <div class="col-6 text-end">
        <button type="button" class="btn btn-primary border btn-md" v-on:click="onReady">
          Ready
        </button>
      </div>
    </div>

  </div>
</template>

<style>
</style>