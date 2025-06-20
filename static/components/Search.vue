<script>
import SearchInput from "@/components/search/SearchInput.vue";
import SearchResultsPreview from "@/components/search/SearchResultsPreview.vue";

export default {
  components: {
    SearchInput,
    SearchResultsPreview
  },
  props: ['vocabId'],
  emits: ['add-to-vocabulary'],
  data() {
    return {
      foundExplanations: {}
    }
  },
  methods: {
    onSearch(result) {
      this.foundExplanations = result;
    },
    handleAddToVocabulary(wordExplanation) {
      this.$emit('add-to-vocabulary', wordExplanation);
    },
  },
};

</script>

<template>

  <div class="d-flex justify-content-center p-4">
    <div style="padding-top:7px;" class="col-md-4 form-group pull-right">
      <search-input @search-submit="onSearch"/>
    </div>
  </div>

  <search-results-preview @preview-item-selected="handleAddToVocabulary" v-bind="{previewData: this.foundExplanations}"/>

</template>

<style>
</style>
