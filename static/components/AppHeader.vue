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
      <div class="row">
        <div class="col" id="settings">
          <div class="button-group d-flex flex-column align-items-end">
            <router-link v-if="isLoggedIn" to="/account" class="btn btn-md">
              <i class="bi bi-person-gear"></i>
            </router-link>
            <button v-if="isLoggedIn" class="btn btn-link" @click="logout">Logout</button>
            <button v-else class="btn btn-link" @click="redirectToLogin">Login</button>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<style>
header {
  background-color: rgba(225, 115, 96, 0.10);
}
</style>
