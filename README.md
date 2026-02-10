<img width="1193" height="456" alt="trymigrate-logo" src="https://github.com/user-attachments/assets/4af1eea5-0056-4ff7-81c0-9063727d3ce1" />

# try { migrate(); }

_TDD in database migration using Flyway, SchemaCrawler, Testcontainers and AssertJ._

Inspired by

https://www.red-gate.com/hub/product-learning/flyway/testing-databases-whats-required

https://www.codecentric.de/wissens-hub/blog/testing-your-database-migrations-with-flyway-and-testcontainers

## further recommendations

### assert database model

```xml
<dependency>
    <groupId>io.github.bekoenig</groupId>
    <artifactId>assertj-schemacrawler</artifactId>
    <version>...</version>
    <scope>test</scope>
</dependency>
```
### assert data content

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-db</artifactId>
    <version>...</version>
    <scope>test</scope>
</dependency>
```
