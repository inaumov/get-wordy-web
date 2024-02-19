<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";
import CardsTable from "@/components/cards/CardsTable.vue";

import {fetchCards} from '@/assets/cards.js';

export default {
  components: {ActionButtons, CardsTable},
  data() {
    return {
      dictionaryId: this.$route.params.dictionaryId,
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
  }
};

</script>

<template>

  <ActionButtons/>
  <CardsTable cards="cards"/>

</template>
