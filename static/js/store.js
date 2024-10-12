// store.js
import {reactive} from 'vue'

const defaultLimitSelection = 'DEFAULT_LIMIT';

function getExerciseLimitSelection() {
    return localStorage.getItem('exerciseLimitSelection') || defaultLimitSelection;
}

export const store = reactive({
    exerciseLimitSelection: getExerciseLimitSelection(),
    exerciseLimit: 5,
    selectLimit(selection) {
        this.exerciseLimitSelection = selection;
        localStorage.setItem('exerciseLimitSelection', selection);
    }
})
