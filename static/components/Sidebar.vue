<script>
import {useRouter} from 'vue-router';
import {formatTimeSlot} from "@/js/utils.js";
import {useAuth} from "@/js/auth-check.js";

export default {
  props: {
    menuItems: {
      type: Array,
      required: true,
      default: () => [],
    },
    school: {
      type: Object,
      default: () => ({
        name: null,
        logo: null,
        teacherName: null
      }),
      required: false
    },
    activeClass: {
      type: Object,
      required: false
    }
  },
  setup() {
    const {isLoggedIn, logout, school} = useAuth();

    const router = useRouter();

    const navigate = (path) => {
      router.push(path);
    };

    const isActive = (path) => {
      return router.currentRoute.value.path.startsWith(path);
    };

    return {navigate, isActive, isLoggedIn, logout, school};
  },
  methods: {
    formatTimeSlot,
    logout() {
      this.logout();
      this.$router.push("/login?logout=true");
    },
    getInitials(name) {
      return name
          .split(' ')
          .map(word => word[0]?.toUpperCase())
          .slice(0, 2)
          .join('');
    },
    stringToColor(str) {
      // Generates a consistent background color from string
      let hash = 0;
      for (let i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
      }
      let color = '#';
      for (let i = 0; i < 3; i++) {
        const value = (hash >> (i * 8)) & 0xff;
        color += ('00' + value.toString(16)).slice(-2);
      }
      return color;
    }
  },
  computed: {
    generatedLogo() {
      const initials = this.getInitials(this.school?.name || 'My School');
      const bgColor = this.stringToColor(this.school?.name || 'My School');
      return `
        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" class="logo">
          <rect width="100%" height="100%" fill="${bgColor}" />
          <text x="50%" y="50%" font-size="28" dy=".35em"
                text-anchor="middle" fill="white" font-family="Arial, sans-serif">
            ${initials}
          </text>
        </svg>
      `;
    }
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
      <hr v-if="school" class="sidebar-divider"/>
      <div v-if="school">
        <div v-if="school.logo" class="d-flex justify-content-center align-items-center mb-2">
          <img :src="school.logo" alt="School logo" class="logo img-fluid"/>
        </div>
        <div v-else v-html="generatedLogo" class="d-flex justify-content-center align-items-center mb-2"></div>
        <p class="school-name mb-1">{{ school.name }}</p>
        <p v-if="school.teacherName" class="teacher-name text-muted">{{ school.teacherName }}</p>
        <button v-if="school.name" class="btn p-1" title="Logout" @click="logout">
          <i class="bi bi-box-arrow-right"></i>
        </button>
      </div>
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
