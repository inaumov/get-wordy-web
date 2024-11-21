import {createRouter, createWebHistory} from 'vue-router'
import MainView from "@/views/MainView.vue";
import WordSheetListView from "@/views/classes/WordSheetListView.vue";
import DictionariesView from "@/views/DictionariesView.vue";
import NotFoundView from "@/views/error/NotFoundView.vue";
import ClassListView from "@/views/ClassListView.vue";
import ManageClassView from "@/views/classes/ManageClassView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: MainView
        },
        {
            path: '/Dictionaries',
            name: 'dictionaries',
            component: DictionariesView
        },
        {
            path: '/ClassList',
            name: 'class-list',
            component: ClassListView
        },
        {
            path: '/Dictionaries/:dictionaryId/Cards',
            name: 'all-cards',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/CardsView.vue'),
            props: (route) => (
                {
                    dictionaryId: route.params.dictionaryId,
                    dictionaryName: route.query.dictionaryName
                }
            ),
        },
        {
            path: '/Dictionaries/:dictionaryId/PlayGame',
            name: 'play-game',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/PlayGameView.vue'),
            props: true
        },
        {
            path: '/Dictionaries/:dictionaryId/Generate',
            name: 'generate',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/GenerateCardsView.vue'),
            props: true
        },
        {
            path: '/Dictionaries/:dictionaryId/AddCard',
            name: 'add-card',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/AddCardView.vue'),
            props: true
        },
        {
            path: '/Dictionaries/:dictionaryId/Cards/:cardId',
            name: 'edit-card',
            component: () => import('@/views/EditCardView.vue'),
            props: (route) => (
                {
                    dictionaryId: route.params.dictionaryId,
                    cardId: route.params.cardId
                }
            ),
        },
        {
            path: '/Class',
            name: 'add-new-class',
            component: ManageClassView
        },
        {
            path: '/Class/:classId',
            name: 'class-wordsheet-list',
            component: WordSheetListView,
            props: (route) => (
                {
                    classId: route.params.classId
                }
            ),
        },
        {
            path: '/Class/:classId/Wordsheet/:wordsheetId',
            name: 'wordsheet',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/classes/WordSheetView.vue'),
            props: (route) => (
                {
                    classId: route.params.classId,
                    wordsheetId: route.params.wordsheetId
                }
            ),
        },
        {
            path: '/Class/:classId/Wordsheet/:wordsheetId/:word',
            name: 'edit-word',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/error/NotFoundView.vue'),
            props: (route) => (
                {
                    classId: route.params.classId,
                    wordsheetId: route.params.wordsheetId,
                    word: route.params.word
                }
            ),
        },
        {
            path: '/Settings',
            name: 'settings',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/SettingsView.vue')
        },
        {
            path: '/:catchAll(.*)',
            name: 'not-found',
            component: NotFoundView
        }
    ]
})

export default router
