# Base de datos MongoDB

Esta carpeta contiene lo necesario para levantar la base de datos **MongoDB** requerida por el backend de Django y poblarla con datos de ejemplo.

## 1. Levantar MongoDB con Docker

Si cuentas con Docker, el método más rápido es utilizar el archivo `docker-compose.yml` incluido aquí.

```bash
cd gym_fullstack/database
docker compose up -d
```

Esto creará un contenedor `gym-mongo` exponiendo el puerto `27017`. Los datos se almacenan en la carpeta `data/` del host (creada automáticamente) para que persistan entre reinicios.

> Si no usas Docker, instala MongoDB en tu sistema y asegúrate de que escuche en `mongodb://localhost:27017` o actualiza la variable `MONGO_DB_URI` en el backend.

## 2. Variables de entorno del backend

En el backend, configura tu archivo `.env` (o exporta variables) para apuntar a la misma instancia de MongoDB:

```env
DJANGO_SECRET_KEY=cambia-esta-clave
MONGO_DB_URI=mongodb://localhost:27017
MONGO_DB_NAME=gymdb
DJANGO_DEBUG=True
CORS_ALLOWED_ORIGINS=http://localhost:5173
CSRF_TRUSTED_ORIGINS=http://localhost:5173
```

## 3. Poblar datos de ejemplo

Después de levantar el backend por primera vez (ya sea que ejecutes las migraciones o simplemente `python manage.py migrate`), puedes cargar información de ejemplo ejecutando:

```bash
cd gym_fullstack/backend
source .venv/bin/activate  # o el entorno virtual que creaste
python manage.py shell < ../database/seed/seed_data.py
```

El script creará:

- Un superusuario `admin@gym.com` (contraseña `Admin123!`).
- Un entrenador `coach@gym.com` (contraseña `Coach123!`).
- Un cliente `cliente@gym.com` (contraseña `Cliente123!`).
- Membresías de prueba, asignaciones y pagos asociados.

Puedes ejecutar el script las veces que quieras; detecta si los registros ya existen para evitar duplicados.

## 4. Verificar conexión

Con el backend en ejecución (`python manage.py runserver`) y MongoDB levantado, deberías poder consultar los endpoints en `http://localhost:8000/api/`. El frontend en React se conecta automáticamente usando `VITE_API_BASE` (por defecto `http://localhost:8000/api`).

---

Para limpiar los contenedores creados con Docker Compose:

```bash
cd gym_fullstack/database
docker compose down
```

Si quieres eliminar los datos persistentes, borra la carpeta `data/` después de bajar los contenedores.
