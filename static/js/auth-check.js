const usersAPI = import.meta.env.VITE_USERS_API;

import {ref, provide, inject} from 'vue';

const isLoggedIn = ref(false);
const permissions = ref([]);

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
        const response = await fetch(usersAPI + '/permissions', {
            method: 'GET',
            credentials: 'include', // include cookies in the request,
            headers: {
                'Cache-Control': 'no-cache', // prevent browser caching
            },
        });

        if (response.ok) {
            const data = await response.json();
            permissions.value = [...data];
        }
    } catch (error) {
        console.error('Error while getting user permissions:', error);
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
}

export function useAuth() {
    return {
        isLoggedIn: inject('isLoggedIn'),
        checkLoginStatus: inject('checkLoginStatus'),
        setLoginStatus: inject('setLoginStatus'),
        logout: inject('logout'),
        permissions: inject('permissions')
    };
}
