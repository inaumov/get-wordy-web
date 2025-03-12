<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";
import CardsTable from "@/components/cards/CardsTable.vue";

import {fetchCards} from '@/js/cards.js';

export default {
  components: {ActionButtons, CardsTable},
  props: ['dictionaryId', 'dictionaryName'],
  data() {
    return {
      cards: [],
    }
  },
  methods: {
    async getData() {
      const response = await fetchCards(this.dictionaryId);
      this.cards = await response.json();
    }
  },
  mounted() {
    this.getData()
    console.log("Selected dictionary: id = ", this.dictionaryId, ", name = ", this.dictionaryName)
  }
};

</script>

<template>
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'dictionaries'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>
  <h4 class="p-4">{{ this.dictionaryName }}</h4>
  <action-buttons/>
  <cards-table v-bind="{dictionaryId: this.dictionaryId, cards: this.cards}"/>

</template>
