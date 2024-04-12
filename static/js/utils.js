export function applyCaption(caption) {
    let captionH4 = document.getElementById('caption');
    captionH4.innerHTML = caption
}

export function shuffle(arr) {
    return [...arr].sort(() => Math.random() - 0.5)
}

export function textToArray(textarea) {
    return textarea.trim().split(/\r?\n/).filter(elm => elm);
}
