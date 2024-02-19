<script setup>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
</script>

<template>

  <app-header/>
  <main>
    <router-view/>
  </main>
  <app-footer/>

</template>
