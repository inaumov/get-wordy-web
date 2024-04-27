<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";

import {fetchCard, updateCard} from '@/js/cards.js';
import {textToArray, arrayToText} from '@/js/utils.js';

export default {
  components: {ActionButtons},
  props: ['dictionaryId', 'cardId'],
  data() {
    return {
      card: {
        word: {}
      },
      parts: ["noun", "pronoun", "verb", "adjective", "adverb", "phrasal verb", "phrase"],
    }
  },
  methods: {
    arrayToText: arrayToText,
    async getData() {
      const response = await fetchCard(this.dictionaryId, this.cardId);
      this.card = await response.json();
    },
    getStrSentences: function () {
      let objects = this.card['sentences'];
      if (objects && Array.isArray(objects)) {
        return objects.map(el => el['originalSentence'])
      }
      return [];
    },
    onSubmit: function () {
      let form = document.getElementById('add-card-form');
      let formData = new FormData(form);

      let cardData = {
        word: formData.get('word'),
        partOfSpeech: formData.get('parts'),
        transcription: formData.get('transcription'),
        meaning: formData.get('meaning'),
        sentences: textToArray(formData.get('sentences')),
        collocations: textToArray(formData.get('collocations'))
      };
      updateCard(this.dictionaryId, this.cardId, this.card['wordId'], cardData);
    }
  },
  created() {
    this.getData()
    console.log("Selected card: id = ", this.cardId, ", dictionaryId = ", this.dictionaryId)
  }
};

</script>

<template>

  <action-buttons/>

  <div class="container" id="edit-card-panel">
    <div class="row justify-content-center pb-5">
      <div class="col-8 border p-5 rounded">
        <form action="" id="add-card-form" v-on:submit.prevent="onSubmit()">
          <div class="row mb-3">
            <div class="col">
              <label for="word" class="form-label">A word <i>*</i></label>
              <input type="text" class="form-control" name="word" id="word"
                     v-model="card.word['value']" autocomplete="off" required/>
            </div>
            <div class="col">
              <label for="radioGrp" class="form-label">Part of speech <i>*</i></label>
              <div id="radioGrp">
                <div class="form-check" v-for="part in parts">
                  <label class="form-check-label" v-bind:for="part">{{ part }}</label>
                  <input type="radio" class="form-check-input" v-bind:value="part" v-bind:id="part" name="parts"
                         v-model="card.word['partOfSpeech']" required/>
                </div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="transcription" class="form-label">Transcription</label>
              <input type="text" class="form-control" name="transcription" id="transcription"
                     v-model="card.word['transcription']" autocomplete="off"/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="meaning" class="form-label">Meaning <i>*</i></label>
              <input type="text" class="form-control" name="meaning" id="meaning" autocomplete="off"
                     v-model="card.word['meaning']" required/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="collocations" class="form-label">Collocations <small>(put it line by line)</small>
              </label>
              <textarea class="form-control" rows="3" id="collocations" name="collocations"
                        v-text="arrayToText(card['collocations'])" autocomplete="off"/>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col">
              <label for="sentences" class="form-label">
                Examples of usage in sentences <small>(put it line by line)</small>
                <br/>
                <i>Note: It may be used to check for correct spelling in certain exercises</i>
              </label>
              <textarea class="form-control" rows="7" id="sentences" name="sentences"
                        v-text="arrayToText(getStrSentences())" autocomplete="off"/>
            </div>
          </div>
          <button type="submit" class="btn btn-lg">
            <i class="bi bi-save"></i> Update
          </button>
        </form>
      </div>
    </div>
  </div>

</template>

<style>
div#edit-card-panel label i, div#edit-card-panel button i {
  color: rgb(185, 87, 84)
}
</style>
