import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig(({mode}) => {
  const isSpring = mode === 'spring'

  return {
    plugins: [
      vue()
    ],
    base: isSpring ? '/' : './',
    build: {
      outDir: isSpring
          ? path.resolve(__dirname, 'src/main/resources/static') // Spring Boot static folder
          : path.resolve(__dirname, 'dist'), // default Vite standalone build
      emptyOutDir: true,
    },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./static', import.meta.url))
    }
  },
  server: {
    proxy: {
      // Login flow: /users/auth/status → /status
      '^/users/auth/status$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: () => '/status'
      },

      // Login flow: /users/permissions → /permissions
      '^/users/meta$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: () => '/meta'
      },

      // Login flow: /users/settings → /settings
      '^/users/settings$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: () => '/settings'
      },

      // User flow: /users/my-classes → /my-classes
      '^/users/my-classes$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: () => '/my-classes'
      },

      // User flow: /user/my-vocabularies → /my-vocabularies
      '^/api/v1/user/my-vocabularies$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: () => '/my-vocabularies'
      },
      // User flow: GET /user/my-vocabularies/:id → /my-vocabularies/:vocabId
      '^/api/v1/user/my-vocabularies/([^/]+)$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => {
          const vocabId = path.match(/\/my-vocabularies\/([^/]+)$/)?.[1];
          return `/my-vocabularies/${vocabId}`;
        }
      },

      // User flow: /user/my-classes/:classId/vocabularies → /vocabularies
      '^/api/v1/user/my-classes/([^/]+)/vocabularies$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => {
          const classId = path.match(/\/my-classes\/([^/]+)\/vocabularies/)?.[1];
          return `/vocabularies?classId=${classId}`; // Note: frontend must send classId in body/query if POST
        }
      },
      // GET/PUT/DELETE single vocabulary → /vocabularies/:vocabId
      '^/api/v1/user/my-classes/[^/]+/vocabularies/([^/]+)$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => {
          const vocabId = path.match(/\/vocabularies\/([^/]+)$/)?.[1];
          return `/vocabularies/${vocabId}`;
        }
      },


      // ... class & vocabulary rewrites start ...


      // Base: /api/v1/classes → /classes
      '^/api/v1/classes$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => '/classes'
      },

      // Single class info → /classes/:id
      '^/api/v1/classes/([^/]+)$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/v1/, '')
      },

      // GET/POST vocabularies for class → rewrite to /vocabularies or /vocabularies?classId=xxx
      '^/api/v1/classes/([^/]+)/vocabularies/?$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => {
          const classId = path.match(/\/classes\/([^/]+)\/vocabularies/)?.[1];
          return '/vocabularies'; // Note: frontend must send classId in body/query if POST
        }
      },

      // GET/PUT/DELETE single vocabulary → /vocabularies/:vocabId
      '^/api/v1/classes/[^/]+/vocabularies/([^/]+)$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => {
          const vocabId = path.match(/\/vocabularies\/([^/]+)$/)?.[1];
          return `/vocabularies/${vocabId}`;
        }
      },


      // ... class & vocabulary rewrites end ...


      // GET/POST explanations for vocabulary (list or create)
      '^/api/v1/vocabularies/([^/]+)/explanations/?$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: (path) => {
          // This will route to /explanations — must include vocabularyId in a query (for GET) or body (for POST)
          return '/explanations';
        }
      },

      // GET/PUT/DELETE a specific explanation
      '^/api/v1/vocabularies/[^/]+/explanations/([^/]+)$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: (path) => {
          const match = path.match(/explanations\/([^/]+)$/);
          const explanationId = match?.[1];
          return `/explanations/${explanationId}`;
        }
      },

      // User flow: /user/my-vocabularies/:vocabId/cards → /cards
      '^/api/v1/user/my-vocabularies/([^/]+)/cards/?$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => {
          const vocabId = path.match(/\/my-vocabularies\/([^/]+)\/cards/)?.[1];
          return '/cards'; // Note: frontend must send vocabId in body/query if POST
        }
      },

      // User flow: /user/my-vocabularies/:vocabId/exercise → /exercise
      '^/api/v1/user/my-vocabularies/([^/]+)/exercise$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => {
          const vocabId = path.match(/\/my-vocabularies\/([^/]+)\/exercise/)?.[1];
          return `/exercise?vocabId=${vocabId}`;
        }
      },

      // Base: /api/v1/themes → /themes
      '^/api/v1/themes$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/v1/, '')
      },

      // Single theme info → /themes/:id
      '^/api/v1/themes/([^/]+)$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/v1/, '')
      },


      // ... search words & themes rewrites start ...


      // Search: /api/v1/words → /search
      '^/api/v1/words\\?input=': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path =>
            '/search' + path.substring(path.indexOf('?'))
      },

      // Search: /api/v1/themes → /themes-search
      '^/api/v1/themes\\?input=': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path =>
            '/themes-search' + path.substring(path.indexOf('?'))
      }

      // ... search words & themes rewrites end ...

    }
  }
}})
