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
  <header class="text-muted bg-light">
    <div class="container p-4" id="app">
      <div class="row align-items-center">
        <div class="col">
          <h4 id="caption">My dictionaries</h4>
        </div>
        <div class="col">
          <router-link to="/Settings" class="btn btn-default btn-lg float-end" @click="handleClick">
            <i class="bi bi-gear"></i>
          </router-link>
        </div>
      </div>
    </div>
  </header>
</template>
