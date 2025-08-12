# Objetivo

Escalar o backend para 10.000 requisições/minuto (~167 RPS) garantindo baixa latência (P95 < 300 ms para cotações) e confiabilidade (SLA 99,9%). Cenário: Aracaju ~30k corridas/dia, frota crescendo de 200 → 2.000 motoristas, picos em regiões densas, reclamações por falta de ETA (previsão de chegada) do motorista.

## Visão de Arquitetura 

Edge: CDN + WAF + Rate limiting por IP/appId.

API: Spring Boot (Kotlin) em pods horizontais. Kotlin coroutines.

Fila/Assíncrono: Kafka (ou SQS/PubSub) para eventos de posição, criação/atualização de rides, e fanout de notificações.

Dados quentes: Redis (Cluster) para GEO (localização de motoristas, disponibilidade, locks/idempotência) e cache de cotações/ETAs.

Dados transacionais: MySQL (InnoDB) com índices e read replicas (ou PostgreSQL + PostGIS se priorizarmos recursos GIS avançados).

Armazenamento frio/analítico: bucket + lake/warehouse para BI.

Observabilidade: Prometheus + Grafana, OpenTelemetry (traces), Loki (logs), SLOs e alertas.

## Banco de Dados

Situação atual (MySQL)

Tabelas principais: driver, ride, (posições em tempo real entrando).

Uso de POINT SRID 4326 e SPATIAL INDEX para proximidade; bom, porém funções GIS do MySQL são mais limitadas vs PostGIS.

Otimizações imediatas (MySQL)

### Índices

#### ride:

INDEX idx_ride_driver_status (driver_id, status) para buscas de ride por motorista/estado.

INDEX idx_ride_created (created_at) para ordenações/housekeeping.

#### driver:

SPATIAL INDEX idx_driver_location (location) (NOT NULL).

INDEX idx_driver_available (available, location_updated_at) para filtros rápidos de disponibilidade recentes.

Indexações Geoespaciais & Matching

Redis GEO (hot path)

Manter toda a frota disponível em um GEOSET por cidade/área: GEOADD drivers:aracaju lon lat driverId.


Banco relacional (fallback/consistência)

Persistir driver.location com POINT para relatórios e backup.

Para matching no banco, usar ST_Distance_Sphere (MySQL) ou ST_DWithin (PostGIS) com índice espacial + bounding box.

Threads & Concorrência

Server: migrar para Netty (WebFlux) com coroutines. 


## Atualização de posição

Ingestão via Kafka/HTTP; normalizar e escrever em Redis GEO; snapshots no DB assíncronos.

## Observabilidade & Confiabilidade

Métricas: RPS, P50/P95 latência por rota, taxa de cache hit, erros das integrações Google, fila de eventos, tempo de matching.

Traces: propagar traceId entre API→Redis→DB

## Alertas:

Cache hit < 60% por 10 min.

Latência P95 de /rides/quote > 400 ms.

Erros 5xx > 1% por 5 min.

Circuit breaker.