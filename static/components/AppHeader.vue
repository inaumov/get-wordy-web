<script>
import {useAuth} from "@/js/auth-check.js";

export default {
  setup() {
    const {isLoggedIn, logout} = useAuth();
    return {isLoggedIn, logout};
  },
  methods: {
    redirectToLogin() {
      window.location.href = '/login';
    },
    logout() {
      this.logout();
    }
  }
}
</script>

<template>
  <header class="text-muted">
    <div class="container py-4">
      <div class="d-flex justify-content-end">
        <div v-if="isLoggedIn">
          <router-link to="/account" class="btn btn-md">
            <i class="bi bi-person-gear"></i>
          </router-link>
          <button v-if="isLoggedIn" class="btn p-1" title="Logout" @click="logout">
            <i class="bi bi-box-arrow-right"></i>
          </button>
        </div>
        <button v-else class="btn btn-link" title="Account" @click="redirectToLogin">Login</button>
      </div>
    </div>
  </header>
</template>

<style>
header {
  background-color: rgba(225, 115, 96, 0.10);
}
</style>
