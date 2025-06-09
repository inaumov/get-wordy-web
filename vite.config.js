import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./static', import.meta.url))
    }
  },
  server: {
    proxy: {
      // Login flow: /users/auth/status → /status
      '^/users/auth/status': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: () => '/status'
      },

      // Login flow: /users/permissions → /permissions
      '^/users/meta': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: () => '/meta'
      },

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
          return `/vocabularies`; // Note: frontend must send classId in body/query if POST
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

      // GET/POST explanations for a vocabulary (list or create)
      '^/api/v1/vocabularies/([^/]+)/explanations/?$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: (path) => {
          // This will route to /explanations — must include vocabularyId in query (for GET) or body (for POST)
          return `/explanations`;
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

      // Base: /api/v1/templates → /templates
      '^/api/v1/templates$': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        rewrite: path => '/templates'
      },

    }
  }
})
