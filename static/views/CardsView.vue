<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";
import CardsTable from "@/components/cards/CardsTable.vue";

import {applyCaption} from '@/js/utils.js'
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
    applyCaption(this.dictionaryName)
    console.log("Selected dictionary: id = ", this.dictionaryId, ", name = ", this.dictionaryName)
  }
};

</script>

<template>

  <action-buttons/>
  <cards-table v-bind="{dictionaryId: this.dictionaryId, cards: this.cards}"/>

</template>
