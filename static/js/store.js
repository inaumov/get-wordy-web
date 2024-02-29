// store.js
import {reactive} from 'vue'

const defaultlimitSelection = 'DEFAULT_LIMIT';

function getExerciseLimitSelection() {
    return localStorage.getItem('exerciseLimitSelection') || defaultlimitSelection;
}

export const store = reactive({
    exerciseLimitSelection: getExerciseLimitSelection(),
    exerciseLimit: 5,
    selectLimit(selection) {
        this.exerciseLimitSelection = selection;
        localStorage.setItem('exerciseLimitSelection', selection);
    }
})
