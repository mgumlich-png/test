# GKS Operator MVP

Produktionsnahes MVP einer mandantenfähigen Großkundenschnittstelle (GKS) nach i-Kfz/KBA-Modell. Das Monorepo enthält ein Spring Boot Backend, ein React-Frontend sowie Infrastruktur für Postgres und MinIO.

## Architekturüberblick
- **Backend (Java 17, Spring Boot)**: REST-API mit JWT-Authentifizierung, Rollenmodell (TENANT_ADMIN, CASE_WORKER, PARTNER_ADMIN, PARTNER_USER), Entities/Repositories/Services/Controller für Mandanten, Partner, Vorgänge, Dokumente und AuditEvents. Dokumente werden versioniert gespeichert und per Hash gesichert (lokaler Storage, S3-kompatible Backends können später angeschlossen werden). OpenAPI ist via `/swagger-ui` verfügbar.
- **Frontend (React + TypeScript)**: Einfache Operator-Oberfläche mit Login, Dashboard, Vorgangsliste, Vorgangsdetail inkl. Dokumenten-Upload.
- **Datenbank (PostgreSQL)**: Primärer Persistenzspeicher.
- **MinIO (S3-kompatibel)**: Platzhalter für spätere Integration eines objektbasierten Dokumentenspeichers.
- **Containerisierung**: Dockerfiles für Backend/Frontend, `docker-compose` zum lokalen Hochfahren aller Abhängigkeiten.

Die Architektur trennt bewusst Kern-API und spätere KBA/GKS-Konnektoren. AuditEvents werden bei allen wichtigen Änderungen erzeugt.

## Projektstruktur
```
backend/        # Spring Boot API
frontend/       # React UI (Vite)
import-templates/ # Beispieltemplates (CSV, XLSX)
docker-compose.yml
```

## Setup & Start
### Voraussetzungen
- Docker & Docker Compose
- (Optional) Java 17 und Maven für lokalen Backend-Start
- (Optional) Node 20+ für lokale Frontend-Entwicklung

### Schnellstart mit Docker Compose
```bash
docker compose build
docker compose up
```
- Backend: http://localhost:8080 (Swagger UI unter `/swagger-ui`)
- Frontend: http://localhost:3000
- MinIO-Konsole: http://localhost:9001 (minio/minio123)

Standard-Admin: `admin@gk.local` / `admin`

### Lokaler Backend-Start
```bash
cd backend
mvn spring-boot:run
```

### Lokaler Frontend-Start
```bash
cd frontend
npm install
npm run dev
```
Hinweis: In restriktiven Umgebungen kann das Installieren von npm-Paketen fehlschlagen. In CI/Produktivumgebungen sollte ein vorbereiteter Cache oder privater Registry genutzt werden.

## Domänenmodell (Auszug)
- **Tenant**: Mandant des Betreibers, CRUD nur für TENANT_ADMIN.
- **Partner**: Vertragspartner eines Mandanten.
- **UserAccount**: Nutzer mit Rollen & optionaler Partnerbindung.
- **CaseRecord**: Vorgänge (Abmeldung, Neuzulassung, Ummeldung) mit Status-Lifecycle von Draft bis Done/Failed.
- **DocumentRecord**: Versionierte Dokumente mit Hash, pro Vorgang abrufbar/exportierbar.
- **AuditEvent**: Revisionssichere Änderungen (Wer/Was/Wann).

## Import-Templates
Beispiele liegen unter `import-templates/`:
- `cases.csv` – einfache CSV-Struktur für Draft-Imports.
- `cases.xlsx` – äquivalente Excel-Vorlage (Sheet `cases`).

## Erweiterbarkeit
- Der Dokumentenspeicher nutzt aktuell lokalen Storage; eine S3-Implementierung kann über den `FileStorageService` ergänzt werden.
- Validierungsregeln und KBA-spezifische Prüfdienste lassen sich im `CaseService` erweitern.
- Separater GKS/SOAP-Connector kann als eigener Service ergänzt werden, während das API stabil bleibt.

## Sicherheit & Audit
- JWT-Authentifizierung mit konfigurierbarem Secret (`security.jwt.secret`).
- Rollenbasierte Zugriffskontrolle über `@PreAuthorize`.
- AuditEvents werden bei CRUD/Status-Änderungen und Dokument-Uploads geschrieben.
- Mandanten- und Partner-Sicht werden serverseitig erzwungen (Partner sehen nur eigene Vorgänge/Dokumente; Tenant-Admins nur ihren Mandanten). Vor dem Statuswechsel auf `SENT` muss mindestens ein Dokument am Vorgang hinterlegt sein.
