<script>
import ExplanationForm from "@/components/words/ExplanationForm.vue";

import {addExplanation} from '@/js/explanations-api.js';
import {textToArray} from '@/js/utils.js';

export default {
  components: {ExplanationForm},
  props: ['vocabId'],
  data() {
    return {
      card: {}
    }
  },
  methods: {
    handleFormSubmit: function () {
      let form = document.getElementById('word-explanation-form');
      let formData = new FormData(form);

      const requestModel = {
        value: formData.get('word'),
        transcription: formData.get('transcription'),
        explanation: {
          partOfSpeech: formData.get('parts'),
          meaning: formData.get('meaning'),
          inContext: textToArray(formData.get('sentences')),
          collocations: textToArray(formData.get('collocations'))
        }
      };
      addExplanation(this.vocabId, requestModel);
    }
  },
};

</script>

<template>
  <ExplanationForm :onSubmit="handleFormSubmit" :model="{}"/>
</template>

<style>
</style>
