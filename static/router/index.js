import {createRouter, createWebHistory} from 'vue-router'
import LandingPage from "@/pages/LandingPage.vue";
import MainView from "@/views/MainView.vue";
import DictionariesView from "@/views/DictionariesView.vue";
import ClassListView from "@/views/ClassListView.vue";
import SharedMaterials from "@/views/shared/SharedMaterials.vue";
import ClassDetailsView from "@/views/classes/ClassDetailsView.vue";
import MyClasses from "@/views/shared/MyClasses.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: LandingPage,
        },
        {
            path: "/Dashboard",
            component: MainView,
            children: [
        // components rendered inside Dashboard
        {
            path: '/Dictionaries',
            name: 'dictionaries',
            component: DictionariesView
        },
        {
            path: '/Classroom/:classId',
            name: 'classroom',
            component: SharedMaterials,
            props: (route) => (
                {
                    classId: route.params.classId,
                }
            ),
        },
        {
            path: '/MyClasses',
            name: 'my-classes',
            component: MyClasses
        },
        {
            path: '/Classes',
            name: 'day-classes',
            component: ClassListView
        },
        {
            path: '/Classes/:classId',
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
            component: () => import('@/views/classes/TemplatesView.vue'),
        },
        {
            path: '/Templates/:templateId',
            name: 'template-preview',
            component: () => import('@/views/classes/Template.vue'),
            props: (route) => (
                {
                    templateId: route.params.templateId
                }
            ),
        },
        {
            path: '/Templates/:templateId/Words/:wordId',
            name: 'edit-explanation',
            component: () => import('@/views/EditCardView.vue'),
            props: (route) => (
                {
                    vocabId: route.params.templateId,
                    wordId: route.params.wordId
                }
            ),
        },
        {
            path: '/Dictionaries/:dictionaryId/Cards',
            name: 'all-cards',
            component: () => import('@/views/CardsView.vue'),
            props: (route) => (
                {
                    dictionaryId: route.params.dictionaryId,
                    dictionaryName: route.query.dictionaryName
                }
            ),
        },
        {
            path: '/Dictionaries/:dictionaryId/Exercise',
            name: 'play-game',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/PlayGameView.vue'),
            props: true
        },
        {
            path: '/Dictionaries/:dictionaryId/Search',
            name: 'generate',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/GenerateCardsView.vue'),
            props: true
        },
        {
            path: '/Classes',
            name: 'add-new-class',
            component: () => import('@/views/classes/ManageClassView.vue')
        },
        {
            path: '/Classes/:classId/Vocabularies/:vocabId',
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
                    path: '/Classes/:classId/Vocabularies/:vocabId/Explanation',
                    name: 'add-explanation',
                    component: () => import('@/views/AddCardView.vue'),
                    props: true
                },
                {
                    path: '/Classes/:classId/Vocabularies/:vocabId/Explanation/:wordId',
                    name: 'edit-explanation',
                    component: () => import('@/views/EditCardView.vue'),
                    props: (route) => (
                        {
                            vocabId: route.params.vocabId,
                            wordId: route.params.wordId
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
        // finish children components declaration
            ],
        },
        {
            path: '/:catchAll(.*)',
            name: 'not-found',
            component: () => import('@/pages/404Page.vue')
        }
    ]
})

export default router
