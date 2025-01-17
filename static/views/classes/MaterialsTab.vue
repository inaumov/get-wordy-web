<script>
import ClassVocabularies from "@/views/classes/ClassVocabularies.vue";

export default {
  components: {ClassVocabularies},
  props: ['classId'],
}
</script>

<template>
  <div class="">
    <div class="pt-3">
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

  <ClassVocabularies v-bind="{classId: this.classId}"/>

</template>

<style scoped>

</style>