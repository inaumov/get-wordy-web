<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";

import {createCard} from '@/js/cards.js';
import {toSentenceArray} from '@/js/utils.js';

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
        examples: toSentenceArray(formData.get('examples')),
        collocations: toSentenceArray(formData.get('collocations'))
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
              <label for="word" class="form-label">A word *</label>
              <input type="text" class="form-control" name="word" id="word" required/>
            </div>
            <div class="col">
              <label for="radioGrp" class="form-label">Part of speech *</label>
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
              <input type="text" class="form-control" name="transcription" id="transcription"/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="meaning" class="form-label">Meaning *</label>
              <input type="text" class="form-control" name="meaning" id="meaning" required/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="examples" class="form-label">Your example(s) line by line</label>
              <textarea class="form-control" rows="5" id="examples" name="examples"/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="collocations" class="form-label">Collocation(s) line by line</label>
              <textarea class="form-control" rows="5" id="collocations" name="collocations"/>
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
div#add-card-panel i {
  color: rgb(185, 87, 84)
}
</style>
