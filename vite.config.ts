import { defineConfig } from 'vite'
import tailwindcss from '@tailwindcss/vite'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    tailwindcss(),
  ],
  build: {
    // Output to a 'dist' subdirectory to separate generated files
    outDir: resolve(__dirname, 'src/main/resources/static/dist'),
    emptyOutDir: true,
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'src/main/frontend/main.js'),
      },
      output: {
        entryFileNames: `[name].js`,
        chunkFileNames: `[name].js`,
        assetFileNames: `[name].[ext]`
      }
    }
  },
  // Disable publicDir copying since static assets are already in the target static folder
  publicDir: false,
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src/main/frontend'),
    }
  }
})
