<script>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import {provideAuth, checkLoginStatus} from '@/js/auth-check.js';
import {onMounted} from "vue";

export default {
  components: {AppHeader, AppFooter},
  setup() {
    // provide globally to all child components
    provideAuth();
    onMounted(() => {
      checkLoginStatus();
    });
    return {};
  }
};

</script>

<template>

  <app-header/>
  <main>
    <router-view/>
  </main>
  <app-footer/>

</template>
