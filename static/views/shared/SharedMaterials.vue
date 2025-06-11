<script>
import {getSharedVocabularies} from '@/js/shared-vocabs-api.js';

export default {
  name: 'SharedMaterialsView',
  components: {},
  props: ['classId', 'name'],
  data() {
    return {
      vocabularies: [],
    }
  },
  methods: {
    async getData() {
      const response = await getSharedVocabularies(this.classId);
      this.vocabularies = await response.json();
    }
  },
  mounted() {
    this.getData()
  },
  computed: {
    hasVocabs() {
      return this.vocabularies && this.vocabularies.length > 0;
    },
  }
};
</script>

<template>

  <div class="py-4" id="vocabularies">
    <h5 class="py-2">{{ name }}</h5>

    <div v-if="hasVocabs" v-for="item in vocabularies"
        :key="item['vocabId']"
        class="mb-4 bg-light bg-opacity-10 border border-danger-subtle rounded">

      <!-- make the whole element as clickable-->
      <router-link :to="{ name: 'vocabulary-preview', params: { classId: this.classId, vocabId : item['vocabId']}}"
                   class="row p-3 text-decoration-none text-dark">

        <span class="col-8">
          {{ item['name'] }}
        </span>

        <!-- displaying total count as a badge in a separate column -->
        <div class="col-4 text-end">
          <span class="badge bg-info rounded-pill">{{ item['wordsTotal'] }} words</span>
        </div>

      </router-link>

    </div>

    <div v-else class="d-flex justify-content-center align-items-center vh-25">
      <div class="text-center w-50">
        <p class="lead">No shared vocabularies assigned so far...</p>
      </div>
    </div>

  </div>

</template>

<style>
#vocabularies > a:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}
</style>