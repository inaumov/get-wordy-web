<script>
import WordsheetTable from "@/components/classes/WordsheetTable.vue";

import {applyCaption} from '@/js/utils.js'
import {fetchWordSheet} from '@/js/classes-api.js';
import {searchWordData} from '@/js/wordsheet-api.js';

export default {
  components: {WordsheetTable},
  props: ['classId', 'wordsheetId'],
  data() {
    return {
      name: 'rr',
      wordsheetId: 0,
      wordsList: []
    }
  },
  methods: {
    async getData() {
      const response = await fetchWordSheet(this.classId, this.wordsheetId);
      const wordsheet = await response.json();
      this.wordsList = wordsheet['items'];
    },
    onSearch: function () {
      let form = document.getElementById('search-words-form');
      let formData = new FormData(form);
      let wordSearchRequest = formData.get('words');
      const newItem = searchWordData(this.wordsheetId, wordSearchRequest);
      this.wordsList = [...this.wordsList, newItem];
    },
    onReady() {

    }
  },
  mounted() {
    this.getData()
    applyCaption(this.name)
    console.log("Selected wordsheet: id = ", this.wordsheetId, ", name = ", this.name)
  }
};

</script>

<template>

  <div class="container">

    <div class="d-flex justify-content-center p-4">
      <div style="padding-top:7px;" class="col-md-4 form-group pull-right">
        <form id="search-words-form" action="" method="get" class="form-inline" v-on:submit.prevent="onSearch">
          <div class="form-group">
            <div class="input-group">
              <input type="text" class="form-control" name="words" placeholder="Search for..." autocomplete="off"
                     required>
              <span class="input-group-btn">
                <button type="submit" class="btn btn-md btn-default border">
                  <i class="bi bi-search"></i>
                </button>
              </span>
            </div>
          </div>
        </form>
      </div>
    </div>

    <wordsheet-table :key="this.wordsList.length" v-bind="{wordsheetId: this.wordsheetId, items: this.wordsList}"/>

    <div class="d-flex justify-content-end p-4">
      <button type="button" class="btn btn-primary border btn-md" v-on:click="onReady">
        Ready
      </button>
    </div>

  </div>
</template>

<style>
</style>