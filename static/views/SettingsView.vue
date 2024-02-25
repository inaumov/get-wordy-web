<script>
import {fetchDictionaries, updateName, updatePicture} from '@/js/dictionaries.js';

let selectedDictionaryId = 0;

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
    onSelectElement(event) {
      let val = event.target.innerText;
      const result = this.dictionaries.find(obj => {
        return obj.name === val || obj.picture === val;
      })
      selectedDictionaryId = result.dictionaryId;
      console.log('Selected: ' + selectedDictionaryId);
    },
    onNameEdit(event) {
      let currName = event.target.innerText.trim();
      const result = this.dictionaries.find(obj => {
        return obj.dictionaryId === selectedDictionaryId
      });
      let actualName = result.name;
      if (currName === actualName) {
        console.log('No changes detected in name: ' + actualName, 'for id = ', selectedDictionaryId);
      } else {
        updateName(selectedDictionaryId, currName);
        console.log('Name has been changed: ' + currName, 'for id = ', selectedDictionaryId);
      }
    },
    onPictureEdit(event) {
      let currUrl = event.target.innerText.trim();
      const result = this.dictionaries.find(obj => {
        return obj.dictionaryId === selectedDictionaryId
      });
      let actualUrl = result.picture;
      if (currUrl === actualUrl) {
        console.log('No changes detected in picture url: ' + actualUrl, 'for id = ', selectedDictionaryId);
      } else {
        console.log('Picture url has been changed: ' + currUrl, 'for id = ', selectedDictionaryId);
        updatePicture(selectedDictionaryId, currUrl);
      }
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
          <span contenteditable="true" class="p-1" v-text="dictionary.name"
                v-on:blur="onNameEdit"
                v-on:focusin="onSelectElement"
          >
          </span>
        </td>
        <td>
          <span contenteditable="true" class="p-1" v-text="dictionary.picture"
                v-on:blur="onPictureEdit"
                v-on:focusin="onSelectElement"
          >
          </span>
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
