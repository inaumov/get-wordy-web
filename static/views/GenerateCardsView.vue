<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";

import {generateCards} from '@/js/cards.js';
import {textToArray} from '@/js/utils.js';

export default {
  components: {ActionButtons},
  props: ['dictionaryId'],
  data() {
    return {
    }
  },
  methods: {
    onSubmit: function () {
      let form = document.getElementById('generate-cards-form');
      let formData = new FormData(form);
      const wordsArr = textToArray(formData.get('words'));
      generateCards(this.dictionaryId, wordsArr);
    }
  },
};

</script>

<template>

  <action-buttons/>

  <div class="container" id="generate-cards-panel">
    <div class="row justify-content-center pb-5">
      <div class="col-8 border p-5 rounded">
        <form action="" id="generate-cards-form" v-on:submit.prevent="onSubmit()">
          <div class="mb-3">
            <label for="text" class="form-label">Input new words to generate cards</label>
            <textarea class="form-control" rows="5" id="text" name="words" autocomplete="off" required/>
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
div#generate-cards-panel i {
  color: rgb(185, 87, 84)
}
</style>
