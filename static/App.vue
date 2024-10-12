<script>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import {ref, provide} from 'vue';

export default {
  components: {AppHeader, AppFooter},
  setup() {
    const isLoggedIn = ref(true);

    // provide globally to all child components
    provide('isLoggedIn', isLoggedIn);
    provide('setLoginStatus', (status) => {
      isLoggedIn.value = status;
    });

    return {isLoggedIn};
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
