<script>
import {getUserClasses} from '@/js/classes-api.js';

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
    formatSchedule(schedule) {
      const formattedStartTime = schedule.startTime?.substring(0, 5); // get hours and minutes (HH:mm)
      const formattedEndTime = schedule.endTime?.substring(0, 5); // get hours and minutes (HH:mm)
      return `${formattedStartTime} - ${formattedEndTime}`;
    },
    dateHappened(endDate) {
      return endDate;
    }
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
            <p class="class-schedule" v-if="classInfo.schedules">
              <span class="text-muted" v-for="schedule in classInfo.schedules">
                <strong>{{ schedule.dayOfWeek }}: </strong>{{ formatSchedule(schedule) }}<br/>
              </span>
            </p>
            <span v-else>No schedule assigned</span>
            <p class="class-material">
              <strong>Materials / References: </strong> {{ classInfo.material }}
            </p>
            <p class="class-material">
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
            <p class="text-muted" v-if="classInfo.endDate">
              One time activity, at {{ dateHappened(classInfo.endDate) }}
            </p>
            <p v-else class="text-muted">
              Repeatable classes (Archived)
            </p>
            <p class="class-material">
              <strong>Materials / References: </strong> {{ classInfo.material }}
            </p>
            <p class="class-material">
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
  cursor: pointer;
}

.class-name {
  font-size: 18px;
  font-weight: bold;
  color: #444;
}

.class-material {
  color: #333;
}

.class-schedule {
  color: #555;
}
</style>
