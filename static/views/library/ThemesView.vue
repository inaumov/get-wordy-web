<script>
import {createTheme, fetchThemes} from '@/js/themes-api.js';
import CreateModal from "@/components/modal/CreateVocabulary.vue";

export default {
  name: 'Themes',
  components: {CreateModal},
  data() {
    return {
      themes: [],
    };
  },
  computed: {
    hasThemes() {
      return this.themes && this.themes.length > 0;
    },
  },
  methods: {
    async getData() {
      try {
        const response = await fetchThemes();
        this.themes = await response.json();
      } catch (err) {
        console.error('Failed to load themes:', err);
      }
    },
    async addTheme(name) {
      let response = await createTheme(name);

      if (!response.ok) {
        const err = await response.json();
        const error = new Error(err.message);
        Object.assign(error, {status: response.status});
        throw error;
      }
      const created = await response.json();
      this.themes.unshift(created);
    },
    showCreateModal() {
      this.$refs.createModal.open();
    },
    randomColor(id) {
      const colors = ['#ffd6a5', '#fdffb6', '#caffbf', '#9bf6ff', '#a0c4ff', '#bdb2ff', '#ffc6ff'];
      const index = id.toString().split('').reduce((sum, c) => sum + c.charCodeAt(0), 0) % colors.length;
      return colors[index];
    },
    themeRoute(theme) {
      return {
        name: 'theme-preview',
        params: { themeId: theme.themeId }
      }
    }
  },
  mounted() {
    this.getData();
  },
};
</script>

<template>
  <div class="p-4 d-flex justify-content-start">
    <h4 class="m-0">Library</h4>
  </div>
  <!-- card grid -->
  <div v-if="hasThemes" id="themes" class="p-4">
    <div class="d-flex justify-content-end mb-3" style="gap: 20px">
      <button class="btn btn-sm btn-outline-primary" @click="showCreateModal">
        <i class="bi bi-plus"></i> New Theme
      </button>
    </div>
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div v-for="theme in themes" :key="theme.themeId" class="col">
        <router-link
            :to="themeRoute(theme)"
            class="card h-100 border-0 shadow-sm text-decoration-none text-dark theme-card"
        >
          <div class="card-body d-flex flex-column">
            <div class="d-flex align-items-center mb-2">
              <h5 class="card-title mb-0">{{ theme.name }}</h5>
            </div>
            <div class="mt-auto d-flex justify-content-between align-items-center">
              <span class="badge bg-info-subtle text-dark">
                {{ theme.wordsTotal }} words
              </span>
              <span v-if="theme.status === 'READY'" class="badge bg-success-subtle text-dark">
                {{ theme.status }}
              </span>
              <span v-else class="badge bg-secondary-subtle text-dark">
                {{ theme.status }}
              </span>
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
  <div v-else class="p-4 text-center mt-5">
    <p class="lead">You haven’t created any theme yet.</p>
    <button class="btn btn-sm btn-outline-primary" @click="showCreateModal">Create first theme</button>
  </div>
  <CreateModal ref="createModal" :on-submit="addTheme" modal-name="theme"/>

</template>

<style scoped>
.theme-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 1rem;
}

.theme-card:hover {
  transform: translateY(-3px);
}

.badge.bg-info-subtle {
  background-color: #d0ebff;
}
</style>