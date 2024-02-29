<script>
import {fetchDictionaries, updateName, updatePicture} from '@/js/dictionaries.js';
import {store} from '@/js/store.js';

let selectedDictionaryId = 0;

export default {
  setup: function () {
    return {
      limitSettings: store
    }
  },
  data() {
    return {
      dictionaries: [],
    }
  },
  methods: {
    async getData() {
      const response = await fetchDictionaries();
      this.dictionaries = await response.json();
    },
    onSelectElement(event) {
      let val = event.target.innerText;
      const dictionary = this.dictionaries.find(obj => {
        return obj.name === val || obj.picture === val;
      })
      selectedDictionaryId = dictionary['dictionaryId'];
    },
    onNameEdit(event) {
      this.handleEdit(event, 'name', updateName)
    },
    onPictureEdit(event) {
      this.handleEdit(event, 'picture', updatePicture)
    },
    handleEdit(event, property, serviceFunc) {
      let currVal = event.target.innerText.trim();
      const dictionary = this.dictionaries.find(obj => {
        return obj['dictionaryId'] === selectedDictionaryId
      });
      let actualVal = dictionary[property];
      if (currVal === actualVal) {
        console.log('No changes detected for dictionary id =', selectedDictionaryId, 'and property [' + property + ']:', actualVal);
      } else {
        console.log('Property [' + property + '] has been changed:', currVal, ', dictionary id =', selectedDictionaryId);
        serviceFunc(selectedDictionaryId, currVal);
        dictionary[property] = currVal; // update model
      }
    },
    deleteDictionary(id) {
      this.$emit('deleteDictionary', id)
    },
    onLimitSelectionToggle(selection) {
      this.limitSettings.selectLimit(selection);
      console.log('Limit Settings selected:', selection)
    }
  },
  mounted() {
    this.getData()
  }
};

</script>

<template>
  <div v-if="dictionaries.length > 0" class="container p-4" id="content">
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
          <div style="display: flex">
            <div style="width: 95.33%">
              <span contenteditable="true" class="p-1" v-text="dictionary.picture"
                    v-on:blur="onPictureEdit"
                    v-on:focusin="onSelectElement"
              >
              </span>
            </div>
            <div id="actions" style="width: 4.67%">
              <button class="btn btn-lg float-end" @click="deleteDictionary(dictionary.id)">
                <i class="bi bi-x-lg"></i>
              </button>
            </div>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
    <h6 class="pt-3">Select cards limit for exercise</h6>
    <div class="border bg-light p-2">
      <div class="form-check pt-1 pb-1">
        <input class="form-check-input" type="radio" name="exerciseLimit" id="defaultLimit" value="DEFAULT_LIMIT"
               v-model="limitSettings.exerciseLimitSelection"
               @change="onLimitSelectionToggle('DEFAULT_LIMIT')"
        >
        <label class="form-check-label" for="defaultLimit">
          Predefined number, where 5 is the default
        </label>
      </div>
      <div class="form-check pt-1 pb-1">
        <input class="form-check-input" type="radio" name="exerciseLimit" id="rollDice" value="ROLL_DICE"
               v-model="limitSettings.exerciseLimitSelection"
               @change="onLimitSelectionToggle('ROLL_DICE')"
        >
        <label class="form-check-label" for="rollDice">
          Roll dice before each game, from 2 to 12
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

tr .btn {
  opacity: 0;
}

tr:hover .btn {
  opacity: 1;
}

table #actions .btn {
  padding: 0 5px !important;
}

</style>
