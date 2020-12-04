# Build
FROM node:14 as build-deps
WORKDIR /app
COPY package*.json /app/
RUN npm install --production
COPY ./ /app/
RUN npm run build

# Serve
FROM nginx:1.19-alpine
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
# Copy static assets
COPY --from=build-deps /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]