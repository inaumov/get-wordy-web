<script>
import {fetchClasses} from '@/js/classes-api.js';
import {dateHappened, formatDateTime, formatTimeSlot, getFullDayName} from '@/js/utils.js'
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
      searchTerm: "",
      showActiveOnly: false,
      hasDraftsOnly: false
    }
  },
  methods: {
    getFullDayName,
    async getData() {
      const response = await fetchClasses();
      let dayClasses = await response.json();
      this.classList = dayClasses
          .sort((cls1, cls2) => cls2.isActive - cls1.isActive)
          .map(item => {
        // add dynamic properties based on the condition
        return {
          ...item, // spread the existing properties
          hasParticipants: item['participants'] && item['participants'].length >= 1,
          hasDrafts: item['draftsCount'] && item['draftsCount'] >= 0,
        };
      });
    },
    navigateToClassDetails(classItem) {
      this.router.push({
            name: 'class-dashboard',
            params: {
              classId: classItem['classId']
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
    // get the badge class for a specific participant in a specific class
    getBadgeClass(classItem, index) {
      // if colorOrder is not defined, shuffle colors for this class
      if (!classItem.colorOrder) {
        classItem.colorOrder = this.shuffleColors();
      }
      // return the class based on the shuffled color order for the class
      return classItem.colorOrder[index % classItem.colorOrder.length];
    },
    // get the badge class randomly
    pickBadgeColorClass(count) {
      const colors = this.shuffleColors();
      const colorCount = colors.length;
      const index = colorCount % count;
      return colors[index];
    },
    formatTimeSlot,
    dateHappened,
    formatDateTime,
    clearSearch() {
      this.searchTerm = "";
    }
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
          // filter by search term (by class name)
          .filter(cls => {
            if (!this.searchTerm) return true;
            const term = this.searchTerm.toLowerCase();
            return cls.name.toLowerCase().includes(term);
          })
          // filter by format
          .filter(cls => {
            return this.selectedFormat === '' || cls.format === this.selectedFormat;
          })
          // filter by active status
          .filter(cls => {
            if (!this.showActiveOnly) return true;
            return cls.isActive;
          })
          // filter by drafts presence
          .filter(cls => {
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
    <!-- search -->
    <div class="group-search-input d-flex align-items-center position-relative mb-3" style="max-width: 300px;">
      <input
          class="form-control px-3 py-1"
          type="text"
          v-model="searchTerm"
          placeholder="Search by name..."
          autocomplete="off"
          style="padding: 6px 12px; line-height: 1.5;"
      />
      <button
          v-if="searchTerm"
          @click="clearSearch"
          class="btn btn-sm position-absolute top-50 end-0 translate-middle-y me-2 px-1 py-0 text-muted"
          style="z-index: 2; background: none; border: none;"
          aria-label="Clear search"
      >
        &times;
      </button>
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
      <div class="row g-3">
        <div class="col-md-3" v-for="classItem in filteredClasses"
             :key="classItem['classId']"
             @click="navigateToClassDetails(classItem)">
          <div class="group px-3 py-2">
            <div>
              <div class="d-flex align-items-center justify-content-between">
                <span class="ellipsis d-inline-block text-dark" style="max-width: 190px;">
                  <strong>{{ classItem['name'] }}</strong>
                </span>
                <span v-if="classItem['isActive']" class="active d-flex align-items-center">
                  <i class="bi bi-dot"></i>Active
                </span>
                <span v-else-if="!classItem['isActive']" class="disabled d-flex align-items-center">
                  <i class="bi bi-dot"></i>Disabled
                </span>
              </div>
              <div v-if="classItem['format']" class="my-1">
                <span class="text-primary-emphasis">{{ classItem['format'] }}</span>
              </div>
              <span v-if="classItem.hasDrafts" class="text-muted" :class="pickBadgeColorClass(classItem.drafts)">
                  <strong>Drafts</strong>: {{ classItem['draftsCount'] }}
              </span>
              <span v-else-if="classItem['lastUpdatedAt']" class="text-muted">
                  <strong>Last vocabulary update</strong>: {{ formatDateTime(classItem['lastUpdatedAt']) }}
              </span>
              <span v-else class="text-muted text-end">
                  No vocabularies added yet
              </span>
              <div class="py-1">
                <div v-if="classItem.timeSlots">
                  <span v-for="timeSlot in classItem.timeSlots" :key="timeSlot.dayOfWeek" class="text-muted">
                    <strong>{{ timeSlot.dayOfWeek }}</strong>: {{ formatTimeSlot(timeSlot) }}<br/>
                  </span>
                </div>
                <span v-else-if="classItem.endDate" class="text-muted">
                  <strong>One time activity</strong>: {{ dateHappened(classItem.endDate) }}
                </span>
                <span class="text-muted" v-else>No time slot assigned</span>
              </div>
              <div class="">
                <span class="text-muted">
                  <strong>Participants</strong>: {{ classItem['participants'].length }}
                </span>
                <div v-if="classItem.hasParticipants" class="badge-container">
                  <span
                      v-for="(user, attendeeIndex) in classItem['participants']"
                      :key="attendeeIndex"
                      :class="getBadgeClass(classItem, attendeeIndex)"
                      class="badge"
                  >
                    {{ user }}
                  </span>
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
.group {
  cursor: pointer;
  min-height: 198px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

.class-schedule {
  font-size: 0.9rem;
  color: #444;
}

.badge-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

</style>
