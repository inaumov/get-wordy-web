<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";
import PlayGame from "@/components/game/PlayGame.vue";
import RollDice from "@/components/game/RollDice.vue";
import CardsList from "@/components/game/CardsList.vue";
import MatchExercise from "@/components/game/MatchExercise.vue";

export default {
  components: {ActionButtons, PlayGame, RollDice, CardsList, MatchExercise},
  props: ['dictionaryId'],
  data() {
    return {
      currentComponent: '',
      cards: []
    }
  },
  methods: {
    nextStep(component, cards) {
      this.currentComponent = component
      this.cards = cards
    },
    forceRerender() {
      this.nextStep('PlayGame')
      console.log('Force rerender PlayGame parent view. dictionaryId = ', this.dictionaryId);
    }
  },
  mounted() {
    this.nextStep('PlayGame')
    console.log('PlayGame parent view mounted. dictionaryId = ', this.dictionaryId);
  }
}

</script>

<template>

  <action-buttons v-bind="{forceRerender: this.forceRerender}"/>
  <component :is="currentComponent" @nextStep="nextStep"
             v-bind="{dictionaryId: this.dictionaryId, cards: this.cards}">
  </component>

</template>
