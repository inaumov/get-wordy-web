<script>
import {assignUser, revokeUser} from "@/js/attendees-api";

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
        this.classInfo.attendees.push(this.newUserIdentity);
        this.newUserIdentity = "";
      } catch (error) {
        console.error("Error assigning user:", error);
      }
    },
    async handleRevokeUser(userIdentity) {
      try {
        await revokeUser(this.classInfo?.classId, userIdentity);
        const index = this.classInfo.attendees.indexOf(userIdentity);
        if (index !== -1) {
          this.classInfo.attendees.splice(index, 1);
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
    <section class="assign-section">
      <h5 class="section-title">Assign new User</h5>
      <div class="assign-wrapper">
        <input v-model="newUserIdentity" type="text" class="form-control"
               placeholder="Enter user identity (ID or email)"/>
        <button class="btn btn-success assign-btn" @click="handleAssignUser">Assign</button>
      </div>
      <p v-if="inputError" class="text-danger mt-2 text-center">{{ inputError }}</p>
    </section>
    <!-- users list (below) -->
    <section>
      <h5 class="section-title">Users with Access</h5>
      <ul v-if="this.classInfo.attendees.length > 0" class="access-list">
        <li v-for="user in this.classInfo.attendees" :key="user">
          <span>{{ user }}</span>
          <button class="btn btn-outline-danger btn-sm" @click="handleRevokeUser(user)">Revoke</button>
        </li>
      </ul>
      <p v-else class="text-muted text-center">No users assigned yet.</p>
    </section>
  </div>
</template>

<style scoped>
.class-access-container {
  padding: 20px;
  max-width: 500px;
  margin: 0 auto; /* center the whole block */
}

.section-title {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 8px;
}

.assign-section {
  margin-bottom: 20px;
}

.assign-wrapper {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.assign-btn {
  flex-shrink: 0;
}

.access-list {
  list-style: none;
  padding: 0;
}

.access-list li {
  display: flex;
  justify-content: space-between;
  padding: 8px;
  border-bottom: 1px solid #ddd;
}

.access-list li:hover {
  background-color: #f8f9fa;
}
</style>