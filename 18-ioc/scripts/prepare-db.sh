#!/bin/sh

createdb hh_hw_ioc
createuser -P hh_hw_ioc
psql -d hh_hw_ioc -U hh_hw_ioc -h localhost < ./src/test/resources/schema.sql
