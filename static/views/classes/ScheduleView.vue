<script>

export default {
  data() {
    return {
      days: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
    };
  },
  methods: {
    isToday(day) {
      const today = new Date();
      const todayIndex = today.getDay(); // get day index (0-6)
      return this.days[todayIndex] === day;
    },
    navigateToClasses(day) {
      // navigate to the page showing classes for the selected day
      this.$router.push({name: 'day-classes', params: {day}});
    },
    addNewClass() {
      this.$router.push({
        name: 'add-new-class'
      });
    },
  },

}
</script>

<template>
  <div class="container p-4">
    <div class="row justify-content-center">
      <p class="lead text-center">View and manage vocabulary streamlining groups and their assignees.</p>
      <div class="text-center"
          v-for="(day, index) in days"
          :key="index"
          :class="['col-2 col-sm-1', 'mb-4', 'day-button-container', { 'today': isToday(day) }]"
      >
        <button
            class="btn w-100 day-btn d-flex justify-content-center align-items-center"
            @click="navigateToClasses(day)"
        >
          <span class="day-text">{{ day }}</span>
        </button>
      </div>
    </div>
    <div class="d-flex mt-5 justify-content-center align-items-center">
      <div class="text-center w-50">
        <p class="lead">Start a new group to streamline vocabulary learning.</p>
        <div class="d-flex justify-content-center mt-3">
          <div class="card text-center w-100"
               @click="addNewClass()"
               data-bs-toggle="tooltip"
               data-bs-placement="right"
               title="Start a new class"
               style="cursor: pointer;"
          >
            <div class="card-body d-flex justify-content-center align-items-center">
              <i class="bi bi-plus" style="font-size: 2rem;"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* general Button Styling */
.day-btn {
  font-size: 1.25rem;
  padding: 1.5rem;
  border-radius: 12px;
  transition: all 0.3s ease;
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  color: #495057;
  font-weight: 600;
  height: 100px; /* set fixed height for uniformity */
  min-width: 100px; /* minimum width for the buttons */
}

.day-btn:hover {
  background-color: #007bff;
  color: white;
  transform: translateY(-3px); /* slight lift effect on hover */
}

.day-btn:focus {
  outline: none;
}

/* today Button (active State) */
.today .day-btn {
  background-color: #007bff;
  color: white;
  font-weight: bold;
  animation: pulse 1s ease-in-out infinite;
}

.today .day-btn:hover {
  background-color: #0056b3;
  transform: translateY(0); /* remove lift effect for today */
}

/* animation for "Today" button */
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1); /* slight increase in size */
  }
  100% {
    transform: scale(1);
  }
}

/* optional: day text styling */
.day-text {
  text-transform: capitalize;
  letter-spacing: 1px;
  display: block;
}

/* spacing and responsive Layout */
.day-button-container {
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .col-2 {
    flex: 0 0 40%; /* 40% on smaller screens */
  }

  .col-sm-1 {
    flex: 0 0 30%; /* 30% on mobile */
  }
}

/* visual effects for button focus (for accessibility) */
.day-btn:focus {
  outline: 2px solid rgba(0, 123, 255, 0.5); /* blue outline for accessibility */
}

</style>