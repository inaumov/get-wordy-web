<script>
import {searchWordData} from "@/js/words-search-api.js";

export default {
  props: ['onSearchEventName'],
  methods: {
    async onSearch() {
      let form = document.getElementById('search-words-form');
      let inputField = document.getElementById('ai-search-input');
      // send request and clean input field on success
      let formData = new FormData(form);
      let searchRequest = formData.get('words');
      const response = await searchWordData(searchRequest)
      let searchResult = await response.json();
      if (response.ok && searchResult.hasOwnProperty('explanations') && searchResult.explanations.length > 0) {
        inputField.value = '';
        this.$emit(this.onSearchEventName, searchResult);
      }
    },
  }
}
</script>

<template>
  <form id="search-words-form" action="" method="get" class="form-inline" v-on:submit.prevent="onSearch">
    <div class="form-group">
      <div class="input-group">
        <input id="ai-search-input" type="text" class="form-control" name="words" placeholder="Search for..."
               autocomplete="off"
               required>
        <span class="input-group-btn">
                <button type="submit" class="btn btn-md btn-default border">
                  <i class="bi bi-search"></i>
                </button>
              </span>
      </div>
    </div>
  </form>
</template>

<style scoped>

</style>