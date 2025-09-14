<script>
import {useAuth} from "@/js/auth-check.js";

export default {
  setup() {
    const {isLoggedIn, logout} = useAuth();
    return {isLoggedIn, logout};
  },
  methods: {
    redirectToLogin() {
      window.location.href = '/login'; // server handled page
    },
    logout() {
      this.logout();
      window.location.href = "/login?logout=true"; // server handled page
    },
    goToAccount() {
      window.location.href = "/account";
    }
  }
}
</script>

<template>
  <header class="text-muted">
    <div class="container py-4">
      <div class="d-flex justify-content-end">
        <div v-if="isLoggedIn">
          <button class="btn btn-md" title="Account" @click="goToAccount">
            <i class="bi bi-person-gear"></i>
          </button>
          <button v-if="isLoggedIn" class="btn p-1" title="Logout" @click="logout">
            <i class="bi bi-box-arrow-right"></i>
          </button>
        </div>
        <button v-else class="btn btn-link" title="Login" @click="redirectToLogin">Login</button>
      </div>
    </div>
  </header>
</template>

<style>
header {
  background-color: rgba(225, 115, 96, 0.10);
}
</style>
