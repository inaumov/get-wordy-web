export function useMenuConfig(accessType) {
    const menuConfig = {
        teacherAccess: [
            {
                uri: '/Schedule',
                label: 'Schedule & Classes',
                icon: 'bi bi-book',
                title: 'Manage and organize classes'
            },
            {
                uri: '/Templates',
                label: 'Templates',
                icon: 'bi bi-arrow-repeat',
                title: 'Manage reusable vocabulary templates'
            },
        ],
        sharedAccess: [
            {
                uri: '/Classroom',
                label: 'Classroom',
                icon: 'bi bi-people',
                title: 'Access classroom-shared materials'
            },
            {
                uri: '/Dictionaries',
                label: 'My Space',
                icon: 'bi bi-box',
                title: 'Manage personal dictionaries and materials'
            },
        ],
    };

    return menuConfig[accessType] || [];
}
