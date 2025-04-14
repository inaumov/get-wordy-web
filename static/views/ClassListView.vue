<script>
import {fetchClasses} from '@/js/classes-api.js';
import {getFullDayName} from '@/js/utils.js'
import {useRouter} from "vue-router";

export default {
  props: {
    classList: {
      type: Array,
      required: true
    }
  },
  name: 'ClassListView',
  setup() {
    let router = useRouter();
    return {router}
  },
  data() {
    return {
      classList: [],
      selectedFormat: "",
      day: null,
      showActiveOnly: false,
      hasDraftsOnly: false
    }
  },
  methods: {
    getFullDayName,
    async getData() {
      const response = await fetchClasses(this.day);
      let dayClasses = await response.json();
      this.classList = dayClasses
          // .filter(item =>
          //     item.schedules?.some(schedule => schedule?.dayOfWeek.toLowerCase() === this.day.toLowerCase())
          // )
          .sort((cls1, cls2) => cls2.isActive - cls1.isActive)
          .map(item => {
        // add dynamic properties based on the condition
        return {
          ...item, // spread the existing properties
          hasAttendees: item['attendees'] && item['attendees'].length >= 1,
          timeSlots: item['schedules']?.map(x => {
            return this.formatTimeSlot(x)
          }),
          hasDrafts: item['drafts'] && item['drafts'] >= 0,
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
    },
    uniqueFormats() {
      const formats = this.classList.map(c => c.format);
      return [...new Set(formats)];
    },
    filteredClasses() {
      return this.classList
          .filter(cls => {
            // filter by format
            return this.selectedFormat === '' || cls.format === this.selectedFormat;
          })
          .filter(cls => {
            // filter by active status
            if (!this.showActiveOnly) return true;
            return cls.isActive;
          })
          .filter(cls => {
            // filter by drafts presence
            if (!this.hasDraftsOnly) return true;
            return cls.hasDrafts;
          });
    }
  },
  mounted() {
    this.getData()
  }
};
</script>

<template>
  <div class="p-4 d-flex justify-content-start">
    <h4>Groups</h4>
  </div>
  <div class="p-4 d-flex justify-content-end" style="gap: 20px">
    <div class="input-group" style="max-width: 240px">
      <input id="group-search-input" type="text" class="form-control" name="groups-name" placeholder="Search for a group"
             autocomplete="off"
             required>
      <span class="input-group-btn">
                <button type="submit" class="btn btn-md btn-default border">
                  <i class="bi bi-search"></i>
                </button>
      </span>
    </div>
    <router-link :to="{name: 'add-new-class'}" class="btn btn-light" title="Create group">Create group</router-link>
  </div>
  <div class="px-4 d-flex justify-content-start" style="gap: 40px">
    <!-- format filter -->
    <div>
      <select
          v-model="selectedFormat"
          class="form-select px-3 py-1"
          id="formatFilter"
          style="border-radius: 1rem; width: auto; min-width: 180px; padding: 6px 12px; line-height: 1.5;"
      >
        <option value="">All formats</option>
        <option v-for="format in uniqueFormats" :key="format" :value="format">
          {{ format }}
        </option>
      </select>
    </div>
    <!-- active groups checkbox filter -->
    <div class="form-check py-1">
      <input
          class="form-check-input"
          type="checkbox"
          id="activeGroupsOnly"
          v-model="showActiveOnly"
      />
      <label class="form-check-label" for="activeGroupsOnly">
        Show active groups only
      </label>
    </div>
    <!-- has drafts checkbox filter -->
    <div class="form-check py-1">
      <input
          class="form-check-input"
          type="checkbox"
          id="hasDraftsOnly"
          v-model="hasDraftsOnly"
      />
      <label class="form-check-label" for="hasDraftsOnly">
        Has drafts
      </label>
    </div>
  </div>

  <div v-if="hasClasses" class="container p-4">
    <div class="day-groups">
      <div class="row mx-1">
        <div class="col-md-3 card" v-for="classItem in filteredClasses"
             :key="classItem['classId']"
             @click="navigateToClassDetails(classItem)">
          <div class="py-2">
            <div>
              <div class="d-flex align-items-center justify-content-between">
                <span><strong>{{ classItem['name'] }}</strong></span>
                <span v-if="classItem['isActive']" class="active d-flex align-items-center">
                  <i class="bi bi-dot"></i>&nbsp;Active</span>
                <span v-else-if="!classItem['isActive']" class="disabled d-flex align-items-center">
                  <i class="bi bi-dot"></i>&nbsp;Disabled</span>
              </div>
              <div v-if="classItem['format']" class="">
                <span class="text-muted">{{ classItem['format'] }}</span>
              </div>
              <div class="">
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
              <div class="">
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
      </div>
    </div>
  </div>
  <div v-else class="d-flex justify-content-center align-items-center">
    <div class="text-center w-50">
      <p class="lead">No vocabulary streamlining group has been registered yet. Please create one.</p>
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

.disabled {
  color: grey;
  font-size: 13px;
}

.active {
  color: green;
  font-size: 13px;
}

</style>
