export function shuffle(arr) {
    return [...arr].sort(() => Math.random() - 0.5)
}

export function textToArray(textarea) {
    return textarea.trim().split(/\r?\n/).filter(elm => elm);
}

export function arrayToText(strings) {
    if (strings && Array.isArray(strings)) {
        return strings.join('\n');
    }
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
