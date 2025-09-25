## README

### Project info
- backend api sourcetree in `src/main/java`
- frontend sourcetree in `src/main/frontend`

### Development config
- run backend with spring profile `local`
- backend api runs on port 8080
- frontend dev server runs on port 3000

when changing ports, remember to change the proxy configuration in `vite.config.ts`.

### Production config
- run api with spring profile `prod`
- provide the following environment variables
  - DB_URL 
  - DB_USER
  - DB_PASSWORD

### TODO
- add frontend tests
- zod validation errors i18n
    - security
        - make distinction between reader and writer users
- event
    - crud
    - crud courses
- dish
    - course 1 <-> 1 dish to course 1 <-> * dish
    - data table
    - image upload
    - refactor dish <-> course onetoone relation to manytoone
- wine
    - add maker property
