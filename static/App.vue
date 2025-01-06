<script>
import {provideAuth, checkLoginStatus} from '@/js/auth-check.js';
import {onMounted} from "vue";

export default {
  components: {},
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
    <router-view/>
</template>
