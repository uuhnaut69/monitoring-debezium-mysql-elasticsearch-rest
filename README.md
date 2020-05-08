# monitoring-debezium-mysql-elasticsearch-rest ![build](https://travis-ci.com/uuhnaut69/monitoring-debezium-mysql-elasticsearch-rest.svg?branch=master)

An example monitor embedded debezium connector via REST

- Start / Stop / Reset offset

- View log data change via websocket

- Use database to store offset data (DatabaseOffsetBackingStore)

# Notes

- We are always dependent on the availability of the binlog after we have finished the initial snapshot. If we miss binlog entries beyond the binlog availability, we need to redo the full snapshot to gain a consistent state again.
( If application isn't running for sometimes, and in the meantime unprocessed binlog files get deleted on the database server (Binlog's retention / WAL's retention)).

- We can migration data (redo a stampshot from a time in past) that create add a column flag, and trigger debezium by write a query to update flag field with condition timestamp wanna start. Dealing with table have auto update time, we can update without changing update_time field by set update_time = update_time

# TODO

- [ ] Implement save schema history into database instead of file
