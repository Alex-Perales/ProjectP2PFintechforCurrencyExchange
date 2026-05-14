# Integración de APIs de Tasas de Cambio

## Evaluación: ¿Se puede hacer en tu proyecto?

**Respuesta: SÍ, completamente viable.** Tu arquitectura ya soporta esto:

| Componente | Por qué funciona |
|---|---|
| **Redis** | Ya existe en `docker-compose.yml` — ideal para cachear tasas cada 1-5 minutos |
| **Celery** | Tareas asíncronas para actualizar tasas en background |
| **Modelo `exchange_rate`** | Ya existe en la estructura — solo necesita conexión a API externa |
| **Flask Service Layer** | Dónde implementar la lógica de obtención y caché |
| **PostgreSQL** | Almacenar histórico de tasas para auditoría y análisis |

---

## Opciones de APIs — Análisis para tu caso

### 1. **ExchangeRate.host** ⭐ (MVP)

**Recomendación:** Comienza aquí.

```python
# Ventajas
- Gratis, sin API key
- Rápida y confiable
- Muchas monedas (200+)
- Simple JSON

# Desventajas
- Menos precisa que Open Exchange Rates
- No apta para producción seria (fintech)

# Tu caso
PERFECTO PARA MVP porque:
- Bajas a cero costos iniciales
- Pruebas y validación del concepto
- Suficientemente exacta para transacciones P2P
```

**Endpoint:**
```
GET https://api.exchangerate.host/latest?base=USD&symbols=PEN
```

---

### 2. **Open Exchange Rates** (Producción)

**Recomendación:** Migra aquí cuando estés en producción.

```python
# Ventajas
- Muy precisa (datos interbancarios)
- 200+ monedas
- Histórico de tasas
- Excelente documentación
- Estable para fintech
- SLA garantizado

# Desventajas
- Plan Starter: ~$12/mes (250 requests/día)
- Plan Pro: ~$99/mes (unlimited)

# Tu caso
RECOMENDADO PARA PRODUCCIÓN porque:
- Tasas interbancarias reales
- Confiable para dinero real
- Bajo costo relativo vs. transacciones
```

---

### 3. **Fixer.io** (Alternativa)

```python
# Similar a Open Exchange Rates
- Precisa pero con datos ECB
- Buen soporte para EUR
- Precio similar
```

---

## Arquitectura de Implementación

### Estructura propuesta en `app/services/`

```
app/services/
├── exchange_rate_service.py       # Lógica de obtención y caché
├── providers/
│   ├── __init__.py
│   ├── exchange_rate_provider.py  # Clase abstracta
│   ├── exchangerate_host.py       # Proveedor 1: ExchangeRate.host
│   └── open_exchange_rates.py     # Proveedor 2: Open Exchange Rates
└── tasks/
    └── update_exchange_rates.py   # Celery task — actualización background
```

---

## Implementación Paso a Paso

### Paso 1: Servicio de tasas de cambio

```python
# app/services/exchange_rate_service.py

from datetime import datetime, timedelta
from typing import Dict, Optional
from flask import current_app
from redis import Redis
import requests
import json

class ExchangeRateService:
    CACHE_TTL = 300  # 5 minutos
    CACHE_KEY = "exchange_rates"
    
    def __init__(self, redis_client: Redis):
        self.redis = redis_client
    
    def get_rate(self, base: str, target: str) -> Optional[float]:
        """Obtiene tasa de cambio con caché."""
        
        # 1. Intentar obtener del caché
        cached = self._get_cached_rates()
        if cached and base in cached and target in cached[base]:
            return cached[base][target]
        
        # 2. Si no está en caché, obtener de API externa
        rate = self._fetch_from_api(base, target)
        
        # 3. Cachear resultado
        if rate:
            self._cache_rates(rate, base)
        
        return rate
    
    def _get_cached_rates(self) -> Optional[Dict]:
        """Obtiene tasas del caché de Redis."""
        cached = self.redis.get(self.CACHE_KEY)
        return json.loads(cached) if cached else None
    
    def _fetch_from_api(self, base: str, target: str) -> Optional[float]:
        """Obtiene tasa de una API externa."""
        
        provider = current_app.config.get('EXCHANGE_RATE_PROVIDER', 'exchangerate_host')
        
        if provider == 'exchangerate_host':
            return self._fetch_exchangerate_host(base, target)
        elif provider == 'open_exchange_rates':
            return self._fetch_open_exchange_rates(base, target)
    
    def _fetch_exchangerate_host(self, base: str, target: str) -> Optional[float]:
        """Proveedor: ExchangeRate.host (gratis, sin API key)."""
        try:
            url = f"https://api.exchangerate.host/latest"
            params = {"base": base, "symbols": target}
            response = requests.get(url, params=params, timeout=5)
            
            if response.status_code == 200:
                data = response.json()
                return data.get('rates', {}).get(target)
        except Exception as e:
            current_app.logger.error(f"ExchangeRate.host error: {e}")
        
        return None
    
    def _fetch_open_exchange_rates(self, base: str, target: str) -> Optional[float]:
        """Proveedor: Open Exchange Rates (API key requerida)."""
        try:
            api_key = current_app.config.get('OPEN_EXCHANGE_RATES_KEY')
            url = f"https://api.exchangerate.host/latest"
            params = {
                "app_id": api_key,
                "base": base,
                "symbols": target
            }
            response = requests.get(url, params=params, timeout=5)
            
            if response.status_code == 200:
                data = response.json()
                return data.get('rates', {}).get(target)
        except Exception as e:
            current_app.logger.error(f"Open Exchange Rates error: {e}")
        
        return None
    
    def _cache_rates(self, rate: float, base: str, ttl: int = CACHE_TTL):
        """Cachea tasas en Redis."""
        rates = self._get_cached_rates() or {}
        
        if base not in rates:
            rates[base] = {}
        
        rates[base]['timestamp'] = datetime.utcnow().isoformat()
        self.redis.setex(
            self.CACHE_KEY,
            ttl,
            json.dumps(rates)
        )
```

