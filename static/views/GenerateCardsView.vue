<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";

import {generateCards} from '@/js/cards.js';
import {textToArray} from '@/js/utils.js';
import SearchInput from "@/components/search/SearchInput.vue";
import SearchResultsPreview from "@/components/search/SearchResultsPreview.vue";

export default {
  components: {SearchResultsPreview, SearchInput, ActionButtons},
  props: ['dictionaryId'],
  data() {
    return {
      searchResult: {
        explanations: []
      }
    }
  },
  methods: {
    onSearch(result) {
      // generateCards(this.dictionaryId, wordsArr);
      this.searchResult = result;
    },
    handleAddWord() {

    },
  },
};

</script>

<template>

  <action-buttons/>

  <div class="d-flex justify-content-center p-4" id="generate-cards-panel">
    <div style="padding-top:7px;" class="col-md-4 form-group pull-right">
      <search-input @generate-cards-result="onSearch" v-bind="{onSearchEventName: 'generate-cards-result'}"/>
    </div>
  </div>

  <search-results-preview @add-to-wordsheet="handleAddWord" v-bind="{previewData: this.searchResult}"/>

</template>

<style>
div#generate-cards-panel i {
  color: rgb(185, 87, 84)
}
</style>
