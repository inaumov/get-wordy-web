<script>
import {createVocabulary, fetchUserVocabularies} from "@/js/dictionaries.js";
import {formatDateTime} from "@/js/utils.js";
import CreateVocabularyModal from "@/components/modal/CreateVocabulary.vue";

export default {
  name: 'DictionariesView',
  components: {
    CreateVocabularyModal
  },
  data() {
    return {
      vocabularies: [],
      favorites: []
    };
  },
  computed: {
    sortedVocabularies() {
      const sortedNonFav = this.vocabularies.sort((a, b) =>
          new Date(b.updateTime) - new Date(a.updateTime)
      );
      return [...this.favorites, ...sortedNonFav];
    }
  },
  methods: {
    async getData() {
      const response = await fetchUserVocabularies();
      const all = await response.json();
      const [favorites, vocabularies] = all.reduce(
          ([fav, nonFav], item) => {
            item.type === 'FAV' ? fav.push(item) : nonFav.push(item);
            return [fav, nonFav];
          },
          [[], []]
      );
      this.vocabularies = vocabularies;
      this.favorites = favorites;
    },
    formatDateTime,
    getColor(type) {
      switch (type) {
        case 'FAV':
          return '#fff8d6'; // pastel yellow
        case 'OWN':
          return '#e3f2fd'; // pastel blue
        case 'SHARED':
          return '#e8f5e9'; // pastel green
        default:
          return '#ffffff';
      }
    },
    showCreateModal() {
      this.$refs.createModal.open();
    },
    async handleCreateVocabulary(name) {
      let response = await createVocabulary(name);

      if (!response.ok) {
        const err = await response.json();
        const error = new Error(err.message);
        Object.assign(error, {status: response.status});
        throw error;
      }
      const created = await response.json();
      this.vocabularies.unshift(created);
    }
  },
  mounted() {
    this.getData();
  }
};
</script>

<template>

  <div class="vocabulary-list p-4">
    <!-- new vocab creation block -->
    <div v-if="!vocabularies || vocabularies.length === 0" class="text-center mt-5">
      <p class="lead">You haven’t created any vocabularies yet.</p>
      <button class="btn btn-sm btn-outline-primary" @click="showCreateModal">Create Your First Vocabulary</button>
    </div>
    <div v-else class="d-flex justify-content-between align-items-center mb-3">
      <h4 class="mb-0">My Vocabularies</h4>
      <button class="btn btn-sm btn-outline-primary" @click="showCreateModal">
        <i class="bi bi-plus"></i> New Vocabulary
      </button>
    </div>
    <CreateVocabularyModal ref="createModal" :createAction="handleCreateVocabulary"/>
    <!-- all user own and shared vocabularies -->
    <div
        v-for="vocab in sortedVocabularies"
        :key="vocab.vocabId"
        class="vocab-item p-3 mb-3 rounded shadow-sm"
        :style="{ backgroundColor: getColor(vocab.type) }"
    >
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h5 class="mb-0">{{ vocab.name }}</h5>
        <small class="text-muted">Last Updated: {{ formatDateTime(vocab.updateTime) }}</small>
      </div>
      <div class="text-muted mb-1">
        <span v-if="vocab.className">{{ vocab.className }} &nbsp;•&nbsp;</span>
        {{ vocab.wordsTotal }} words
      </div>

      <div v-if="vocab.wordsTotal > 0" class="d-flex align-items-center gap-2 mb-2">
        <span class="text-muted">Progress:</span>
        <div class="progress flex-grow-1" style="height: 8px;">
          <div
              class="progress-bar"
              :style="{ width: vocab.learningProgress + '%', backgroundColor: '#3b82f6' }"
              role="progressbar"
              :aria-valuenow="vocab.learningProgress"
              aria-valuemin="0"
              aria-valuemax="100"
          ></div>
        </div>
        <span class="text-muted small" style="min-width: 40px; text-align: right;">
          {{ vocab.learningProgress }}%
        </span>
      </div>

      <div class="d-flex flex-wrap gap-2 mt-2">
        <router-link
            class="btn btn-sm btn-success text-white"
            :to="{ name: 'all-cards', params: { vocabId: vocab.vocabId }, query: { vocabName: vocab.name } }"
        >
          <i class="bi bi-card-list"></i> View Words
        </router-link>

        <router-link
            class="btn btn-sm btn-success text-white"
            :to="{ name : 'play-game', params: { vocabId: vocab.vocabId }, query: { vocabName: vocab.name }}"
            :disabled="vocab.wordsTotal === 0"
            title="You wanna play? let's play"
        >
          <i :class="['bi', vocab.learningProgress > 0 ? 'bi-repeat' : 'bi-arrow-90deg-right']"></i>
          {{ vocab.learningProgress > 0 ? 'Continue Learning' : 'Start Learning' }}
        </router-link>

      </div>

    </div>
  </div>
</template>

<style scoped>
.vocab-item {
  transition: background-color 0.2s ease;
}

div.progress {
  margin-top: 3px; /* dirty fix to keep centered */
}

</style>
