<script>
import PlayGame from "@/components/game/PlayGame.vue";
import PreviewCards from "@/components/game/PreviewCards.vue";
import MatchExercise from "@/components/game/MatchExercise.vue";
import UnscrambleExercise from "@/components/game/UnscrambleExercise.vue";
import SpellingExercise from "@/components/game/SpellingExercise.vue";

export default {
  components: {PlayGame, PreviewCards, MatchExercise, UnscrambleExercise, SpellingExercise},
  props: ['vocabId', 'vocabName'],
  data() {
    return {
      currentComponent: 'PreviewCards',
      cards: []
    }
  },
  methods: {
    nextStep(component, cards) {
      this.currentComponent = component
      console.log(`Next step ${this.currentComponent} selected`);
      this.cards = cards
    },
  },
  mounted() {
    console.log(`${this.currentComponent} mounted. vocabId: ${this.vocabId}, vocabName: ${this.vocabName}`);
  }
}

</script>

<template>
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'user-vocabularies'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>
  <h4 class="p-4">{{ this.vocabName }}</h4>
  <component :is="currentComponent" @nextStep="nextStep"
             v-bind="{vocabId: this.vocabId, cards: this.cards}">
  </component>

</template>
