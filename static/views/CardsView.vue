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
    async addWord(wordExplanation) {
      if (!this.vocabularyContains(wordExplanation)) {
        let response = await addToVocabulary(this.vocabId, wordExplanation.wordId);
        if (response.ok) {
          let itemAdded = await response.json();
          if (itemAdded.wordId) {
            console.log(itemAdded)
            this.cards.push(itemAdded);
          }
        } else {
          alert('Error');
        }
      } else {
        alert('This word is already added.');
      }
    },
    vocabularyContains(wordExplanation) {
      return this.cards.some((item) => {
        return item.lemma === wordExplanation.lemma
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
    },
    ids() {
      return this.cards.map(word => word.wordId);
    }
  }
};

</script>

<template>
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'user-vocabularies'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>
  <div class="d-flex justify-content-between align-items-center px-4 py-3">
    <h5 class="mb-0">{{ vocabulary?.name }}</h5>
    <p class="mb-0 fw-light">Words total: {{ vocabulary.wordsTotal }}</p>
  </div>

  <search class="p-4" v-if="!viewOnly" @add-to-vocabulary="addWord" :vocab-word-ids="this.ids"/>
  <cards-table v-bind="{vocabId: this.vocabId, cards: this.cards}"/>

</template>
