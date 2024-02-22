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
    }
  },
  mounted() {
    this.nextStep('PlayGame')
    console.log('PlayGame parent view mounted. dictionaryId = ', this.dictionaryId);
  }
}

</script>

<template>

  <action-buttons/>
  <component :is="currentComponent" @nextStep="nextStep" v-bind="{dictionaryId: this.dictionaryId}"></component>

</template>
