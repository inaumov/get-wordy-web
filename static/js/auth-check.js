import {ref, provide, inject} from 'vue';

const isLoggedIn = ref(false);
const permissions = ref([]);

export async function checkLoginStatus() {
    try {
        const response = await fetch('/users/auth/status', {
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
        const response = await fetch('/users/permissions', {
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

export function provideAuth() {
    provide('isLoggedIn', isLoggedIn);
    provide('checkLoginStatus', checkLoginStatus);
    provide('setLoginStatus', (status) => {
        isLoggedIn.value = status;
    });
    provide('logout', logout);
}

export function useAuth() {
    return {
        isLoggedIn: inject('isLoggedIn'),
        checkLoginStatus: inject('checkLoginStatus'),
        setLoginStatus: inject('setLoginStatus'),
        logout: inject('logout')
    };
}
