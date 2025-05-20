<script>
export default {
  props: ['classInfo'],
  data() {
    return {
      days: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
      SCHEDULE_TYPES: {
        NONE: 'NONE',
        REPEATABLE: 'REPEATABLE',
        ONE_TIME: 'ONE_TIME'
      },
    }
  },
  methods: {
    onSubmit: function () {
      const scheduleType = this.classInfo.scheduleType;

      if (scheduleType === this.SCHEDULE_TYPES.REPEATABLE) {
        // reset any previous validation state
        this.$refs.timeSlots.classList.remove('is-invalid');
        // validate the selected days
        if (this.classInfo.timeSlots.length === 0) {
          // if no days are selected, apply the 'is-invalid' class
          this.$refs.timeSlots.classList.add('is-invalid');
          return;
        }
      }

      if (scheduleType === this.SCHEDULE_TYPES.ONE_TIME) {
        // reset any previous validation state
        this.$refs.classOneTimeDate.classList.remove('is-invalid');
        // validate class one time date
        if (!this.classInfo.endDate) {
          // if no end date selected, apply the 'is-invalid' class
          this.$refs.classOneTimeDate.classList.add('is-invalid');
          return;
        }
      }

      let form = document.getElementById('start-class-form');
      let formData = new FormData(form);

      let requestData = {
        name: formData.get('name'),
        format: formData.get('format'),
        notes: formData.get('notes'),
        scheduleType: scheduleType,
      };
      if (scheduleType === this.SCHEDULE_TYPES.REPEATABLE) {
        requestData['timeSlots'] = this.classInfo.timeSlots;
      }
      if (scheduleType === this.SCHEDULE_TYPES.ONE_TIME) {
        requestData['endDate'] = this.classInfo.endDate;
      }
      // emit the structured requestData to parent
      this.$emit('classInfoSubmit', requestData);
    },
    addTimeSlot() {
      this.classInfo.timeSlots.push({ dayOfWeek: '', startTime: '', endTime: '' });
    },
    removeTimeSlot(index) {
      this.classInfo.timeSlots.splice(index, 1);
    },
  },
};

</script>

<template>
  <div class="container mt-5 d-flex justify-content-center">
    <div class="w-100" style="max-width: 800px;">
      <form id="start-class-form" @submit.prevent="onSubmit">

        <!-- first row: class name and class format -->
        <div class="row mb-3">
          <div class="col-6">
            <label for="name" class="form-label">Name<i>*</i></label>
            <input type="text" v-model="classInfo['name']" class="form-control" id="name" name="name"
                   autocomplete="off" required>
          </div>
          <div class="col-6">
            <label for="format" class="form-label">Format</label>
            <input type="text" v-model="classInfo['format']" class="form-control" id="format"
                   name="format"
                   autocomplete="off">
            <small class="form-text text-muted">e.g., Online, In-person, Hybrid, VIP</small>
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-6">
            <label class="form-label">Schedule Type</label>
            <select v-model="classInfo.scheduleType" class="form-select">
              <option :value="SCHEDULE_TYPES.NONE">None</option>
              <option :value="SCHEDULE_TYPES.REPEATABLE">Repeatable</option>
              <option :value="SCHEDULE_TYPES.ONE_TIME">One-Time</option>
            </select>
          </div>
        </div>

        <!-- time slots selection (visible only if repeatable) -->
        <div v-if="classInfo.scheduleType === SCHEDULE_TYPES.REPEATABLE" class="row mb-3">
          <div class="col-12">
            <label for="timeSlots" class="form-label">
              Select the time slots for the class<i>*</i>
            </label>
            <div ref="timeSlots" id="timeSlots" class="d-flex flex-column gap-2">
              <div
                  v-for="(timeSlot, index) in this.classInfo.timeSlots"
                  :key="index"
                  class="d-flex align-items-center gap-3"
              >
                <select
                    v-model="timeSlot.dayOfWeek"
                    class="form-select w-auto"
                    :id="'timeslot-day-' + index"
                    required
                >
                  <option value="" disabled>Select Day</option>
                  <option v-for="day in days" :key="day" :value="day">
                    {{ day }}
                  </option>
                </select>
                <input
                    type="time"
                    v-model="timeSlot.startTime"
                    class="form-control w-auto"
                    :id="'timeslot-start-' + index"
                    required
                />
                <input
                    type="time"
                    v-model="timeSlot.endTime"
                    class="form-control w-auto"
                    :id="'timeslot-end-' + index"
                    required
                />
                <!-- remove time slots button -->
                <button
                    type="button"
                    class="btn btn-danger btn-sm"
                    @click="removeTimeSlot(index)"
                >
                  Remove
                </button>

              </div>
            </div>

            <!-- Validation Feedback -->
            <div v-if="this.classInfo?.timeSlots?.length === 0" class="invalid-feedback">
              At least one slot required.
            </div>

            <!-- add time slots button -->
            <button
                type="button"
                class="btn btn-primary mt-2"
                @click="addTimeSlot"
            >
              Add more slots
            </button>

          </div>
        </div>

        <!-- one-time end date selection -->
        <div v-if="classInfo.scheduleType === SCHEDULE_TYPES.ONE_TIME" class="row mb-3">
          <div ref="classOneTimeDate" id="classOneTimeDate" class="col-12">
            <label class="form-label" for="endDate">Date scheduled<i>*</i></label>
            <input type="date" id="endDate" v-model="this.classInfo.endDate" class="form-control">
          </div>
          <!-- Validation Feedback -->
          <div v-if="!this.classInfo.endDate" class="invalid-feedback">
            Please select the end date for this activity.
          </div>
        </div>

        <!-- last row: notes (textarea) -->
        <div class="row mb-3">
          <div class="col-12">
            <label for="notes" class="form-label">Any additional notes for student</label>
            <textarea class="form-control" id="notes" name="notes" rows="5" maxlength="500"
                      v-model="this.classInfo['notes']"
                      autocomplete="off"></textarea>
            <small class="form-text text-muted">Max 500 characters</small>
          </div>
        </div>

        <!-- submit / back Buttons -->
        <div class="row py-4">
          <div class="d-flex flex-column align-items-end">
            <button type="submit" class="btn btn-secondary">Submit</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style>
</style>
