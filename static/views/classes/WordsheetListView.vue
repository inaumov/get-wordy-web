<script>
import {fetchWordsheetList} from '@/js/classes-api.js';
import {applyCaption} from '@/js/utils.js'

export default {
  name: 'WordsheetListView',
  props: ['classId'],
  data() {
    return {
      name: '',
      wordsheetList: [
        {
          wordsheetId: 0,
          name: '',
          wordsTotal: 0,
          isShared: false
        }
      ],
    }
  },
  methods: {
    async getData() {
      const response = await fetchWordsheetList(this.classId);
      this.wordsheetList = await response.json();
    }
  },
  mounted() {
    this.getData()
    applyCaption(this.name || 'Wordsheet(s)')
  },
  computed: {
    hasWordsheet() {
      return this.wordsheetList && this.wordsheetList.length > 0;
    }
  }
};
</script>

<template>

  <div v-if="hasWordsheet" class="container p-4" id="wordsheet-list">
    <div
        v-for="wordsheet in wordsheetList"
        :key="wordsheet['wordsheetId']"
        class="mb-4 bg-light bg-opacity-10 border border-danger-subtle rounded">

      <!-- make the whole element as clickable-->
      <router-link
          :to="{ name: 'wordsheet', params: { classId: this.classId, wordsheetId : wordsheet['wordsheetId']}, query: { name: wordsheet['name'] }}"
          class="row p-5 text-decoration-none text-dark">

        <div class="col-8">
          <h5>{{ wordsheet['name'] }}</h5>
        </div>

        <!-- displaying total count as a badge in a separate column -->
        <div class="col-3 text-end">
          <span class="badge bg-info rounded-pill">{{ wordsheet['wordsTotal'] }}</span>
        </div>

        <!-- displaying whether the item is shared or not (optional property) -->
        <div class="col-1 text-end">
          <span
              v-if="wordsheet['isShared']"
              class="badge bg-success text-white"
              data-bs-toggle="tooltip"
              title="This wordsheet is available now for associated student">
            Shared
          </span>
          <span v-else class="badge bg-secondary text-white">
            Not shared yet
          </span>
        </div>
      </router-link>

    </div>
  </div>

  <div v-else class="d-flex justify-content-center p-5">
    <p class="lead">Loading wordsheet list for the class...</p>
  </div>

</template>

<style>
#wordsheet-list > div:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}
</style>