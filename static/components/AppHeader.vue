<script>
import {applyCaption} from '@/js/utils.js'

export default {
  methods: {
    handleClick() {
      applyCaption('Settings')
    }
  }
}
</script>

<template>
  <header class="text-muted">
    <div class="container p-4" id="gw_header">
      <div class="row align-items-center">
        <div class="col">
          <h4 id="caption">My dictionaries</h4>
        </div>
        <div class="col" id="settings">
          <router-link to="/Settings" class="btn btn-lg float-end" @click="handleClick">
            <i class="bi bi-gear"></i>
          </router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<style>
header {
  background-color: rgba(225, 115, 96, 0.10);
}
</style>
