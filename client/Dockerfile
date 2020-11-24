# Build
FROM node:14 as build-deps
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install --production
COPY . .

# Serve
FROM nginx:1.19-alpine
# Default nginx settings
COPY --from=build-deps /usr/src/app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]