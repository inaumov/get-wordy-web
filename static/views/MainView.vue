<script>
import Sidebar from '@/components/Sidebar.vue';
import {useMenuConfig} from "@/composables/useMenuConfig.js";

export default {
  name: 'DashboardPage',
  components: {
    Sidebar,
  },
  props: {
    // menuItems: {
    //   type: Array,
    //   default: () => [],
    // },
    // showSidebar: {
    //   type: Boolean,
    //   default: true,
    // },
  },
  data() {
    return {
      permissions: [null],
    };
  },
  computed: {
    isLoggedIn() {
      return true;
    },
    menuItems() {
      if (this.isLoggedIn) {
        if (this.permissions.includes('P_SHARED_CLASS')) {
          return useMenuConfig('sharedAccess');
        }
        if (this.permissions.includes('P_MANAGE_CLASSES')) {
          return useMenuConfig('teacherAccess');
        }
      }
      return [];
    },
    showSidebar() {
      return this.isLoggedIn && (this.permissions.includes('P_MANAGE_CLASSES') || this.permissions.includes('P_SHARED_CLASS'));
    },
  },
  mounted() {
    // Example: Simulate fetching user data (replace with real auth logic)
    this.fetchUserData();
  },
  methods: {
    fetchUserData() {
      this.permissions = ['P_MANAGE_OWN_VOCAB'];
      this.permissions = ['P_SHARED_CLASS'];
      this.permissions = ['P_MANAGE_CLASSES'];
    },
  },
};
</script>

<template>
  <div class="app-layout">
    <!-- sidebar with menu items -->
    <sidebar class="sidebar" v-if="showSidebar" :menuItems="menuItems"/>
    <!-- main content area -->
    <main class="main-content">
      <router-view/>
    </main>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 250px;
  background: #f8f9fa; /* Light Bootstrap background color */
  border-right: 1px solid #dee2e6;
}

.main-content {
  flex-grow: 1;
  padding: 20px;
  background: #ffffff;
}
</style>
