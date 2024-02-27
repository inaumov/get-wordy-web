<script>
import ActionButtons from "@/components/cards/ActionButtons.vue";
import PlayGame from "@/components/game/PlayGame.vue";
import RollDice from "@/components/game/RollDice.vue";
import CardsList from "@/components/game/CardsList.vue";

export default {
  components: {ActionButtons, PlayGame, RollDice, CardsList},
  props: ['dictionaryId'],
  data() {
    return {
      currentComponent: ''
    }
  },
  methods: {
    nextStep(component) {
      this.currentComponent = component
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
  <component :is="currentComponent" @nextStep="nextStep" v-bind="{dictionaryId: this.dictionaryId}"></component>

</template>
