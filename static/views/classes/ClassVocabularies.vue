<script>
import {getVocabularies} from '@/js/classes-api.js';
import {formatDateTime} from "@/js/utils.js";

export default {
  name: 'ClassVocabularies',
  props: ['classId'],
  data() {
    return {
      vocabularies: [],
    }
  },
  methods: {
    async getData() {
      const response = await getVocabularies(this.classId);
      this.vocabularies = await response.json();
    },
    formatDateTime
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
  <div>
    <h5 class="pb-2 border-bottom">Vocabularies</h5>
    <ul v-if="hasVocabs" class="pt-2 list-group spaced-items" id="vocabularies">

      <li
          v-for="vocabulary in vocabularies"
          :key="vocabulary['vocabId']"
          class="list-group-item vocab-item border rounded px-3 py-1">

        <div class="row d-flex align-items-center">
          <router-link
              :to="{ name: 'vocabulary', params: { classId: this.classId, vocabId : vocabulary['vocabId']}}"
              class="col py-2 text-decoration-none text-dark">
            <span class="text-start">
              {{ vocabulary['name'] }}
            </span>
          </router-link>
          <!-- displaying total count as a badge -->
          <div class="col-auto text-end">
            <span class="badge bg-success rounded-pill">{{ vocabulary['wordsTotal'] }} words</span>
          </div>
          <!-- displaying whether the item is shared or not (optional property) -->
          <div class="col text-end">
            <span v-if="vocabulary.isShared" class="text-success">
              Last update: {{ formatDateTime(vocabulary['updateTime']) }}
            </span>
            <span v-else class="text-secondary">
              Not shared
            </span>
          </div>
        </div>
      </li>
    </ul>

    <div v-else class="d-flex justify-content-center p-5">
      <p class="lead">Loading vocabularies for the class...</p>
    </div>
  </div>
</template>

<style>
#vocabularies > a:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}

.spaced-items .vocab-item + .vocab-item {
  margin-top: 0.75rem;
}
</style>