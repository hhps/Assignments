Lesson 18: IoC
=====

This is a simple application which demonstrates work of Spring IoC, transaction manager and unit testing of Spring
applications.

Run tests:
```
mvn test
```

Run application:
```
mvn exec:java
```

Note that you have to run PostgreSQL instance before running the application and execute current bash script:
```
./scripts/prepare-db-as-postgres.sh
```
Default password for `hh_hw_ioc` PostgreSQL user is the same as login (`hh_hw_ioc`). If you prefer you own password
you should also change it in `./src/main/resources/db.properties`.
