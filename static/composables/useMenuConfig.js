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
                uri: '/Templates',
                label: 'Templates',
                icon: 'bi bi-arrow-repeat',
                title: 'Manage reusable vocabulary templates and search by themes'
            },
        ];
    },

    sharedAccess(activeClass) {
        const defaultMenuItem = {
            uri: '/Dictionaries',
            label: 'My Space',
            icon: 'bi bi-box',
            title: 'Manage personal dictionaries and materials'
        };

        if (!activeClass) {
            return [defaultMenuItem];
        }

        return [
            {
                uri: '/MyClasses',
                label: 'My Classes',
                icon: 'bi bi-people',
                title: 'Assigned classes'
            },
            {
                uri: `/Classroom/${activeClass.classId}`,
                label: 'Vocabularies',
                icon: 'bi bi-list',
                title: 'Access classroom-shared vocabularies'
            },
            defaultMenuItem
        ];
    }
}
