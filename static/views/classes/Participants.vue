<script>
import {assignUser, revokeUser} from "@/js/participants-api.js";

export default {
  props: ['classInfo'],
  data() {
    return {
      newUserIdentity: "",
      inputError: ""
    };
  },
  methods: {
    async handleAssignUser() {
      if (!this.newUserIdentity.trim()) {
        this.inputError = "User identity cannot be empty!";
        return;
      }
      this.inputError = "";
      try {
        await assignUser(this.classInfo?.classId, this.newUserIdentity);
        this.classInfo.participants.push(this.newUserIdentity);
        this.newUserIdentity = "";
      } catch (error) {
        console.error("Error assigning user:", error);
      }
    },
    async handleRevokeUser(userIdentity) {
      try {
        await revokeUser(this.classInfo?.classId, userIdentity);
        const index = this.classInfo.participants.indexOf(userIdentity);
        if (index !== -1) {
          this.classInfo.participants.splice(index, 1);
        }
      } catch (error) {
        console.error("Error revoking user:", error);
      }
    }
  }
};
</script>

<template>
  <div class="class-access-container">
    <!-- assign new user (top & centered) -->
    <h5 class="pb-2 border-bottom">Participants</h5>
    <form class="d-flex align-items-center gap-2 pt-2 mb-3" @submit.prevent="handleAssignUser">
      <input
          v-model="newUserIdentity"
          type="text"
          class="form-control form-control-sm"
          placeholder="User identity, or email"
      />
      <button type="submit" class="btn btn-outline-success btn-sm btn-equals" style="width: 90px;">
        Assign
      </button>
    </form>
    <!-- users list (below) -->
    <h6 class="pt-3">{{ this.classInfo.participants?.length }} participant(s)</h6>
    <ul v-if="this.classInfo.participants.length > 0" class="access-list">
      <li class="py-2 d-flex align-items-center" v-for="user in this.classInfo.participants" :key="user">
        <span class="px-2 me-auto">{{ user }}</span>
        <button class="btn btn-outline-danger btn-sm btn-equals" @click="handleRevokeUser(user)">Revoke</button>
      </li>
    </ul>
    <p v-else class="text-muted text-center">No users assigned yet.</p>
  </div>
</template>

<style scoped>

.btn-equals {
  min-width: 68px
}

.access-list li {
  display: flex;
  justify-content: space-between;
}

.access-list li:hover {
  background-color: #f8f9fa;
}
</style>