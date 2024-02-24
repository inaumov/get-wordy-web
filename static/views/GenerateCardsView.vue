<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";

import {generateCards} from '@/js/cards.js';

export default {
  components: {ActionButtons},
  props: ['dictionaryId'],
  data() {
    return {
      formData: {
        words: [],
      },
    }
  },
  methods: {
    generate_cards: generateCards
  },
};

</script>

<template>

  <action-buttons/>

  <div class="container" id="generate-cards-panel">
    <div class="row justify-content-center pb-5">
      <div class="col-8 border p-5">
        <form action="" id="generate-cards-form" v-on:submit.prevent="generate_cards(this.dictionaryId, this.formData)">
          <div class="mb-3">
            <label for="text" class="form-label">Input new words to generate cards</label>
            <textarea class="form-control" rows="5" id="text" name="words" v-model.lazy="formData.words" required>
            </textarea>
          </div>
          <button type="submit" class="btn btn-primary">Submit</button>
        </form>
      </div>
    </div>
  </div>

</template>
