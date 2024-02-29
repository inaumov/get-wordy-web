// store.js
import {reactive} from 'vue'

export const store = reactive({
    exerciseLimitSelection: 'ROLL_DICE',
    exerciseLimit: 5,
    selectLimit(selection) {
        this.exerciseLimitSelection = selection;
    }
})
