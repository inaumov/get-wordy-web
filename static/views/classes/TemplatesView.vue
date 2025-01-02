<script>
import {fetchTemplates} from '@/js/classes-api.js';

export default {
  name: 'TemplatesView',
  data() {
    return {
      vocabularies: [],
    }
  },
  methods: {
    async getData() {
      const response = await fetchTemplates();
      this.vocabularies = await response.json();
    }
  },
  mounted() {
    this.getData()
  },
  computed: {
    hasTemplates() {
      return this.vocabularies && this.vocabularies.length > 0;
    }
  }
};
</script>

<template>

  <div v-if="hasTemplates" class="container p-4" id="templates">
    <h4 class="pb-4">Prepared vocabularies</h4>

    <div class="row pb-4">
      <div class="col">
        <div class="d-flex flex-column align-items-end">
          <button class="btn btn-lg" v-on:click="" title="Add template vocabulary">
            <i class="bi bi-file-plus"></i>
            Add template
          </button>
        </div>
      </div>
    </div>

    <div
        v-for="template in vocabularies"
        :key="template['templateId']"
        class="mb-4 bg-light bg-opacity-10 border border-danger-subtle rounded">

      <!-- make the whole element as clickable-->
      <router-link :to="{ name: 'template-preview', params: { templateId : template['templateId']}, query: { name: template['name'] }}"
                   class="row p-3 text-decoration-none text-dark">

        <span class="col-8">
          {{ template['name'] }}
        </span>

        <!-- displaying total count as a badge in a separate column -->
        <div class="col-4 text-end">
          <span class="badge bg-info rounded-pill">{{ template['wordsTotal'] }} words</span>
        </div>

      </router-link>

    </div>
  </div>

  <div v-else class="d-flex justify-content-center p-5">
    <p class="lead">Loading vocabulary templates...</p>
  </div>

</template>

<style>
#templates > a:hover {
  background-color: #f8f9fa;
  cursor: pointer;
}
</style>