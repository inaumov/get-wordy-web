<script>
import {fetchThemes} from '@/js/themes-api.js';

export default {
  name: 'Themes',
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
    formatDate(dateStr) {
      if (!dateStr) return '';
      return new Date(dateStr).toLocaleDateString();
    },
    randomColor(id) {
      const colors = ['#ffd6a5', '#fdffb6', '#caffbf', '#9bf6ff', '#a0c4ff', '#bdb2ff', '#ffc6ff'];
      const index = id.toString().split('').reduce((sum, c) => sum + c.charCodeAt(0), 0) % colors.length;
      return colors[index];
    },
  },
  mounted() {
    this.getData();
  },
};
</script>

<template>
  <!-- header -->
  <div class="p-4 d-flex justify-content-start">
    <h4>Library</h4>
  </div>
  <div v-if="hasThemes === false" class="p-4 text-center mt-5">
    <p class="lead">You haven’t created any theme yet.</p>
    <router-link
        :to="{ name: 'theme-new' }"
        class="btn btn-sm btn-outline-primary"
    >Create First Theme
    </router-link>
  </div>
  <div v-else class="p-4 d-flex justify-content-end">
    <router-link
        :to="{ name: 'theme-new' }"
        class="btn btn-sm btn-outline-primary">
      <i class="bi bi-plus"></i> New Theme
    </router-link>
  </div>

  <!-- card grid -->
  <div id="themes" class="p-4">
    <div v-if="hasThemes" class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div v-for="theme in themes" :key="theme.themeId" class="col">
        <router-link
            :to="{ name: 'theme-preview', params: { themeId: theme.themeId } }"
            class="card h-100 border-0 shadow-sm text-decoration-none text-dark theme-card"
        >
          <div class="card-body d-flex flex-column">
            <div class="d-flex align-items-center mb-2">
              <div
                  class="theme-icon me-3 flex-shrink-0"
                  :style="{ backgroundColor: theme.color || randomColor(theme.themeId) }"
              ></div>
              <h5 class="card-title mb-0 flex-grow-1">{{ theme.name }}</h5>
            </div>

            <div class="mt-auto d-flex justify-content-between align-items-center">
              <span class="badge bg-info-subtle text-dark">
                {{ theme.wordsTotal ? theme.wordsTotal : 0 }} words
              </span>
              <small class="text-muted">
                {{ theme.lastModified ? `Updated ${formatDate(theme.lastModified)}` : '' }}
              </small>
            </div>
          </div>
        </router-link>
      </div>
    </div>

  </div>

</template>

<style scoped>
.theme-card {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-radius: 1rem;
}

.theme-card:hover {
  transform: translateY(-3px);
}

.theme-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

.badge.bg-info-subtle {
  background-color: #d0ebff;
}
</style>