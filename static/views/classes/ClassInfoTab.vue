<script>
import {updateClassInfo} from '@/js/classes-api.js';

export default {
  props: ['classInfo'],
  setup(props) {
    console.log("Prop classInfo in setup:", props.classInfo);
    return {
    };
  },
  data() {
    return {
      days: ["Sun", "Mon", "Tue", "Wen", "Thu", "Fri", "Sat"],
      schedules: [
        { dayOfWeek: '', startTime: '', endTime: '' } // Default schedule
      ],
    }
  },
  created() {
    console.log("Prop classInfo in created:", this.classInfo);
  },
  computed: {
    isRepeatableLocal: {
      get() {
        return this.classInfo.isRepeatable;
      },
      set(value) {
        this.classInfo.isRepeatable = value;
      }
    },
    endDateLocal: {
      get() {
        return this.classInfo.endDate;
      },
      set(value) {
        this.classInfo.endDate = value;
      }
    }
  },
  methods: {
    onSubmit: function () {
      if (this.isRepeatableLocal === true) {
        // reset any previous validation state
        this.$refs.classSchedules.classList.remove('is-invalid');
        // validate the selected days
        if (this.classInfo.schedules.length === 0) {
          // if no days are selected, apply the 'is-invalid' class
          this.$refs.classSchedules.classList.add('is-invalid');
          return;
        }
      }
      if (this.isRepeatableLocal === false) {
        // reset any previous validation state
        this.$refs.classOneTimeDate.classList.remove('is-invalid');
        // validate class one time date
        if (!this.endDateLocal) {
          // if no end date selected, apply the 'is-invalid' class
          this.$refs.classOneTimeDate.classList.add('is-invalid');
          return;
        }
      }

      let form = document.getElementById('start-class-form');
      let formData = new FormData(form);

      let isRepeatable = this.isRepeatableLocal;
      let requestData = {
        name: formData.get('name'),
        format: formData.get('format'),
        notes: formData.get('notes'),
        isRepeatable: isRepeatable,
      };
      if (isRepeatable === true) {
        requestData['schedules'] = this.classInfo.schedules
      }
      if (isRepeatable === false) {
        requestData['endDate'] = this.endDateLocal
      }
      updateClassInfo(requestData)
          .then(response => {
            if (response.ok) {
            }
            console.log("PUT: class info has been requested. Response.status =", response.status);
          });
    },
    addSchedule() {
      this.classInfo.schedules.push({ dayOfWeek: '', startTime: '', endTime: '' });
    },
    removeSchedule(index) {
      this.classInfo.schedules.splice(index, 1);
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
            <input type="text" v-model="this.classInfo['name']" class="form-control" id="name" name="name"
                   autocomplete="off" required>
          </div>
          <div class="col-6">
            <label for="format" class="form-label">Format</label>
            <input type="text" v-model="this.classInfo['format']" class="form-control" id="format"
                   name="format"
                   autocomplete="off">
            <small class="form-text text-muted">e.g., Online, In-person, Hybrid, VIP</small>
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-6">
            <label class="form-label">Class Type</label>
            <select v-model="isRepeatableLocal" class="form-select">
              <option :value="true">Repeatable (Scheduled)</option>
              <option :value="false">One-time Class</option>
            </select>
          </div>
        </div>

        <!-- schedule selection (visible only if repeatable) -->
        <div class="row mb-3" v-if="this.isRepeatableLocal === true">
          <div class="col-12">
            <label for="classSchedules" class="form-label">
              Select the schedules for the class<i>*</i>
            </label>
            <div ref="classSchedules" id="classSchedules" class="d-flex flex-column gap-2">
              <div
                  v-for="(schedule, index) in this.classInfo.schedules"
                  :key="index"
                  class="d-flex align-items-center gap-3"
              >
                <select
                    v-model="schedule.dayOfWeek"
                    class="form-select w-auto"
                    :id="'schedule-day-' + index"
                    required
                >
                  <option value="" disabled>Select Day</option>
                  <option v-for="day in days" :key="day" :value="day">
                    {{ day }}
                  </option>
                </select>
                <input
                    type="time"
                    v-model="schedule.startTime"
                    class="form-control w-auto"
                    :id="'schedule-start-' + index"
                    required
                />
                <input
                    type="time"
                    v-model="schedule.endTime"
                    class="form-control w-auto"
                    :id="'schedule-end-' + index"
                    required
                />
                <!-- Remove Schedule Button -->
                <button
                    type="button"
                    class="btn btn-danger btn-sm"
                    @click="removeSchedule(index)"
                >
                  Remove
                </button>

              </div>
            </div>

            <!-- Validation Feedback -->
            <div v-if="this.classInfo?.schedules?.length === 0" class="invalid-feedback">
              Please add at least one schedule.
            </div>

            <!-- Add Schedule Button -->
            <button
                type="button"
                class="btn btn-primary mt-2"
                @click="addSchedule"
            >
              Add more slots
            </button>

          </div>
        </div>

        <!-- one-time end date selection -->
        <div v-if="this.isRepeatableLocal === false" class="row mb-3">
          <div ref="classOneTimeDate" id="classOneTimeDate" class="col-12">
            <label class="form-label" for="endDate">Date scheduled<i>*</i></label>
            <input type="date" id="endDate" v-model="this.endDateLocal" class="form-control">
          </div>
          <!-- Validation Feedback -->
          <div v-if="!this.endDateLocal" class="invalid-feedback">
            Please select the end date for this activity.
          </div>
        </div>

        <!-- last row: notes (textarea) -->
        <div class="row mb-3">
          <div class="col-12">
            <label for="notes" class="form-label">Any additional notes</label>
            <textarea class="form-control" id="notes" name="notes" rows="5" maxlength="500"
                      v-model="this.classInfo['notes']"
                      autocomplete="off"></textarea>
            <small class="form-text text-muted">Max 500 characters</small>
          </div>
        </div>

        <!-- submit / back Buttons -->
        <div class="row py-4">
          <div class="d-flex flex-column align-items-end">
            <button type="submit" class="btn btn-primary">Save</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style>
</style>
