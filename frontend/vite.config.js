import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],

  //change build output path to Spring Boot resources directory
  build: {
    outDir: "../src/main/resources/static"
  },

  //set proxy to run dev server
  server: {
    proxy: {
      "/api": {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
