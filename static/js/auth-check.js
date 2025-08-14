const usersAPI = import.meta.env.VITE_USERS_API;

import {ref, provide, inject} from 'vue';

const isLoggedIn = ref(false);
const permissions = ref([]);
const school = ref([]);

export async function checkLoginStatus() {
    try {
        const response = await fetch(usersAPI + '/auth/status', {
            method: 'GET',
            credentials: 'include', // include cookies in the request,
            headers: {
                'Cache-Control': 'no-cache', // prevent browser caching
            },
        });

        if (response.ok) {
            const data = await response.json();
            isLoggedIn.value = data['loggedIn'];
            if (data['loggedIn']) {
                await loadPermissions();
                // only learners have settings for now
                if (permissions.value.some(p => p === 'P_MANAGE_OWN_CARDS')) {
                    await loadUserSettings();
                }
            }
        } else {
            isLoggedIn.value = false;
        }
    } catch (error) {
        console.error('Error checking login status:', error);
        isLoggedIn.value = false;
    }
}

export async function logout() {
    try {
        const response = await fetch('/logout', {
            method: 'GET',
            credentials: 'include', // include cookies in the request,
            headers: {
                'Cache-Control': 'no-cache', // prevent browser caching
            },
        });

        if (response.ok) {
            isLoggedIn.value = false;
        }
    } catch (error) {
        console.error('Error while logout:', error);
    }
}

async function loadPermissions() {
    try {
        const response = await fetch(usersAPI + '/meta', {
            method: 'GET',
            credentials: 'include', // include cookies in the request,
            headers: {
                'Cache-Control': 'no-cache', // prevent browser caching
            },
        });

        if (response.ok) {
            const meta = await response.json();
            permissions.value = meta['permissions'];
            school.value = meta['school'];
        }
    } catch (error) {
        console.error('Error while getting user permissions:', error);
    }
}

/**
 * @typedef {Object} Settings
 * @property {number} cardsLimitExercise
 * @property {boolean} schoolUpdatesEnabled
 */

/**
 * @returns {Promise<Settings>}
 */
async function loadUserSettings() {
    try {
        const response = await fetch(usersAPI + "/settings", {
            method: 'GET',
            credentials: 'include', // include cookies in the request,
            headers: {
                'Cache-Control': 'no-cache', // prevent browser caching
            }
        });
        if (response.ok) {
            /** @type {Settings} */
            const data = await response.json();

            if (data?.cardsLimitExercise) {
                localStorage.setItem("cardsLimitExercise", data.cardsLimitExercise);
                console.log("Cards limit:", data.cardsLimitExercise);
            }
        }
    } catch (err) {
        console.error("Failed to fetch settings:", err);
    }
}

export function getUserClasses() {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        headers: headers,
    };

    let getRequest = new Request(usersAPI + "/my-classes", initObject);

    return fetch(getRequest)
        .catch(err => console.error("Error fetching accessible classes:", err));
}

export function provideAuth() {
    provide('isLoggedIn', isLoggedIn);
    provide('checkLoginStatus', checkLoginStatus);
    provide('setLoginStatus', (status) => {
        isLoggedIn.value = status;
    });
    provide('logout', logout);
    provide('permissions', permissions);
    provide('school', school);
}

export function useAuth() {
    return {
        isLoggedIn: inject('isLoggedIn'),
        checkLoginStatus: inject('checkLoginStatus'),
        setLoginStatus: inject('setLoginStatus'),
        logout: inject('logout'),
        permissions: inject('permissions'),
        school: inject('school')
    };
}

export function hasPermissions() {
    return permissions.value.length > 0;
}
