<script>
import {applyCaption} from '@/js/utils.js'
import {useAuth} from "@/js/auth-check.js";

export default {
  setup() {
    const {isLoggedIn, logout} = useAuth();
    return {isLoggedIn, logout};
  },
  methods: {
    handleClick() {
      applyCaption('Settings')
    },
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
    <div class="container p-4" id="gw_header">
      <div class="row align-items-center">
        <div class="col">
          <h4 id="caption">My dictionaries</h4>
        </div>
        <div class="col" id="settings">
          <div class="button-group d-flex flex-column align-items-end">
            <router-link v-if="isLoggedIn" to="/Settings" class="btn btn-md" @click="handleClick">
              <i class="bi bi-gear"></i>
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
