<script>
import {createVocabulary, fetchUserVocabularies} from "@/js/dictionaries.js";
import {getSharedVocabularies} from '@/js/shared-vocabs-api.js';
import {formatDateTime} from "@/js/utils.js";
import CreateVocabularyModal from "@/components/modal/CreateVocabulary.vue";
import {getUserClasses} from "@/js/auth-check.js";

export default {
  name: 'AllUserVocabularies',
  components: {
    CreateVocabularyModal
  },
  data() {
    return {
      activeClass: {},
      vocabularies: []
    };
  },
  computed: {
    hasVocabs() {
      return this.vocabularies && this.vocabularies.length > 0;
    }
  },
  methods: {
    async fetchActiveClass() {
      const response = await getUserClasses();
      const assignedClasses = await response.json();
      return assignedClasses
          .filter(classInfo => classInfo.isActive);
    },
    async loadAllShared() {
      const myClasses = await this.fetchActiveClass();
      const allDataArrays = await Promise.all(
          myClasses.map(async (myClass) => {
            const res = await getSharedVocabularies(myClass.classId);
            const vocabList = await res.json();
            // inject classId into each element
            return vocabList.map(item => ({
              ...item,
              classId: myClass.classId
            }));
          })
      );
      // flattens [[...], [...], ...] to one []
      return allDataArrays.flat();
    },
    async getData() {
      const response = await fetchUserVocabularies();
      const allOwn = await response.json();
      const allShared = await this.loadAllShared();
      const combined = [...allOwn, ...allShared];
      this.vocabularies = this.sortVocabularies(combined);
    },
    sortVocabularies(vocabList) {
      const fav = vocabList.find(v => v.type === 'FAV');
      const rest = vocabList
          .filter(v => v.type !== 'FAV')
          .sort((a, b) => new Date(b.updateTime) - new Date(a.updateTime));
      return fav ? [fav, ...rest] : rest;
    },
    formatDateTime,
    getColor(accessType) {
      switch (accessType) {
        case 'FAV':
          return '#fff8d6';
        case 'SHARED':
          return '#e8f5e9';
        default:
          return '#e3f2fd';
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
    <div v-if="hasVocabs === false" class="text-center mt-5">
      <p class="lead">You haven’t created any vocabularies yet.</p>
      <button class="btn btn-outline-primary" @click="showCreateModal">Create Your First Vocabulary</button>
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
        v-for="vocab in vocabularies"
        :key="vocab.vocabId"
        class="vocab-item p-3 mb-3 rounded shadow-sm"
        :style="{ backgroundColor: getColor(vocab.accessType) }"
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

      <div v-if="vocab.accessType === 'FAV' && vocab.wordsTotal > 0" class="d-flex flex-wrap gap-2 mt-2">
        <router-link
            class="btn btn-sm btn-success text-white"
            :to="{ name: 'view-cards', params: { vocabId: vocab.vocabId }}"
            title="View favorite words"
        >
          <i class="bi bi-card-list me-2"></i>
          <span>View Words</span>
        </router-link>
        <router-link
            v-if="vocab.wordsTotal > 0"
            class="btn btn-md btn-success text-white"
            :to="{ name : 'play-game', params: { vocabId: vocab.vocabId }}"
            title="You wanna play? let's play"
        >
          <i :class="['bi', vocab.learningProgress > 0 ? 'bi-repeat' : 'bi-arrow-90deg-right']"></i>
          {{ vocab.learningProgress > 0 ? 'Continue Learning' : 'Start Learning' }}
        </router-link>
      </div>

      <div v-if="vocab.accessType === 'SHARED'" class="d-flex flex-wrap gap-2 mt-2">
        <router-link
            class="btn btn-md btn-success text-white"
            :to="{ name: 'shared-vocabulary-preview', params: { classId: vocab.classId, vocabId: vocab.vocabId }}"
        >
          <i class="bi bi-card-list me-2"></i>
          <span>View Words</span>
        </router-link>
        <router-link
            v-if="vocab.wordsTotal > 0"
            class="btn btn-md btn-success text-white"
            :to="{ name : 'play-game', params: { vocabId: vocab.vocabId }}"
            title="You wanna play? let's play"
        >
          <i :class="['bi', vocab.learningProgress > 0 ? 'bi-repeat' : 'bi-arrow-90deg-right']"></i>
          {{ vocab.learningProgress > 0 ? 'Continue Learning' : 'Start Learning' }}
        </router-link>
      </div>

      <div v-if="vocab.accessType === 'OWN'" class="d-flex flex-wrap gap-2 mt-2">
        <router-link
            class="btn btn-md btn-success text-white"
            :to="{ name: 'view-cards', params: { vocabId: vocab.vocabId }}"
        >
          <i class="bi bi-card-list me-2"></i>
          <span v-if="vocab.wordsTotal > 0">View Words</span>
          <span v-else>Add Words</span>
        </router-link>

        <router-link
            v-if="vocab.wordsTotal > 0"
            class="btn btn-md btn-success text-white"
            :to="{ name : 'play-game', params: { vocabId: vocab.vocabId }}"
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
