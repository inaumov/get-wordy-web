<script>

export default {
  props: ['classId'],
}
</script>

<template>

  <div class="">
      <div class="d-flex flex-column align-items-end">
        <button class="btn btn-md" v-on:click="" title="Select template">
          <i class="bi bi-file-earmark-plus"></i>
          Use template
        </button>
        <button class="btn btn-md" v-on:click="" title="Upload pdf documents">
          <i class="bi bi-file-earmark-pdf"></i>
          Upload pdf documents
        </button>
        <button class="btn btn-md" v-on:click="" title="Add vocabulary">
          <i class="bi bi-file-earmark-plus"></i>
          Add vocabulary
        </button>
        <button class="btn btn-md" v-on:click="" title="Link to youtube video">
          <i class="bi bi-youtube"></i>
          Youtube video
        </button>
        <button class="btn btn-md" v-on:click="" title="Add links to external resources">
          <i class="bi bi-globe"></i>
          External resources
        </button>
      </div>
  </div>

</template>

<style scoped>

</style>