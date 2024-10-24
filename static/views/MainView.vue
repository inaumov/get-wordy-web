<script>
import DictionariesView from '@/views/DictionariesView.vue';
import ClassListView from '@/views/ClassListView.vue';
import {useAuth} from '@/js/auth-check.js';

export default {
  components: {ClassListView, DictionariesView},
  setup: function () {
    const {permissions} = useAuth();
    return {permissions: permissions};
  }
}

</script>

<template>
  <div>
    <DictionariesView v-if="this.permissions.includes('P_MANAGE_OWN_DICTIONARIES')"/>
    <ClassListView v-if="this.permissions.includes('P_MANAGE_CLASSES')"/>
  </div>
</template>
