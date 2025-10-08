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
    const backendBase = import.meta.env.VITE_LOGOS_API; // from .env
    const {isLoggedIn, loggedInUser, permissions, logout, school} = useAuth();

    const router = useRouter();

    const navigate = (path) => {
      router.push(path);
    };

    const isActive = (path) => {
      return router.currentRoute.value.path.startsWith(path);
    };

    return {navigate, isActive, logout, isLoggedIn, loggedInUser, permissions, school, backendBase};
  },
  methods: {
    formatTimeSlot,
    doLogout() {
      this.logout();
      window.location.href = "/login?logout=true"; // server handled page
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
      const initials = this.getInitials(this.school?.name || this.school?.teacherName);
      const bgColor = this.stringToColor(this.school?.name || this.school?.teacherName);
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

      <!-- school and class info (if student) -->
      <div v-if="isLoggedIn && permissions.includes('P_SHARED_CLASS') && activeClass" class="school-info">
        <!-- bell icon centered with class name -->
        <div class="active-class-header">
          <h5 class="active-class-name">{{ activeClass.name }}</h5>
          <span class="status-badge">
            <i class="bi bi-bell animated-bell"></i>
          </span>
        </div>
        <!-- time slots section -->
        <div v-if="activeClass?.timeSlots">
          <span v-for="timeSlot in activeClass.timeSlots" :key="timeSlot.dayOfWeek">
            <strong>{{ timeSlot.dayOfWeek }}</strong>: {{ formatTimeSlot(timeSlot) }}<br/>
          </span>
        </div>

        <hr class="sidebar-divider"/>
        <!-- school info (optional) -->
        <div v-if="school">
          <div v-if="school.logo" class="d-flex justify-content-center align-items-center mb-2">
            <img class="logo img-fluid" :src="`${backendBase}/${school.logo}`" alt="School logo"/>
          </div>
          <div v-else v-html="generatedLogo" class="d-flex justify-content-center align-items-center mb-2"></div>
          <p v-if="school.name" class="fw-bold mb-1">{{ school.name }}</p>
          <p v-if="school.teacherName" class="small text-muted">Teacher: {{ school.teacherName }}</p>
        </div>
      </div>

      <!-- school info (if teacher) -->
      <div v-if="isLoggedIn && permissions.includes('P_MANAGE_CLASSES') && school?.name" class="school-info">
        <div v-if="school.logo" class="d-flex justify-content-center align-items-center mt-3">
          <img class="logo img-fluid" :src="`${backendBase}/${school.logo}`" alt="School logo"/>
        </div>
        <div v-else v-html="generatedLogo" class="d-flex justify-content-center align-items-center mt-3"></div>
        <p v-if="school.name" class="fw-bold">{{ school.name }}</p>
      </div>

      <hr class="sidebar-divider"/>
      <!-- user info + sign out -->
      <div class="d-flex flex-column align-items-center text-center mb-3">
        <p class="fw-bold mb-1">{{ loggedInUser.displayName }}</p>
        <button class="btn btn-link p-1 d-flex align-items-center" title="Sign Out" @click="doLogout">
          <i class="bi bi-box-arrow-right me-2"></i><small>Sign Out</small>
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

.school-info {
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
