<script>
import {getUserClasses} from '@/js/auth-check.js';
import {dateHappened, formatTimeSlot} from "@/js/utils.js";

export default {
  name: 'MyClasses',
  components: {},
  data() {
    return {
      isExpanded: null,
      assignedClasses: []
    }
  },
  methods: {
    async fetchAttendeeClasses() {
      const response = await getUserClasses();
      this.assignedClasses = await response.json();
    },
    formatTimeSlot,
    dateHappened,
  },
  mounted() {
    this.fetchAttendeeClasses();
  },
  computed: {
    activeClasses() {
      return this.assignedClasses.filter(classInfo => classInfo.isActive);
    },
    pastClasses() {
      return this.assignedClasses.filter(classInfo => !!!classInfo.isActive);
    },
  }
};
</script>

<template>
  <div class="container p-4">

    <!-- ACTIVE -->
    <div v-if="activeClasses.length">
      <h4 class="section-title">Active Classes</h4>
      <div class="class-list">
        <div v-for="classInfo in activeClasses" :key="classInfo.classId" class="class-item active">
          <div class="class-info">
            <h5 class="class-name">{{ classInfo.name }}</h5>
            <div v-if="classInfo.timeSlots" class="class-schedule">
              <span class="text-muted" v-for="timeSlot in classInfo.timeSlots">
                <strong>{{ timeSlot.dayOfWeek }}: </strong>{{ formatTimeSlot(timeSlot) }}<br/>
              </span>
            </div>
            <p v-else class="text-muted">
              One time activity<span v-if="classInfo.endDate">, at {{ dateHappened(classInfo.endDate) }}</span>
            </p>
            <p class="class-notes" v-if="classInfo.notes">
              <strong>Notes for student: </strong> {{ classInfo.notes }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- PAST -->
    <div v-if="pastClasses.length" class="pt-4">
      <h4 class="section-title">Past Activities</h4>
      <div class="class-list">
        <div v-for="classInfo in pastClasses" :key="classInfo.classId" class="class-item past">
          <div class="class-info">
            <h5 class="class-name">{{ classInfo.name }}</h5>
            <p v-if="!classInfo.isActive" class="text-muted">
              Archived
            </p>
            <p v-else class="text-muted">
              One time activity<span v-if="classInfo.endDate">, at {{ dateHappened(classInfo.endDate) }}</span>
            </p>
            <p class="class-notes" v-if="classInfo.notes">
              <strong>Notes for student: </strong> {{ classInfo.notes }}
            </p>
          </div>
        </div>
      </div>
    </div>

  </div>

</template>

<style scoped>

.class-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
}

/* active classes */
.class-item.active {
  background: #c8e6c9; /* light green */
  border-left: 6px solid #388e3c; /* dark green */
}

/* past activities */
.class-item.past {
  background: #ffccbc; /* light red */
  border-left: 6px solid #e64a19; /* dark red */
}

/* common class styling */
.class-item {
  padding: 14px;
  border-radius: 6px;
  transition: background 0.2s ease;
}

.class-name {
  font-size: 18px;
  font-weight: bold;
  color: #444;
}

.class-notes {
  color: #333;
}

.class-schedule {
  color: #555;
  padding-bottom: 1rem;
}
</style>
