<script>
import PlayGame from "@/components/game/PlayGame.vue";
import PreviewCards from "@/components/game/PreviewCards.vue";
import MatchExercise from "@/components/game/MatchExercise.vue";
import UnscrambleExercise from "@/components/game/UnscrambleExercise.vue";
import SpellingExercise from "@/components/game/SpellingExercise.vue";
import {fetchUserVocabulary} from "@/js/dictionaries.js";

export default {
  components: {PlayGame, PreviewCards, MatchExercise, UnscrambleExercise, SpellingExercise},
  props: ['vocabId'],
  data() {
    return {
      currentComponent: 'PreviewCards',
      vocabulary: {},
      cards: []
    }
  },
  methods: {
    nextStep(component, cards) {
      this.currentComponent = component
      console.log(`Next step ${this.currentComponent} selected`);
      this.cards = cards
    },
    async getVocabulary() {
      const response = await fetchUserVocabulary(this.vocabId);
      this.vocabulary = await response.json();
    }
  },
  mounted() {
    this.getVocabulary();
    console.log(`${this.currentComponent} mounted. vocabId: ${this.vocabId}, vocabName: ${this.vocabulary.name}`);
  }
}

</script>

<template>
  <div class="p-4 d-flex justify-content-start">
    <router-link :to="{name: 'user-vocabularies'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>
  <h4 class="p-4">{{ this.vocabulary.name }}</h4>
  <component :is="currentComponent" @nextStep="nextStep"
             v-bind="{vocabId: this.vocabId, cards: this.cards}">
  </component>

</template>
