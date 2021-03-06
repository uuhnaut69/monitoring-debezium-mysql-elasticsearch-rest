# Monitoring Embedded Debezium Mysql Elasticsearch Rest ![build](https://travis-ci.com/uuhnaut69/monitoring-debezium-mysql-elasticsearch-rest.svg?branch=master)

An example monitor embedded debezium connector via REST

- Control start / stop / rest connector via Restful API

- Implement using database to store offset checkpoint instead of memory or file (DatabaseOffsetBackingStore)

## Prerequisites

- Java 8+

- Docker

- Docker-Compose

## Start environment

```bash
docker-compose up -d
```

## Start api service

Generate dummy data

```http request
curl -X POST localhost:8080/companies
```

## Start connector service

Manual start
```http request
curl -X POST localhost:8081/sync/start
```

Or start from checkpoint time

```http request
curl -X POST localhost:8081/sync/start?fromCheckpointTime={CheckpointTime}
```

Stop connector

```http request
curl -X POST localhost:8081/sync/stop
```


## Notes

- We are always dependent on the availability of the binlog after we have finished the initial snapshot. If we miss binlog entries beyond the binlog availability, we need to redo the full snapshot to gain a consistent state again.
( If application isn't running for sometimes, and in the meantime unprocessed binlog files get deleted on the database server (Binlog's retention / WAL's retention)).

- We can migration data (redo a snapshot from a time in past) that create add a column flag, and trigger debezium by write a query to update flag field with condition timestamp wanna start. Dealing with table have auto update time, we can update without changing update_time field by set update_time = update_time

