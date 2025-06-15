export function toReadableStatus(status) {
    if (status === 'TO_LEARN') {
        return 'To learn'
    } else if (status === 'LEARNT') {
        return 'Done'
    } else if (status === 'POSTPONED') {
        return 'Postponed'
    } else if (status === 'EDIT') {
        return 'Edit'
    } else {
        return status
    }
}

export function shuffle(arr) {
    return [...arr].sort(() => Math.random() - 0.5)
}

export function textToArray(textarea) {
    return textarea.trim()
        .split("\n")                // Split the input string into an array of lines
        .map((line) => line.trim()) // Remove leading/trailing whitespace from each line
        .filter(Boolean);           // Remove empty lines (falsy values like "")
}

export function getFullDayName(day) {
    const dayNames = {
        sun: "Sunday",
        mon: "Monday",
        tue: "Tuesday",
        wed: "Wednesday",
        thu: "Thursday",
        fri: "Friday",
        sat: "Saturday"
    };

    return dayNames[day.toLowerCase()] || day; // In case of an invalid input, return the original value
}

export function formatTimeSlot(timeSlot) {
    const formattedStartTime = timeSlot.startTime?.substring(0, 5); // get hours and minutes (HH:mm)
    const formattedEndTime = timeSlot.endTime?.substring(0, 5); // get hours and minutes (HH:mm)
    return `${formattedStartTime} - ${formattedEndTime}`;
}

export function dateHappened(endDate) {
    return endDate;
}

export function formatDateTime(dateTime) {
    return new Date(dateTime).toLocaleString(undefined,{
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric'
    });
}
