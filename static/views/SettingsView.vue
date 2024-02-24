<script>
import {fetchDictionaries} from '@/js/dictionaries.js';

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
  <div v-if="dictionaries[0].dictionaryId" class="container p-4" id="content">
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
    <h6 class="pt-3">Select cards limit for exercise</h6>
    <div class="border bg-light p-2">
      <div class="form-check pt-1 pb-1">
        <input class="form-check-input" type="radio" name="exerciseLimit" id="rollDice" checked>
        <label class="form-check-label" for="rollDice">
          Roll dice before each game, from 2 to 12
        </label>
      </div>
      <div class="form-check pt-1 pb-1">
        <input class="form-check-input" type="radio" name="exerciseLimit" id="defaultLimit">
        <label class="form-check-label" for="defaultLimit">
          Predefined number, where 5 is the default
        </label>
      </div>
    </div>
  </div>
  <div v-else class="d-flex justify-content-center p-4">
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
