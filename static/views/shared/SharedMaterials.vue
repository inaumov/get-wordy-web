<script>
import {fetchWordsheetList} from '@/js/classes-api.js';
import {applyCaption} from '@/js/utils.js'

export default {
  name: 'SharedMaterialsView',
  props: ['classId'],
  data() {
    return {
      classId: 'desna-4xRg7',
      name: '',
      wordsheetList: [],
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
    applyCaption(this.name || 'Wordsheet list')
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
    <h4 class="pb-4">Shared word sheets</h4>

    <div
        v-for="wordsheet in wordsheetList"
        :key="wordsheet['wordsheetId']"
        class="mb-4 bg-light bg-opacity-10 border border-danger-subtle rounded">

      <!-- make the whole element as clickable-->
      <router-link v-for="wordsheet in wordsheetList"
                   :key="wordsheet['wordsheetId']"
                   :to="{ name: 'wordsheet-preview', params: { classId: this.classId, wordsheetId : wordsheet['wordsheetId']}, query: { name: wordsheet['name'] }}"
                   class="row p-3 text-decoration-none text-dark">

        <span class="col-8">
          {{ wordsheet['name'] }}
        </span>

        <!-- displaying total count as a badge in a separate column -->
        <div class="col-4 text-end">
          <span class="badge bg-info rounded-pill">{{ wordsheet['wordsTotal'] }} words</span>
        </div>

      </router-link>

    </div>
  </div>

  <div v-else class="d-flex justify-content-center p-5">
    <p class="lead">Loading wordsheet list for the class...</p>
  </div>

</template>

<style>
#wordsheet-list > a:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}
</style>