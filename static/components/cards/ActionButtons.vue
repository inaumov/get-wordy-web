<script>
export default {
  props: ['forceRerender']
}
</script>

<template>
  <div class="container" id="action-buttons">
    <div class="row p-5 text-center">
      <div class="col-12">
        <router-link :to="{ name : 'all-cards'}" class="btn btn-lg">
          <i class="bi bi-card-list"></i> All cards
        </router-link>
        <router-link :to="{ name : 'play-game'}" class="btn btn-lg" @click="forceRerender">
          <i class="bi bi-emoji-smile-upside-down"></i> Try game
        </router-link>
        <router-link :to="{ name : 'generate'}" class="btn btn-lg">
          <i class="bi bi-bricks"></i> Generate
        </router-link>
        <router-link :to="{ name : 'add-card'}" class="btn btn-lg">
          <i class="bi bi-plus-circle"></i> Add manually
        </router-link>
      </div>
    </div>
  </div>
</template>

<style>
div#action-buttons i {
  color: rgb(185, 87, 84)
}
</style>
