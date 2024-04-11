FROM node:18.18.2-alpine as builder

RUN npm install -g pnpm@8.14.3

WORKDIR /app

COPY package.json pnpm-lock.yaml ./

RUN pnpm install --frozen-lockfile

COPY . .

RUN pnpm format:check
RUN pnpm lint

RUN pnpm build

CMD ["pnpm", "preview", "--host", "0.0.0.0", "--port", "3000"]
