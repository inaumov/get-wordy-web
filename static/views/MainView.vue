<script>
import Sidebar from '@/components/Sidebar.vue';
import {useMenuConfig} from "@/composables/useMenuConfig.js";
import {getUserClasses, useAuth} from "@/js/auth-check.js";

export default {
  name: 'DashboardPage',
  components: {
    Sidebar,
  },
  setup() {
    const {checkLoginStatus, permissions} = useAuth();
    return {checkLoginStatus, permissions};
  },
  props: {
  },
  data() {
    return {
      assignedClasses: []
    };
  },
  computed: {
    isLoggedIn() {
      return true;
    },
    menuItems() {
      if (this.isLoggedIn) {
        if (this.permissions.includes('P_SHARED_CLASS')) {
          return useMenuConfig.sharedAccess(this.activeClass);
        }
        if (this.permissions.includes('P_MANAGE_CLASSES')) {
          return useMenuConfig.teacherAccess();
        }
      }
      return [];
    },
    showSidebar() {
      return this.isLoggedIn && this.menuItems.length > 0;
    },
    activeClass() {
      // show the first active class, or nothing if none are active
      return this.assignedClasses.find(classInfo => classInfo.isActive) || null;
    }
  },
  mounted() {
    if (this.permissions.some(p => p === 'P_SHARED_CLASS')) {
      this.fetchAttendeeClasses();
    }
  },
  methods: {
    async fetchAttendeeClasses() {
      const response = await getUserClasses();
      this.assignedClasses = await response.json();
    },
  },
};
</script>

<template>
  <div class="app-layout">
    <!-- sidebar with menu items -->
    <sidebar class="sidebar" v-if="showSidebar" :menuItems="menuItems" :activeClass="this.activeClass"/>
    <!-- main content area -->
    <main class="main-content vh-100" style="overflow-y: auto;">
      <router-view v-if="isLoggedIn"/>
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
  padding: 0 20px;
  background: #ffffff;
}
</style>
