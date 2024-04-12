<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";

import {createCard} from '@/js/cards.js';
import {textToArray} from '@/js/utils.js';

export default {
  components: {ActionButtons},
  props: ['dictionaryId'],
  data() {
    return {
      parts: ["noun", "pronoun", "verb", "adjective", "adverb", "phrasal verb"],
    }
  },
  methods: {
    onSubmit: function () {
      let form = document.getElementById('add-card-form');
      let formData = new FormData(form);

      let newCard = {
        word: formData.get('word'),
        partOfSpeech: formData.get('parts'),
        transcription: formData.get('transcription'),
        meaning: formData.get('meaning'),
        sentences: textToArray(formData.get('sentences')),
        collocations: textToArray(formData.get('collocations'))
      };
      createCard(this.dictionaryId, newCard);
    }
  },
};

</script>

<template>

  <action-buttons/>

  <div class="container" id="add-card-panel">
    <div class="row justify-content-center pb-5">
      <div class="col-8 border p-5 rounded">
        <form action="" id="add-card-form" v-on:submit.prevent="onSubmit()">
          <div class="row mb-3">
            <div class="col">
              <label for="word" class="form-label">A word <i>*</i></label>
              <input type="text" class="form-control" name="word" id="word" autocomplete="off" required/>
            </div>
            <div class="col">
              <label for="radioGrp" class="form-label">Part of speech <i>*</i></label>
              <div id="radioGrp">
                <div class="form-check" v-for="part in parts">
                  <label class="form-check-label" v-bind:for="part">{{ part }}</label>
                  <input type="radio" class="form-check-input" v-bind:value="part" v-bind:id="part" name="parts" required/>
                </div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="transcription" class="form-label">Transcription</label>
              <input type="text" class="form-control" name="transcription" id="transcription" autocomplete="off"/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="meaning" class="form-label">Meaning <i>*</i></label>
              <input type="text" class="form-control" name="meaning" id="meaning" autocomplete="off" required/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="collocations" class="form-label">Collocations <small>(put it line by line)</small>
              </label>
              <textarea class="form-control" rows="3" id="collocations" name="collocations" autocomplete="off"/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="sentences" class="form-label">
                Examples of usage in sentences <small>(put it line by line)</small>
                <br/>
                <i>Note: It may be used to check for correct spelling in certain exercises</i>
              </label>
              <textarea class="form-control" rows="7" id="sentences" name="sentences" autocomplete="off"/>
            </div>
          </div>
          <button type="submit" class="btn btn-lg">
            <i class="bi bi-save"></i> Submit
          </button>
        </form>
      </div>
    </div>
  </div>

</template>

<style>
div#add-card-panel label i, div#add-card-panel button i {
  color: rgb(185, 87, 84)
}
</style>
