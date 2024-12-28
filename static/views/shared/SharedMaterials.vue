<script>
import {fetchWordsheetList} from '@/js/classes-api.js';
import {applyCaption} from '@/js/utils.js'

export default {
  name: 'SharedMaterialsView',
  components: {},
  props: ['classId'],
  data() {
    return {
      classId: 'desna-4xRg7',
      name: '',
      wordsheetList: [],
      dictionary: {
        "dictionaryId": 5,
        "name": "Active vocabulary",
        "picture": "https://cdn-icons-png.flaticon.com/512/709/709418.png",
        "cardsTotal": 6
      },
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
    },
    hasActiveVocabulary() {
      return true;
    }
  }
};
</script>

<template>

  <div v-if="hasActiveVocabulary" class="container p-4" id="active-vocabulary">
    <h4 class="pb-4">Currently learning words</h4>

    <div>
      <div id="dictionary" class="card text-center">
        <img v-bind:src="dictionary['picture']" class="card-img-top mx-auto d-block" v-bind:alt="dictionary['name']">
        <div class="card-body">
          <h5 class="card-title">{{ dictionary['name'] }}</h5>
          <router-link class="btn btn-primary"
                       :to="{ name: 'all-cards', params: { dictionaryId : dictionary['dictionaryId']}, query: { dictionaryName: dictionary['name'] }}">
            {{ dictionary['cardsTotal'] }}
          </router-link>
        </div>
      </div>
    </div>
  </div>

  <div v-if="hasWordsheet" class="container p-4" id="wordsheet-list">
    <h4 class="pb-4">All shared word sheets</h4>

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