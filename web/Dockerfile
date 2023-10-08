FROM node:20-alpine as build
WORKDIR /app
COPY package.json .
RUN yarn

COPY . .
RUN yarn build

FROM nginx:latest
COPY --from=build /app/dist/ /usr/share/nginx/html
COPY --from=build /app/nginx.conf /etc/nginx/conf.d/default.conf