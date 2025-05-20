<script>
import {dateHappened, formatTimeSlot} from "@/js/utils.js";

export default {
  props: {
    classInfo: {
      type: Object,
      required: true,
    },
  },
  methods: {
    formatTimeSlot,
    dateHappened,
    editClass() {
      this.$emit('edit', this.classInfo);
    },
    updateActivation() {
      this.$emit('activation', this.classInfo);
    },
    deleteClass() {
      this.$emit('delete', this.classInfo);
    }
  },
};
</script>

<template>
  <div class="d-flex justify-content-between align-items-start">
    <!-- class info -->
    <div class="class-card p-3 rounded w-75">
      <div class="d-flex align-items-center justify-content-between">
        <span class="name">{{ classInfo.name }}</span>
        <span v-if="classInfo['isActive']" class="text-success">
          <i class="bi bi-dot"></i>Active
        </span>
        <span v-else class="text-danger">
          <i class="bi bi-dot"></i>Inactive
        </span>
      </div>
      <span class="text-muted">{{ classInfo.format }}</span>
      <div v-if="this.classInfo.scheduleType === 'REPEATABLE'">
        <span class="text-muted" v-for="(timeSlot, index) in this.classInfo.timeSlots" :key="index">
          <strong>{{ timeSlot.dayOfWeek }}: </strong>{{ formatTimeSlot(timeSlot) }}
          <i v-if="index < this.classInfo.timeSlots.length - 1" class="bi bi-dot"></i>
        </span>
      </div>
      <div v-if="this.classInfo.scheduleType === 'ONE_TIME'">
        <span class="text-muted">
          One time activity<span>, at {{ dateHappened(this.classInfo.endDate) }}</span>
        </span>
      </div>
      <p class="class-notes" v-if="this.classInfo.notes">
        <strong>Notes for student: </strong> {{ this.classInfo.notes }}
      </p>
    </div>
    <!-- status and actions -->
    <div class="ms-auto">
      <button class="btn btn-light dropdown-toggle" type="button" data-bs-toggle="dropdown">
        Actions
      </button>
      <ul class="dropdown-menu">
        <li>
          <a class="dropdown-item" href="#" @click="editClass">
            Edit
          </a>
        </li>
        <li>
          <a class="dropdown-item" href="#" @click="updateActivation">
            {{ classInfo.isActive ? 'Disable' : 'Enable' }}
          </a>
        </li>
        <li>
          <a class="dropdown-item" style="color: firebrick" href="#" @click="deleteClass">
            Delete
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.class-card {
  border-radius: 0.5rem;
  background: lightgrey;
}

.class-card .name {
  font-size: 1.25rem;
  font-weight: bold;
}

.text-success, .text-danger {
  font-size: 0.9rem;
  margin: 0 0.5rem;
}

.dropdown-toggle {
  background-color: #f8f9fa;
}

.dropdown-item:hover {
  background-color: #e9ecef;
}

</style>
