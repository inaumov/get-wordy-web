<script>
import ExplanationForm from "@/components/words/ExplanationForm.vue";

import {fetchExplanation, editExplanation} from '@/js/explanations-api.js';
import {textToArray} from '@/js/utils.js';

export default {
  components: {ExplanationForm},
  props: ['vocabId', 'wordId'],
  data() {
    return {
      card: {}
    }
  },
  methods: {
    async getData() {
      const response = await fetchExplanation(this.vocabId, this.wordId);
      this.card = {...await response.json()};
    },
    handleFormEdit: function () {
      let form = document.getElementById('word-explanation-form');
      let formData = new FormData(form);

      let requestModel = {
        value: formData.get('word'),
        transcription: formData.get('transcription'),
        explanation: {
          partOfSpeech: formData.get('parts'),
          meaning: formData.get('meaning'),
          sentences: textToArray(formData.get('sentences')),
          collocations: textToArray(formData.get('collocations'))
        }
      };
      editExplanation(this.vocabId, this.wordId, requestModel);
    }
  },
  created() {
    this.getData()
  }
};

</script>

<template>
  <ExplanationForm :model="this.card" :onSubmit="handleFormEdit"/>
</template>

<style>
</style>
