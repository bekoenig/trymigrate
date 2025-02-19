package io.github.bekoenig.trymigrate.core.config;

import java.util.List;
import java.util.Optional;

public interface TrymigrateBeanProvider {

    <T> List<T> all(Class<T> clazz);

    <T> T one(Class<T> clazz);

    <T> Optional<T> findOne(Class<T> clazz);

    <T> T first(Class<T> clazz);

    <T> Optional<T> findFirst(Class<T> clazz);
}
