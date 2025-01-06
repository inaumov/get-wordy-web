<script>
import {useRouter} from 'vue-router';

export default {
  props: {
    menuItems: {
      type: Array,
      required: true,
      default: () => [],
    },
    schoolName: {
      type: String,
      default: 'Top Gear',
    },
    schoolLogo: {
      type: String,
      default: 'public/default_img_2.png',
    },
    teacherName: {
      type: String,
      default: 'Jaremy Clarkson',
    },
  },
  setup() {
    const router = useRouter();

    const navigate = (path) => {
      router.push(path);
    };

    const isActive = (path) => {
      return router.currentRoute.value.path.startsWith(path);
    };

    return {navigate, isActive};
  },
};
</script>

<template>

  <aside class="sidebar d-flex flex-column justify-content-between">

    <!-- Navigation Menu -->
    <nav class="nav flex-column nav-pills">
      <a
          v-for="item in menuItems"
          :key="item.title"
          class="nav-link d-flex align-items-center"
          :class="{ active: isActive(item.uri) }"
          href="#"
          @click.prevent="navigate(item.uri)"
          :title="item.title"
      >
        <i :class="item.icon + ' me-2'"></i>
        {{ item.label }}
      </a>
    </nav>

    <!-- Bottom Section -->
    <div class="footer text-center py-3">
      <hr class="sidebar-divider"/>
      <img :src="schoolLogo" alt="School Logo" class="logo img-fluid mb-2"/>
      <p class="school-name mb-1">{{ schoolName }}</p>
      <p v-if="teacherName" class="teacher-name text-muted">{{ teacherName }}</p>
    </div>
  </aside>
</template>

<style>
.sidebar {
  width: 250px;
  height: 100vh;
  padding: 1rem;
  border-right: 1px solid #ddd;
}

.sidebar-divider {
  border-top: 1px solid steelblue;
  margin: 1rem 0;
}

.nav-link {
  color: #495057;
  font-size: 1rem;
  font-weight: 500;
  padding: 0.75rem 1rem;
}

.nav-link.active {
  background: #007bff;
  color: #ffffff;
  border-radius: 4px;
}

.logo {
  max-width: 80px;
}

.school-name {
  font-weight: bold;
}

.teacher-name {
  font-size: 0.9rem;
}
</style>
