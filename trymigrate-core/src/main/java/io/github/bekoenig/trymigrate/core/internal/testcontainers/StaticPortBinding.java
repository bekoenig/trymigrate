package io.github.bekoenig.trymigrate.core.internal.testcontainers;

import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateContainerCustomizer;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.List;
import java.util.Objects;

/**
 * Customizer to define a static port binding for container database by jvm property.
 * <p>
 * Only for debugging purposes recommended!
 * <p>
 * Usage:
 * {@link StaticPortBinding#PROPERTY_NAME}={@code [host_port]:[container_port]}
 * <ul>
 *     <li>host_port: target port for host system</li>
 *     <li>container_port: optional source port of container</li>
 * </ul>
 * On missing container_port the single exposed port will be used.
 * <p>
 * Examples:
 * <ul>
 *     <li>20000 (a single exposed container port will be bound to host port 20000)</li>
 *     <li>20000:40000 (the container port 40000 will be bound to host port 20000)</li>
 * </ul>
 */
public class StaticPortBinding implements TrymigrateContainerCustomizer {

    public static final String PROPERTY_NAME = "trymigrate.db-port";

    @Override
    public void accept(JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        String dbPort = System.getProperty(PROPERTY_NAME);
        if (Objects.isNull(dbPort)) {
            return;
        }

        if (!dbPort.matches("\\d*(:\\d*)?")) {
            throw new IllegalArgumentException("Invalid port mapping " + dbPort +
                    ". Format is [host_port] or [host_port]:[container_port].");
        }

        jdbcDatabaseContainer.setPortBindings(List.of(toPortBinding(jdbcDatabaseContainer, dbPort.split(":"))));
    }

    private static String toPortBinding(JdbcDatabaseContainer<?> jdbcDatabaseContainer, String[] dbPortParts) {
        return getHostPort(dbPortParts) + ":" + getContainerPort(jdbcDatabaseContainer, dbPortParts);
    }

    private static int getHostPort(String[] ports) {
        return Integer.parseInt(ports[0]);
    }

    private static int getContainerPort(JdbcDatabaseContainer<?> jdbcDatabaseContainer, String[] ports) {
        if (ports.length > 1) {
            return Integer.parseInt(ports[1]);
        }

        List<Integer> exposedPorts = jdbcDatabaseContainer.getExposedPorts();
        if (exposedPorts.isEmpty()) {
            throw new IllegalStateException("No container port is exposed.");
        } else if (exposedPorts.size() > 1) {
            throw new IllegalStateException("More than one container port is exposed. " +
                    "Add container port to fix ambiguous definition.");
        }
        return exposedPorts.get(0);
    }

}
