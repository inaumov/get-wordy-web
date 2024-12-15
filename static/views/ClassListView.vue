<script>
import {fetchClasses} from '@/js/classes-api.js';
import {applyCaption} from '@/js/utils.js'
import {useRouter} from "vue-router";

export default {
  props: ['dayOfWeek'],
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
    async getData() {
      const response = await fetchClasses(this.dayOfWeek);
      let items = await response.json();
      this.classList = items.map(item => {
        // add the 'hasAttendees' property based on the condition
        return {
          ...item, // spread the existing properties
          hasAttendees: item['attendees'] && item['attendees'].length >= 1
        };
      });
    },
    navigateToWordsheetList(classItem) {
      this.router.push({
            name: 'class-wordsheet-list',
            params: {
              classId: classItem['classId']
            }
          });
      console.log("Selected class: id = ", classItem['classId'], ", name = ", classItem['name'])
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
    }
  },
  computed: {
    hasClasses() {
      return this.classList && this.classList.length > 0;
    }
  },
  mounted() {
    this.getData()
    applyCaption('Desna Academy')
  }
};
</script>

<template>
  <div v-if="hasClasses" class="container mt-5">
    <div class="day-groups">
      <h3 class="my-3">{{ dayOfWeek }}</h3>
      <div class="row">
        <div class="col-md-4 card" v-for="classItem in classList"
             :key="classItem['classId']"
             @click="navigateToWordsheetList(classItem)">
          <div class="card-body">
            <span class="class-id">Class ID: {{ classItem['classId'] }}</span>
            <div>
              <div class="class-details">
                <span>{{ classItem['name'] }}</span>
              </div>
              <div class="class-details">
                <span><strong>Attendees</strong>:</span>
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
              <div v-if="classItem['format']" class="class-details">
                <span><strong>Format</strong> : {{ classItem['format'] }}</span>
              </div>
              <div v-if="classItem['level']" class="class-details">
                <span><strong>Level</strong> : {{ classItem['level'] }}</span>
              </div>
              <div v-if="classItem['material']" class="class-details">
                <span><strong>Materials</strong> : {{ classItem['material'] }}</span>
              </div>
              <div v-if="classItem['notes']" class="class-details">
                <span><strong>Notes</strong> : {{ classItem['notes'] }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-4 card" @click="addNewClass()"
             data-bs-toggle="tooltip"
             data-bs-placement="right"
             title="Start a new class"
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
  <div v-else class="d-flex justify-content-center p-5">
    <p class="lead">No classes registered yet, please create.</p>
  </div>
</template>

<style scoped>
.card {
  cursor: pointer;
  min-height: 178px;
}

.class-id {
  color: grey;
  font-size: 0.75rem;
  right: 10px;
  top: 10px;
}

.day-groups h3 {
  font-size: 1.5rem;
}

.badge {
  padding: 0.5rem;
  margin: 0.25rem;
  color: DarkSlateGrey;
  font-weight: bold;
}

.pastel-blue { background-color: rgba(124, 185, 232, 0.9); }
.pastel-pink { background-color: rgba(247, 168, 184, 0.9); }
.pastel-yellow { background-color: rgba(255, 178, 48, 1); }  /* darker yellow */
.pastel-green { background-color: rgba(168, 213, 186, 0.9); }
.pastel-lavender { background-color: rgba(185, 168, 217, 0.9); }
.pastel-peach { background-color: rgba(255, 124, 85, 1); }   /* darker peach/orange */
.pastel-mint { background-color: rgba(141, 228, 161, 0.9); }
.pastel-coral { background-color: rgba(255, 111, 97, 0.9); }
.pastel-lilac { background-color: rgba(216, 167, 208, 0.9); }
.pastel-lemon { background-color: rgba(255, 228, 87, 1); }  /* slightly darker yellow */
.pastel-sky { background-color: rgba(163, 201, 255, 0.9); }
.pastel-beige { background-color: rgba(244, 209, 176, 0.9); }

</style>
