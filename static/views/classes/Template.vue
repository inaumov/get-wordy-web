<script>
import WordsheetTable from "@/components/classes/WordsheetTable.vue";
import SearchInput from "@/components/search/SearchInput.vue";

import {fetchTemplate, saveTemplate, updateTemplateName} from '@/js/templates-api.js';

export default {
  components: {WordsheetTable, SearchInput},
  props: ['templateId'],
  data() {
    return {
      name: '',
      isShared: false,
      wordsList: [],
    }
  },
  methods: {
    async getData() {
      const response = await fetchTemplate(this.templateId);
      const template = await response.json();
      this.name = template['name'];
      this.wordsList = template['words'] || [];
    },
    onNameEdit(event) {
      let currVal = event.target.innerText.trim();
      const actualVal = this.name;
      if (currVal !== actualVal) {
        updateTemplateName(this.templateId, currVal)
            .then(response => {
              if (response.ok) {
                this.name = currVal; // update model
                console.log('Property [name] has been changed to:', currVal, ', for template id =', this.templateId);
              }
              console.log("PATCH template has been requested. Response.status =", response.status);
            });
        return;
      }
      console.log('No changes detected in property [name] for template id =', this.templateId);
    },
    onSearch(searchResult) {
      console.log(searchResult);
    },
    onReady() {
      saveTemplate(this.templateId, this.name, this.wordsList.map((value) => value.templateId))
          .then(response => {
            if (response.ok) {
            }
          });
    },
    handleRemoveItemAction(wordId) {
      const index = this.wordsList.findIndex(obj => obj['wordId'] === wordId)
      this.wordsList.splice(index, 1)
    },
  },
  mounted() {
    this.getData()
  }
};

</script>

<template>
  <div class="d-flex justify-content-start p-4">
    <router-link :to="{name: 'templates'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>

  <div class="container">

    <div class="d-flex justify-content-left p-2">
      <span contenteditable="true" class="h4 p-1" v-text="this.name" v-on:blur="onNameEdit">
      </span>
    </div>

    <div class="d-flex justify-content-center p-2">
      <div style="padding-top:7px;" class="col-md-4 form-group pull-right">
        <search-input @wordsheet-search-submit="onSearch" v-bind="{onSearchEventName: 'wordsheet-search-submit'}"/>
        <small class="text-muted">Enter a topic or theme and click search</small>
      </div>
    </div>

    <wordsheet-table class="p-2" v-bind="{items: this.wordsList}">
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

    <!-- submit -->
    <div class="d-flex justify-content-end p-2">
      <button type="button" class="btn btn-primary border btn-md" v-on:click="onReady">
        Save
      </button>
    </div>

  </div>
</template>

<style>
</style>