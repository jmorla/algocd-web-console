# GEMINI.md - AlgoCD Webportal

## Project Overview
AlgoCD Webportal is a management dashboard and user interface for the AlgoCD platform. It is a modern full-stack application leveraging Spring Boot for the backend and a lightweight, high-performance frontend stack.

### Architecture & Tech Stack
- **Backend:** 
    - **Framework:** Spring Boot 4.0.6 (Java 25)
    - **Persistence:** MyBatis with PostgreSQL
    - **Migrations:** Liquibase
    - **Security:** Spring Security (Form-based login)
    - **Communication:** gRPC Client (via Spring gRPC)
    - **Infrastrucutre:** Kubernetes Client (fabric8)
    - **Performance:** Java Virtual Threads enabled
- **Frontend:**
    - **Templating:** Thymeleaf (Server-side rendering)
    - **Interactivity:** HTMX (AJAX without JavaScript) & Alpine.js (Client-side state)
    - **Styling:** Tailwind CSS 4
    - **Build Tool:** Vite (compiles assets to `src/main/resources/static/dist`)

---

## Getting Started

### Prerequisites
- Java 25
- Node.js (for frontend builds)
- Docker (for Testcontainers during tests)

### Commands

| Task | Command |
| :--- | :--- |
| **Run Application** | `./mvnw spring-boot:run` |
| **Frontend Watch** | `npm run watch` (Auto-rebuilds CSS/JS) |
| **Frontend Build** | `npm run build` |
| **Run Tests** | `./mvnw test` |
| **Full Package** | `npm run build && ./mvnw package` |

---

## Project Structure

- `src/main/java/com/algocd/webportal/`
    - `config/`: Security, MyBatis, and UUID type handlers.
    - `controllers/`: Web controllers (Dashboard, Terminals, Auth).
    - `domains/`: MyBatis entities.
    - `mappers/`: MyBatis mapper interfaces (XML/Annotation).
    - `services/`: Business logic.
    - `validation/`: Custom validators (Password match, Unique email).
- `src/main/frontend/`: Frontend source (Tailwind CSS, Alpine.js logic).
- `src/main/resources/`
    - `templates/`: Thymeleaf HTML templates.
    - `static/`: Static assets and the `dist/` folder for Vite output.
    - `db/changelog/`: Liquibase migration files (YAML).
    - `application.yaml`: Main configuration.

---

## Development Conventions

- **HTMX over SPA:** Prefer HTMX for dynamic content loading and partial page updates rather than building complex client-side components.
- **MyBatis:** Use MyBatis for SQL control. Complex queries should be mapped in XML if they exceed a few lines.
- **Database Migrations:** All schema changes must be done via Liquibase changelogs in `src/main/resources/db/changelog`.
- **Testing:** Use Testcontainers for integration tests involving the database. Ensure Docker is running.
- **Styling:** Use Tailwind 4 utility classes directly in Thymeleaf templates. Custom CSS goes in `src/main/frontend/style.css`.
