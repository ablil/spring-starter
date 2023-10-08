import {defineConfig} from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
const devConfig = defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            "/auth": "http://0.0.0.0:8080"
        }
    }
});

const prodConfig = defineConfig({
    plugins: [react()],
    server: {
        port: 80
    },
});


export default process.env.NODE_ENV === 'production' ? prodConfig : devConfig