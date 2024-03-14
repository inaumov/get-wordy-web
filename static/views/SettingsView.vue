<script>
import {fetchDictionaries, updateName, updatePicture, deleteDictionary} from '@/js/dictionaries.js';
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
    onSelectElement(dictionary) {
      selectedDictionaryId = dictionary['dictionaryId'];
    },
    getSelectedDictionary() {
      return this.dictionaries.find(obj => {
        return obj['dictionaryId'] === selectedDictionaryId
      });
    },
    onNameEdit(event) {
      let currVal = event.target.innerText.trim();
      const dictionary = this.getSelectedDictionary();
      let actualVal = dictionary['name'];
      if (currVal !== actualVal) {
        updateName(selectedDictionaryId, currVal)
            .then(response => {
              if (response.ok) {
                dictionary['name'] = currVal; // update model
                console.log('Property [name] has been changed to:', currVal, ', for dictionary id =', selectedDictionaryId);
              }
              console.log("PATCH dictionary has been requested. Response.status =", response.status);
            });
        return;
      }
      console.log('No changes detected in property [name] for dictionary id =', selectedDictionaryId);
    },
    onPictureEdit(event) {
      let currVal = event.target.innerText.trim();
      const dictionary = this.getSelectedDictionary();
      let actualVal = dictionary['picture'] || "";
      if (currVal !== actualVal) {
        if (currVal === "") {
          updatePicture(selectedDictionaryId, currVal, true)
              .then(response => {
                if (response.ok) {
                  dictionary['picture'] = ""; // update model
                  console.log('Property [picture] has been changed to empty, for dictionary id =', selectedDictionaryId);
                }
                console.log("PATCH dictionary has been requested. Response.status =", response.status);
              });
          return;
        }
        updatePicture(selectedDictionaryId, currVal)
            .then(response => {
              if (response.ok) {
                dictionary['picture'] = currVal; // update model
                console.log('Property [picture] has been changed to:', currVal, ', for dictionary id =', selectedDictionaryId);
              }
              console.log("PATCH dictionary has been requested. Response.status =", response.status);
            });
        return;
      }
      console.log('No changes detected in property [picture] for dictionary id =', selectedDictionaryId);
    },
    deleteDictionary(id) {
      deleteDictionary(id)
          .then(response => {
            if (response.ok) {
              const index = this.dictionaries.findIndex(dictionary => dictionary['dictionaryId'] === id)
              this.dictionaries.splice(index, 1)
            }
            console.log("DELETE dictionary has been requested. Response.status =", response.status);
          })
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
  <div v-if="dictionaries.length > 0" class="container p-4" id="all-settings">
    <h6>Edit dictionaries</h6>
    <table class="table table-hover table-bordered table-light">
      <tbody>
      <tr v-for="dictionary in dictionaries">
        <td>
          <span contenteditable="true" class="p-1" v-text="dictionary.name"
                v-on:blur="onNameEdit"
                v-on:focusin="onSelectElement(dictionary)"
          >
          </span>
        </td>
        <td>
          <div style="display: flex">
            <div style="width: 95.33%">
              <span contenteditable="true" class="p-1" v-text="dictionary.picture"
                    v-on:blur="onPictureEdit"
                    v-on:focusin="onSelectElement(dictionary)"
              >
              </span>
            </div>
            <div id="actions" style="width: 4.67%">
              <button class="btn btn-lg float-end" @click="deleteDictionary(dictionary['dictionaryId'])">
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
               v-model="limitSettings['exerciseLimitSelection']"
               @change="onLimitSelectionToggle('DEFAULT_LIMIT')"
        >
        <label class="form-check-label" for="defaultLimit">
          Predefined number, where 5 is the default
        </label>
      </div>
      <div class="form-check pt-1 pb-1">
        <input class="form-check-input" type="radio" name="exerciseLimit" id="rollDice" value="ROLL_DICE"
               v-model="limitSettings['exerciseLimitSelection']"
               @change="onLimitSelectionToggle('ROLL_DICE')"
        >
        <label class="form-check-label" for="rollDice">
          Roll dice before each game, from 2 to 12
        </label>
      </div>
    </div>
  </div>
  <div v-else class="d-flex justify-content-center p-4" id="first-dictionary">
    <div class="text-center">
      <p class="fs-4">Create your first dictionary</p>
      <p class="lead">
        Dictionaries are an isolated space for grouping cards and tracking your progress
      </p>
      <button class="btn btn-lg mt-4">
        <i class="bi bi-plus-square"></i> Add dictionary
      </button>
    </div>
  </div>
</template>

<style scoped>
div#first-dictionary i {
  color: rgb(185, 87, 84)
}

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
