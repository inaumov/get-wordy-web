<script>
import CardsTable from "@/components/cards/CardsTable.vue";
import Search from "@/components/Search.vue";
import {fetchUserVocabulary} from "@/js/dictionaries.js";
import {fetchCards} from '@/js/cards.js';
import {addToVocabulary} from "@/js/cards.js";

export default {
  components: {
    Search,
    CardsTable
  },
  props: ['vocabId'],
  data() {
    return {
      vocabulary: {},
      cards: [],
    }
  },
  methods: {
    async getVocabulary() {
      const response = await fetchUserVocabulary(this.vocabId);
      this.vocabulary = await response.json();
    },
    async getData() {
      const response = await fetchCards(this.vocabId);
      this.cards = await response.json();
    },
    addWord(wordExplanation) {
      if (!this.vocabularyContains(wordExplanation)) {
        addToVocabulary(this.vocabId, wordExplanation.wordId)
            .then(response => {
              if (response.ok) {
                let itemAdded = response.json();
                this.cards.push(itemAdded);
              } else {
                alert('Error');
              }
            })
      } else {
        alert('This word is already added.');
      }
    },
    vocabularyContains(wordExplanation) {
      return this.cards.some((item) => {
        return item.value === wordExplanation.value
            && item.explanation.partOfSpeech === wordExplanation.explanation.partOfSpeech
      });
    },
  },
  mounted() {
    this.getVocabulary();
    this.getData();
  },
  computed: {
    viewOnly() {
      return this.vocabulary?.accessType !== 'OWN'; // has no type
    }
  }
};

</script>

<template>
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'user-vocabularies'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>
  <h4 class="p-4">{{ this.vocabulary?.name }}</h4>

  <search v-if="!viewOnly" @add-to-vocabulary="addWord" :vocab-id="this.vocabId"/>

  <p class="text-end fw-light px-4">Words total: {{vocabulary.wordsTotal}}</p>
  <cards-table v-bind="{vocabId: this.vocabId, cards: this.cards}"/>

</template>
