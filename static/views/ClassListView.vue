<script>
import {fetchClasses} from '@/js/classes-api.js';
import {getFullDayName} from '@/js/utils.js'
import {useRouter} from "vue-router";

export default {
  props: ['day'],
  name: 'ClassListView',
  setup() {
    let router = useRouter();
    return {router}
  },
  data() {
    return {
      classList: [],
    }
  },
  methods: {
    getFullDayName,
    async getData() {
      const response = await fetchClasses(this.day);
      let dayClasses = await response.json();
      this.classList = dayClasses
          .filter(item =>
              item.schedules?.some(schedule => schedule?.dayOfWeek.toLowerCase() === this.day.toLowerCase())
          )
          .map(item => {
        // add the 'hasAttendees' property based on the condition
        return {
          ...item, // spread the existing properties
          hasAttendees: item['attendees'] && item['attendees'].length >= 1,
          timeSlots: item['schedules'].map(x => {
            return this.formatTimeSlot(x)
          })
        };
      });
    },
    navigateToClassDetails(classItem) {
      this.router.push({
            name: 'class-details',
            params: {
              classId: classItem['classId'],
              day: this.day
            }
          });
    },
    addNewClass() {
      this.router.push({
        name: 'add-new-class'
      });
    },
    // method to shuffle colors randomly
    shuffleColors() {
      const pastelColors = [
        'pastel-blue', 'pastel-pink', 'pastel-yellow', 'pastel-green',
        'pastel-lavender', 'pastel-peach', 'pastel-mint', 'pastel-coral',
        'pastel-lilac', 'pastel-lemon', 'pastel-sky', 'pastel-beige'
      ];
      // shuffle the pastel colors array using the Fisher-Yates algorithm
      for (let i = pastelColors.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [pastelColors[i], pastelColors[j]] = [pastelColors[j], pastelColors[i]];
      }
      return pastelColors;
    },
    // get the badge class for a specific attendee in a specific class
    getBadgeClass(classItem, index) {
      // if colorOrder is not defined, shuffle colors for this class
      if (!classItem.colorOrder) {
        classItem.colorOrder = this.shuffleColors();
      }
      // return the class based on the shuffled color order for the class
      return classItem.colorOrder[index % classItem.colorOrder.length];
    },
    formatTimeSlot(timeSlot) {
      const formatTime = (time) =>
          new Date(`1970-01-01T${time}`).toLocaleTimeString([], {
            hour: '2-digit',
            minute: '2-digit',
          });
      return `${timeSlot['dayOfWeek']} : ${formatTime(timeSlot.startTime)} - ${formatTime(timeSlot.endTime)}`;
    },
  },
  computed: {
    hasClasses() {
      return this.classList && this.classList.length > 0;
    }
  },
  mounted() {
    this.getData()
  }
};
</script>

<template>
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'schedule'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>

  <div v-if="hasClasses" class="container p-4">
    <h4 class="pb-4">{{ getFullDayName(day) }}</h4>
    <div class="day-groups">
      <div class="row mx-1">
        <div class="col-md-4 card" v-for="classItem in classList"
             :key="classItem['classId']"
             @click="navigateToClassDetails(classItem)">
          <div class="card-body">
            <div>
              <div class="class-details">
                <span><strong>{{ classItem['name'] }}</strong></span>
              </div>
              <div v-if="classItem['format']" class="class-details">
                <span class="text-muted">{{ classItem['format'] }}</span>
              </div>
              <div class="class-details">
                <div v-if="classItem.timeSlots">
                  <span
                      v-for="(timeSlot, slotIndex) in classItem.timeSlots"
                      :key="slotIndex"
                      :class="getBadgeClass(classItem, slotIndex)"
                      class="badge">
                        {{ timeSlot }}
                  </span>
                </div>
                <div v-else>
                  None
                </div>
              </div>
              <div class="class-details">
                <span class="text-muted">
                  <strong>Attendees:</strong>
                </span>
                <div v-if="classItem.hasAttendees">
                  <span
                      v-for="(attendee, attendeeIndex) in classItem['attendees']"
                      :key="attendeeIndex"
                      :class="getBadgeClass(classItem, attendeeIndex)"
                      class="badge">
                    {{ attendee }}
                  </span>
                </div>
                <div v-else>
                  None
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-4 card" @click="addNewClass()"
             data-bs-toggle="tooltip"
             data-bs-placement="right"
             title="Start a new class"
             style="cursor: pointer;"
        >
          <div class="card-body d-flex justify-content-center align-items-center">
            <div>
              <!-- plus icon centered within the card -->
              <i class="bi bi-plus" style="font-size: 2rem;"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="d-flex justify-content-center align-items-center">
    <div class="text-center w-50">
      <p class="lead">No vocabulary streamlining group has been registered yet. Please create one.</p>
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
</template>

<style scoped>
.card {
  cursor: pointer;
  min-height: 178px;
}

.day-groups h3 {
  font-size: 1.5rem;
}

.badge {
  padding: 0.5rem;
  color: DarkSlateGrey;
  font-weight: bold;
}

</style>
