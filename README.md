# monitoring-debezium-mysql-elasticsearch-rest

An example monitor embedded debezium connector via REST

- Start / Stop / Reset offset
- View log data change via websocket

# Notes

- We are always dependent on the availability of the binlog after we have finished the initial snapshot. If we miss binlog entries beyond the binlog availability, we need to redo the full snapshot to gain a consistent state again.
( If application isn't running for sometimes, and in the meantime unprocessed binlog files get deleted on the database server (Binlog's retention / WAL's retention))

# TODO

- [ ] Add sample monitor dashboard
