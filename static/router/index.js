import {createRouter, createWebHistory} from 'vue-router'
import DictionariesView from "@/views/DictionariesView.vue";
import NotFoundView from "@/views/error/NotFoundView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: DictionariesView, NotFoundView
        },
        {
            path: '/Dictionaries/:dictionaryId/Cards',
            name: 'all-cards',
            // route level code-splitting
            // this generates a separate chunk (Settings.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/CardsView.vue'),
            props: true
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
