<script>
import SearchTheme from "@/components/SearchTheme.vue";
import {saveTheme} from "@/js/themes-api.js";

export default {
  components: {SearchTheme},
  methods: {
    /**
     * Parent decides how to save
     */
    async onReady(themeName, wordIds) {
      const resp = await saveTheme({themeName, wordIds});
      if (resp.ok) {
        console.log("Saved successfully");
      }
    }
  }
};
</script>

<template>
  <div class="d-flex justify-content-start m-4">
    <router-link
        :to="{ name: 'themes' }"
        class="btn btn-secondary"
        title="Back"
    >
      Back
    </router-link>
  </div>

  <div class="p-4">

    <!-- Child emits: save-theme and save-selected -->
    <search-theme
        @save-theme="onReady"
        @save-selected="onReady"
    />

  </div>
</template>

<style>
</style>