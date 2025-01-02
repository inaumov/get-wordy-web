<script>
import {fetchWordsheetList} from '@/js/classes-api.js';

export default {
  name: 'WordsheetListView',
  props: ['classId'],
  data() {
    return {
      vocabularies: [],
    }
  },
  methods: {
    async getData() {
      const response = await fetchWordsheetList(this.classId);
      this.vocabularies = await response.json();
    }
  },
  mounted() {
    this.getData()
  },
  computed: {
    hasVocabs() {
      return this.vocabularies && this.vocabularies.length > 0;
    }
  }
};
</script>

<template>

  <div v-if="hasVocabs" class="container p-4" id="vocabularies">
    <h4 class="pb-4">Vocabularies</h4>

    <div
        v-for="vocabulary in vocabularies"
        :key="vocabulary['vocabId']"
        class="mb-4 bg-light bg-opacity-10 border border-danger-subtle rounded">

      <!-- make the whole element as clickable-->
      <router-link
          :to="{ name: 'wordsheet', params: { classId: this.classId, wordsheetId : vocabulary['vocabId']}, query: { name: vocabulary['name'] }}"
          class="row p-3 text-decoration-none text-dark">

        <span class="col-8">
          {{ vocabulary['name'] }}
        </span>

        <!-- displaying total count as a badge in a separate column -->
        <div class="col-3 text-end">
          <span class="badge bg-info rounded-pill">{{ vocabulary['wordsTotal'] }} words</span>
        </div>

        <!-- displaying whether the item is shared or not (optional property) -->
        <div class="col-1 text-end">
          <span
              v-if="vocabulary['isShared']"
              class="badge bg-success text-white"
              data-bs-toggle="tooltip"
              title="This vocabulary is available now for associated student">
            Shared
          </span>
          <span v-else class="badge bg-secondary text-white">
            Not shared
          </span>
        </div>
      </router-link>

    </div>
  </div>

  <div v-else class="d-flex justify-content-center p-5">
    <p class="lead">Loading vocabularies for the class...</p>
  </div>

</template>

<style>
#vocabularies > a:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}
</style>