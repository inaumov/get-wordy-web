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
