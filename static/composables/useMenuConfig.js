export const useMenuConfig = {
    teacherAccess() {
        return [
            {
                uri: '/Classes',
                label: 'Classes',
                icon: 'bi bi-people',
                title: 'Manage classes, participants, and organize vocabularies'
            },
            {
                uri: '/Library',
                label: 'Library',
                icon: 'bi bi-arrow-repeat',
                title: 'Manage prepared vocabularies and search by themes'
            },
        ];
    },

    sharedAccess() {
        const defaultMenuItems = this.default();

        return [
            {
                uri: '/User/MyClasses',
                label: 'My Classes',
                icon: 'bi bi-people',
                title: 'Assigned classes'
            },
            ...defaultMenuItems
        ];
    },

    default() {
        const defaultMenuItem = {
            uri: '/User/Vocabularies',
            label: 'Vocabularies',
            icon: 'bi bi-box',
            title: 'Manage personal and shared vocabularies'
        };
        return [
            defaultMenuItem
        ]
    }
}
