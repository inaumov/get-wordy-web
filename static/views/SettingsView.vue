<script>
import {fetchDictionaries} from '@/assets/dictionaries.js';

export default {
  data() {
    return {
      dictionaries: [
        {
          dictionaryId: '',
          picture: '',
          name: '',
          cardsTotal: ''
        }
      ],
    }
  },
  methods: {
    async getData() {
      const response = await fetchDictionaries();
      this.dictionaries = await response.json();
    },
  },
  mounted() {
    this.getData()
  }
};

</script>

<template>
  <div v-if="dictionaries[0].dictionaryId" class="container pt-4 pb-4" id="content">
    <h6>Edit dictionaries</h6>
    <table class="table table-hover table-bordered table-light">
      <tbody>
      <tr v-for="dictionary in dictionaries">
        <td>
          <span contenteditable="true" class="p-1">{{ dictionary.name }}</span>
        </td>
        <td>
          <span contenteditable="true" class="p-1">{{ dictionary.picture }}</span>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
  <div v-else class="d-flex justify-content-center pt-5">
    <div class="text-center">
      <p class="fs-4">Create your first dictionary</p>
      <p class="lead">
        Dictionaries are an isolated space for grouping cards and tracking your progress
      </p>
      <a href="#" class="btn btn-default btn-lg mt-4" target="_self">
        <i class="bi bi-plus-square"></i> Add dictionary
      </a>
    </div>
  </div>
</template>

<style scoped>
td span {
  width: 100%;
  display: inline-block;
}
</style>
