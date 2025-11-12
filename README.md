# GYM 3000 – Conversión a Django + React

Este repositorio contiene la migración del proyecto original (Spring Boot + HTML/CSS) a una arquitectura basada en **Django REST Framework** y **React** con autenticación **JWT** y peticiones autenticadas mediante `fetch`.

## Estructura principal

```
GYM_Entornos_de_programion/
├── gym_fullstack/
│   ├── backend/   # API Django + DRF + MongoDB
│   ├── frontend/  # Interfaz React (Vite)
│   └── database/  # Recursos para levantar MongoDB y sembrar datos
├── FronEnd_Gym/      # Frontend original (solo referencia)
└── Gimnasio_Copia2/  # Backend original en Spring Boot (solo referencia)
```

## Backend (Django REST)

- API REST construida con Django 4.2, Django REST Framework y JWT (`djangorestframework-simplejwt`).
- Base de datos **NoSQL** (MongoDB) utilizando `djongo`.
- Endpoints compatibles con el frontend original (`/usuario/list`, `/membresia/list`, etc.).
- Roles de usuario (Administrador, Entrenador, Cliente) y gestión de perfiles.
- Gestión completa de usuarios, membresías, asignaciones y pagos.

Consulta la documentación detallada en [`gym_fullstack/backend/README.md`](gym_fullstack/backend/README.md).

## Frontend (React + Vite)

- Pantalla de inicio de sesión con manejo de tokens JWT.
- Panel administrativo para CRUD de usuarios, membresías, asignaciones y pagos.
- Panel para clientes con edición de perfil, seguimiento de membresías y pagos.
- Estilos modernos y responsivos inspirados en el diseño original.

Más información en [`gym_fullstack/frontend/README.md`](gym_fullstack/frontend/README.md).

## Base de datos (MongoDB)

Dentro de [`gym_fullstack/database`](gym_fullstack/database/README.md) encontrarás:

- `docker-compose.yml` para levantar MongoDB mediante Docker.
- Un script de semilla (`seed/seed_data.py`) que crea usuarios, membresías, asignaciones y pagos de ejemplo.

Sigue los pasos descritos en su README para iniciar la base de datos y poblarla antes de ejecutar el backend.

## Cómo ejecutar

1. **Base de datos**
   - Levanta MongoDB con Docker (`docker compose up -d` desde `gym_fullstack/database`) o usa tu propia instancia.
   - (Opcional) Pobla datos de ejemplo con `python manage.py shell < ../database/seed/seed_data.py` desde la carpeta del backend.

2. **Backend**
   - Crear y activar un entorno virtual.
   - Instalar dependencias (`pip install -r backend/requirements.txt`).
   - Configurar variables de entorno (URI de MongoDB, claves, etc.).
   - Ejecutar migraciones y levantar el servidor con `python manage.py runserver`.

3. **Frontend**
   - Instalar dependencias con `npm install`.
   - Ejecutar `npm run dev` y acceder a `http://localhost:5173`.

Asegúrate de que el backend esté disponible en `http://localhost:8000/api` o ajusta `VITE_API_BASE` en el frontend.

---

> Los directorios originales se conservan únicamente como referencia histórica y no se modificaron durante la migración.
