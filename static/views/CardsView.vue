<script>
import CardsTable from "@/components/cards/CardsTable.vue";

import {fetchCards} from '@/js/cards.js';
import GenerateCardsView from "@/views/GenerateCardsView.vue";

export default {
  components: {GenerateCardsView, CardsTable},
  props: ['vocabId', 'vocabName'],
  data() {
    return {
      cards: [],
    }
  },
  methods: {
    async getData() {
      const response = await fetchCards(this.vocabId);
      this.cards = await response.json();
    }
  },
  mounted() {
    this.getData()
  }
};

</script>

<template>
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'user-vocabularies'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>
  <h4 class="p-4">{{ this.vocabName }}</h4>
  <GenerateCardsView :vocab-id="this.vocabId"></GenerateCardsView>
  <cards-table v-bind="{vocabId: this.vocabId, cards: this.cards}"/>

</template>
