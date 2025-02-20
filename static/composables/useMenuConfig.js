export function useMenuConfig(accessType) {
    const menuConfig = {
        teacherAccess: [
            {
                uri: '/Materials',
                label: 'Learning Materials',
                icon: 'bi bi-book',
                title: 'Manage and organize vocabularies'
            },
            {
                uri: '/Attendees',
                label: 'Class Attendees',
                icon: 'bi bi-people',
                title: 'Manage and invite people'
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
                uri: '/MyClasses',
                label: 'My Classes',
                icon: 'bi bi-people',
                title: 'Assigned classes'
            },
            {
                uri: '/Classroom',
                label: 'Learning Materials',
                icon: 'bi bi-list',
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
