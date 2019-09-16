#!/bin/bash

mvn dependency:copy@copy-h2-local
java -cp ./tmp/h2*.jar org.h2.tools.Script \
	-url jdbc:h2:file:./data/monster_hunter \
	-user sa \
	-script ./src/main/resources/schema.sql
mvn clean:clean@delete-h2-local package -DskipTests=true