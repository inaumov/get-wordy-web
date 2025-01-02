import {createRouter, createWebHistory} from 'vue-router'
import MainView from "@/views/MainView.vue";
import ClassVocabularies from "@/views/classes/ClassVocabularies.vue";
import DictionariesView from "@/views/DictionariesView.vue";
import NotFoundView from "@/views/error/NotFoundView.vue";
import ClassListView from "@/views/ClassListView.vue";
import ManageClassView from "@/views/classes/ManageClassView.vue";
import SharedMaterials from "@/views/shared/SharedMaterials.vue";
import ScheduleView from "@/views/classes/ScheduleView.vue";
import ClassDetailsView from "@/views/classes/ClassDetailsView.vue";
import TemplatesView from "@/views/classes/TemplatesView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: MainView
        },
        {
            path: '/Schedule',
            name: 'schedule',
            component: ScheduleView,
        },
        {
            path: '/Dictionaries',
            name: 'dictionaries',
            component: DictionariesView
        },
        {
            path: '/Classroom',
            name: 'classroom',
            component: SharedMaterials
        },
        {
            path: '/Schedule/:day',
            name: 'classes',
            component: ClassListView,
            props: true,
        },
        {
            path: '/Classes/:day/:classId',
            name: 'class-details',
            component: ClassDetailsView,
            props: (route) => (
                {
                    classId: route.params.classId,
                }
            ),
        },
        {
            path: '/Templates',
            name: 'templates',
            component: TemplatesView
        },
        {
            path: '/Templates/:templateId',
            name: 'template-preview',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/shared/WordsPreview.vue'),
            props: (route) => (
                {
                    classId: route.params.classId,
                    vocabId: route.params.templateId
                }
            ),
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
            path: '/Classes/:classId/Vocabularies',
            name: 'class-vocabularies',
            component: ClassVocabularies,
            props: (route) => (
                {
                    classId: route.params.classId
                }
            ),
        },
        {
            path: '/Classes/:classId/Vocabulary/:vocabId',
            name: 'vocabulary',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/classes/Vocabulary.vue'),
            props: (route) => (
                {
                    classId: route.params.classId,
                    vocabId: route.params.vocabId
                }
            ),
        },
        {
            path: '/Materials/:classId/Vocabularies/:vocabId',
            name: 'vocabulary-preview',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/shared/WordsPreview.vue'),
            props: (route) => (
                {
                    classId: route.params.classId,
                    vocabId: route.params.vocabId
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
