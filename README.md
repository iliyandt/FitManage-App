### FitManage — Backend README

FitManage is a Spring Boot REST API for gym/club management: members, memberships, payments (Stripe), schedules, access control, and more. It uses JWT authentication and exposes OpenAPI/Swagger docs.

---

### Key Features
- Auth: sign up/in, refresh tokens, password reset via email
- Multi-tenant concepts (e.g., `TenantController`)
- Members, membership plans, subscriptions
- Payments: Stripe Checkout + webhooks
- Stats/demographics, shifts, news, QR access
- Centralized error handling via `GlobalControllerAdvice`

---

### Tech Stack
- Java `17`, Spring Boot `3.4.x` (Web, Security, Data JPA, Validation, Mail, Actuator)
- JPA/Hibernate
- JWT (`io.jsonwebtoken:jjwt-*`)
- OpenFeign (`@EnableFeignClients`)
- OpenAPI/Swagger (`springdoc-openapi`)
- Logback + Logstash encoder

Note: `pom.xml` has MySQL driver; sample `.env` uses PostgreSQL URL. Align driver and URL accordingly.

---

### Requirements
- Java 17+, Maven 3.9+
- Database (PostgreSQL or MySQL)
- Stripe account/keys (for payments) / follow the instruction from **payment-service** README.md

---

### Environment Variables (examples)
- DB: `JDBC_DATABASE_URL`, `JDBC_DATABASE_USERNAME`, `JDBC_DATABASE_PASSWORD`
- Profile: `SPRING_PROFILES_ACTIVE`
- JWT: `JWT_SECRET_KEY`
- Email: `SUPPORT_EMAIL`, `APP_PASSWORD`
- Superadmin (if used): `SUPERADMIN_EMAIL`, `SUPERADMIN_USERNAME`, `SUPERADMIN_PASSWORD`
- Stripe: `STRIPE_API_KEY`, `STRIPE_WEBHOOK_SECRET`

Do not commit real secrets. Use separate values per `local`/`staging`/`prod`.

---

### Local Setup
1) Clone
```
git clone <repo-url>
cd FitManage
```
2) Export environment variables (see above)
3) Run
```
mvn spring-boot:run
# or
mvn -DskipTests package && java -jar target/FitManage-0.0.1-SNAPSHOT.jar
```
Default: `http://localhost:8080`.

---

### Security & CORS
- Stateless JWT with `JwtAuthenticationFilter`
- Public paths: `/api/v1/auth/**`, `/api/v1/tenants/lookup`, `/api/v1/access-requests`, Swagger: `/swagger-ui/**`, `/v3/api-docs/**`, plus `/internal/**`
- Allowed origins (see `SecurityConfiguration.corsConfigurationSource()`): `http://localhost:5173`, `https://dam-il.netlify.app`, `exp://192.168.0.132:8081`, `https://damilsoft.com`

---

### API Docs
- Swagger UI: `http://localhost:8080/swagger-ui.html` or `/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs` (JSON), `/v3/api-docs.yaml` (YAML)

---

### Payments (Stripe)
- Checkout via `StripeController` (e.g., `POST /api/v1/stripe/checkout` with `CheckoutRequest`)
- Configure webhooks to `https://<host>/api/v1/stripe/webhook` and set `STRIPE_WEBHOOK_SECRET`
- follow the instruction from **payment-service** README.md for local testing

---

### Troubleshooting
- 401/403: verify `Authorization: Bearer <token>` and CORS origin
- Stripe webhooks: correct public URL and `STRIPE_WEBHOOK_SECRET`
- DB errors: ensure driver matches URL (PostgreSQL vs MySQL)