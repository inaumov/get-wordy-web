<script>
import {useRouter} from 'vue-router';
import {formatTimeSlot} from "@/js/utils.js";

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
    activeClass: {
      type: Object,
      required: false
    }
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
  methods: {
    formatTimeSlot,
  }
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
      <div v-if="activeClass" class="active-class-item">
        <!-- Bell Icon centered with class name -->
        <div class="active-class-header">
          <h5 class="active-class-name">{{ activeClass.name }}</h5>
          <span class="status-badge">
            <i class="bi bi-bell animated-bell"></i>
          </span>
        </div>
        <!-- time slots section -->
        <div v-if="activeClass?.timeSlots">
          <span v-for="timeSlot in activeClass.timeSlots" :key="timeSlot.dayOfWeek" class="active-class-schedule">
            <strong>{{ timeSlot.dayOfWeek }}</strong>: {{ formatTimeSlot(timeSlot) }}<br/>
          </span>
        </div>
        <span class="active-class-schedule" v-else>No time slots assigned</span>
      </div>
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

.active-class-item {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.active-class-header {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.active-class-name {
  font-size: 1.1rem;
  font-weight: bold;
  margin: 0; /* remove default margin to ensure it's centered */
}

.status-badge {
  color: #ff9800;
  font-weight: bold;
  font-size: 1.3rem;
  display: flex;
  align-items: center;
  gap: 5px;
}

.active-class-schedule {
  font-size: 0.9rem;
  color: #444;
}

/* bell icon animation */
.animated-bell {
  color: #ff9800;
  animation: ring 1.5s infinite ease-in-out;
}

@keyframes ring {
  0%, 100% { transform: rotate(0); }
  25% { transform: rotate(-10deg); }
  50% { transform: rotate(10deg); }
  75% { transform: rotate(-5deg); }
}
</style>
