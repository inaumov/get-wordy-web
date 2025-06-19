import {createRouter, createWebHistory} from 'vue-router'
import {hasPermissions} from '@/js/auth-check.js';
import LandingPage from "@/pages/LandingPage.vue";
import MainView from "@/views/MainView.vue";
import DictionariesView from "@/views/AllVocabularies.vue";
import ClassListView from "@/views/ClassListView.vue";
import ClassDashboard from "@/views/classes/ClassDashboard.vue";
import MyClasses from "@/views/shared/MyClasses.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    // Public Routes
    routes: [
        {
            path: '/',
            name: 'home',
            component: LandingPage, // Public landing page
        },
        // Protected Routes (authenticated layout)
        {
            path: "/Dashboard",
            component: MainView, // Wrapper for sidebar + main content
            beforeEnter: (to, from, next) => {
                if (hasPermissions()) {
                    next(); // allow access
                } else {
                    next("/login"); // redirect if not logged in
                }
            },
            children: [
        // components rendered inside Dashboard
        {
            path: '/User/Vocabularies',
            name: 'user-vocabularies',
            component: DictionariesView
        },
        {
            path: '/User/MyClasses',
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
            name: 'class-dashboard',
            component: ClassDashboard,
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
            path: '/User/Vocabularies/:vocabId/Cards',
            name: 'all-cards',
            component: () => import('@/views/CardsView.vue'),
            props: (route) => (
                {
                    vocabId: route.params.vocabId,
                    vocabName: route.query.vocabName
                }
            ),
        },
        {
            path: '/User/Vocabularies/:vocabId/Exercise',
            name: 'play-game',
            component: () => import('@/views/PlayGameView.vue'),
            props: (route) => (
                {
                    vocabId: route.params.vocabId,
                    vocabName: route.query.vocabName
                }
            ),
        },
        {
            path: '/Classes',
            name: 'add-new-class',
            component: () => import('@/views/classes/AddClass.vue')
        },
        {
            path: '/Classes/:classId/Vocabularies/:vocabId',
            name: 'vocabulary',
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
            path: '/User/Classes/:classId/Vocabularies/:vocabId',
            name: 'vocabulary-preview',
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
            component: () => import('@/pages/404Page.vue') // Global 404
        }
    ]
})

export default router
