<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";

import {createCard} from '@/js/cards.js';

export default {
  components: {ActionButtons},
  props: ['dictionaryId'],
  data() {
    return {
      parts: ["noun", "pronoun", "verb", "adjective", "adverb", "phrasal verb"],
      formData: {
        word: '',
        partOfSpeech: '',
        transcription: '',
        meaning: '',
        contexts: [],
        collocations: []
      },
    }
  },
  methods: {
    create_card: createCard
  },
};

</script>

<template>

  <action-buttons/>

  <div class="container" id="add-card-panel">
    <div class="row justify-content-center pb-5">
      <div class="col-8 border p-5 rounded">
        <form action="" id="add-card-form" v-on:submit.prevent="create_card(this.dictionaryId, this.formData)">
          <div class="row mb-3">
            <div class="col">
              <label for="word" class="form-label">A word *</label>
              <input type="text" class="form-control" name="word" id="word" v-model.lazy="formData.word" required/>
            </div>
            <div class="col">
              <label for="radioGrp" class="form-label">Part of speech *</label>
              <div id="radioGrp">
                <div class="form-check" v-for="part in parts">
                  <label class="form-check-label" v-bind:for="part">{{ part }}</label>
                  <input type="radio" class="form-check-input" v-model="formData.partOfSpeech" v-bind:value="part" v-bind:id="part" required/>
                </div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="transcription" class="form-label">Transcription</label>
              <input type="text" class="form-control" name="transcription" id="transcription" v-model.lazy="formData.transcription"/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="meaning" class="form-label">Meaning *</label>
              <input type="text" class="form-control" name="meaning" id="meaning" v-model.lazy="formData.meaning" required/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="contexts" class="form-label">Your example(s) line by line</label>
              <textarea class="form-control" rows="5" id="contexts" name="contexts" v-model.lazy="formData.contexts">
              </textarea>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="collocations" class="form-label">Collocation(s) line by line</label>
              <textarea class="form-control" rows="5" id="collocations" name="collocations" v-model.lazy="formData.collocations">
              </textarea>
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
