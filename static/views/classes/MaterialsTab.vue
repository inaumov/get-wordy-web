<script setup>
import ClassVocabularies from "@/views/classes/ClassVocabularies.vue";

</script>

<template>
  <div class="row p-4">
    <div class="col">
      <div class="d-flex flex-column align-items-end">
        <button class="btn btn-md" v-on:click="" title="Add printable materials">
          <i class="bi bi-file-earmark-pdf"></i>
          Add materials
        </button>
        <button class="btn btn-md" v-on:click="" title="Add vocabulary">
          <i class="bi bi-file-earmark-plus"></i>
          Add vocabulary
        </button>
        <button class="btn btn-md" v-on:click="" title="Add links to external resources">
          <i class="bi bi-globe"></i>
          Add resources
        </button>
      </div>
    </div>
  </div>

  <ClassVocabularies/>

</template>

<style scoped>

</style>