---

### Paso 2: Celery task para actualización background

```python
# app/services/tasks/update_exchange_rates.py

from celery import shared_task
from flask import current_app
from app.core.database import db
from app.models.exchange_rate import ExchangeRate
from app.services.exchange_rate_service import ExchangeRateService
from datetime import datetime

@shared_task(bind=True)
def update_exchange_rates(self, base_currencies=None):
    """Actualiza tasas de cambio cada 5 minutos."""
    
    if base_currencies is None:
        base_currencies = ['USD', 'EUR', 'PEN']  # Configurable
    
    target_currencies = ['USD', 'EUR', 'PEN', 'MXN', 'COP']  # Monedas soportadas
    
    with current_app.app_context():
        service = ExchangeRateService(redis_client)
        
        for base in base_currencies:
            for target in target_currencies:
                if base != target:
                    rate = service.get_rate(base, target)
                    
                    if rate:
                        # Guardar en BD para auditoría
                        exchange_rate = ExchangeRate(
                            base_currency=base,
                            target_currency=target,
                            rate=rate,
                            timestamp=datetime.utcnow()
                        )
                        db.session.add(exchange_rate)
        
        db.session.commit()
```

---

### Paso 3: Configuración de Celery

```python
# Agregar a docker-compose.yml

celery:
  build: .
  container_name: proyecto_celery
  command: celery -A wsgi.celery worker -l info
  environment:
    - FLASK_ENV=development
    - DATABASE_URL=postgresql://...
    - REDIS_URL=redis://redis:6379
  depends_on:
    - redis
    - postgres
  volumes:
    - ./app:/app/app

celery-beat:  # Scheduler
  build: .
  container_name: proyecto_celery_beat
  command: celery -A wsgi.celery beat -l info --scheduler django_celery_beat.schedulers:DatabaseScheduler
  environment:
    - FLASK_ENV=development
    - DATABASE_URL=postgresql://...
    - REDIS_URL=redis://redis:6379
  depends_on:
    - redis
    - postgres
```

---

### Paso 4: Rutas para obtener tasa

```python
# app/api/v1/exchange/routes.py

from flask_restx import Namespace, Resource, fields
from flask import request
from flask_jwt_extended import jwt_required
from app.services.exchange_rate_service import ExchangeRateService
from app.utils.response import response_success, response_error

ns = Namespace('exchange', description='Tasas de cambio')

@ns.route('/rates')
class ExchangeRates(Resource):
    @jwt_required()
    def get(self):
        """Obtiene tasa de cambio actual."""
        base = request.args.get('base', 'USD')
        target = request.args.get('target', 'PEN')
        
        service = ExchangeRateService(redis_client)
        rate = service.get_rate(base, target)
        
        if rate:
            return response_success({
                'base': base,
                'target': target,
                'rate': rate,
                'timestamp': datetime.utcnow().isoformat()
            })
        
        return response_error('No se pudo obtener la tasa', 404)
```

---

## Variables de Entorno

```bash
# .env.example

# Exchange Rate Provider
EXCHANGE_RATE_PROVIDER=exchangerate_host  # O 'open_exchange_rates'
OPEN_EXCHANGE_RATES_KEY=your-api-key-here

# Celery / Redis
REDIS_URL=redis://redis:6379
CELERY_BROKER_URL=redis://redis:6379/0
CELERY_RESULT_BACKEND=redis://redis:6379/0

# Timing
EXCHANGE_RATE_CACHE_TTL=300  # 5 minutos
EXCHANGE_RATE_UPDATE_INTERVAL=300  # Cada 5 minutos
```

---

## Plan de Implementación

### Fase 1: MVP (Semana 1-2)

```
✅ ExchangeRate.host
✅ Caché en Redis (5 minutos)
✅ Endpoint GET /api/v1/exchange/rates
✅ Sincrónico (sin Celery aún)
✅ Pruebas unitarias
```

### Fase 2: Optimización (Semana 3)

```
✅ Celery + Celery Beat
✅ Actualización automática en background
✅ Almacenamiento histórico en PostgreSQL
✅ Manejo de errores con fallback
```

### Fase 3: Producción (Antes de deploy)

```
✅ Migrar a Open Exchange Rates
✅ API key en variables de entorno
✅ Rate limiting (si lo requiere)
✅ Monitoreo de fallos de API
✅ SLA de tiempo de respuesta
```

---

## Recomendación FINAL para tu proyecto

| Fase | Proveedor | Razón |
|---|---|---|
| **MVP** | ExchangeRate.host | Cero costos, suficiente precisión, válida tasas reales |
| **Beta / Testing** | ExchangeRate.host | Mismo, acumular volumen de transacciones |
| **Producción** | Open Exchange Rates | Tasas interbancarias, confiabilidad para dinero real |

**Ahorro de costos iniciales:** $0 → $12/mes cuando escales.

---

## Checklist de implementación

- [ ] Crear `ExchangeRateService` con métodos de caché
- [ ] Implementar proveedor `ExchangeRate.host`
- [ ] Rutas en `/api/v1/exchange`
- [ ] Tests unitarios (`test_exchange_rate_service.py`)
- [ ] Agregar Celery para actualización automática
- [ ] Almacenar histórico en `exchange_rate` table
- [ ] Documentación en Swagger
- [ ] Tests de integración con API externa
- [ ] Monitoreo y alertas si API falla
- [ ] Documentación de migración a Open Exchange Rates para producción
