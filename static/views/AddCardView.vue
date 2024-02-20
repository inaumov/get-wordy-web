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

  <ActionButtons/>

  <div class="container" id="add-card-panel">
    <div class="row justify-content-center pb-5">
      <div class="col-8 bg-light p-5 border rounded">
        <form action="" id="add-card-form" v-on:submit.prevent="create_card(this.dictionaryId, this.formData)">
          <div class="row mb-3">
            <div class="col">
              <label for="word" class="form-label">New word:</label>
              <input type="text" class="form-control" placeholder="Enter word" name="word" id="word"
                     v-model.lazy="formData.word"/>
            </div>
            <div class="col">
              <label for="radioGrp" class="form-label">Part of speech:</label>
              <div id="radioGrp">
                <div class="form-check" v-for="part in parts">
                  <label class="form-check-label" v-bind:for="part">{{ part }}</label>
                  <input type="radio" class="form-check-input" v-model="formData.partOfSpeech" v-bind:value="part"
                         v-bind:id="part"/>
                </div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="transcription" class="form-label">Transcription:</label>
              <input type="text" class="form-control" placeholder="Enter transcription" name="transcription"
                     id="transcription" v-model.lazy="formData.transcription"/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="meaning" class="form-label">Meaning:</label>
              <input type="text" class="form-control" placeholder="Enter meaning" name="meaning" id="meaning"
                     v-model.lazy="formData.meaning"/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="contexts" class="form-label">Input your example(s) line by line:</label>
              <textarea class="form-control" rows="5" id="contexts" name="contexts" placeholder="Enter examples"
                        v-model.lazy="formData.contexts">
              </textarea>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="collocations" class="form-label">Input your collocation(s) line by line:</label>
              <textarea class="form-control" rows="5" id="collocations" name="collocations"
                        placeholder="Enter collocations" v-model.lazy="formData.collocations">
              </textarea>
            </div>
          </div>
          <button type="submit" class="btn btn-primary">Submit</button>
        </form>
      </div>
    </div>
  </div>

</template>
