package io.github.bekoenig.trymigrate.core.internal.lifecycle;

import io.github.bekoenig.trymigrate.core.Trymigrate;
import io.github.bekoenig.trymigrate.core.internal.StoreSupport;
import io.github.bekoenig.trymigrate.core.internal.catalog.CatalogFactory;
import io.github.bekoenig.trymigrate.core.internal.lint.*;
import io.github.bekoenig.trymigrate.core.internal.lint.config.CompositeLinterRegistry;
import io.github.bekoenig.trymigrate.core.internal.migrate.MigrateProcessor;
import io.github.bekoenig.trymigrate.core.internal.plugin.BeanProviderFactory;
import io.github.bekoenig.trymigrate.core.internal.plugin.PluginDiscovery;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateAssertLints;
import io.github.bekoenig.trymigrate.core.lint.TrymigrateExcludeLint;
import io.github.bekoenig.trymigrate.core.lint.config.TrymigrateLintersConfigurer;
import io.github.bekoenig.trymigrate.core.lint.report.TrymigrateLintsReporter;
import io.github.bekoenig.trymigrate.core.plugin.TrymigrateBeanProvider;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateDataLoader;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateFlywayCustomizer;
import io.github.bekoenig.trymigrate.core.plugin.customize.TrymigrateCatalogCustomizer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.testcontainers.containers.JdbcDatabaseContainer;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.LinterProvider;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

public class MigrateInitializer implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) {
        Trymigrate testConfiguration = AnnotationSupport.findAnnotation(o.getClass(),
                Trymigrate.class).orElseThrow();

        TrymigrateBeanProvider beanProvider = new BeanProviderFactory().create(o, new PluginDiscovery().discover(
                testConfiguration.discoverPlugin(), testConfiguration.excludePlugins()));

        CatalogFactory catalogFactory = new CatalogFactory(
                reverse(beanProvider.all(TrymigrateCatalogCustomizer.class)));

        LintSeverity failOn = AnnotationSupport.findAnnotation(o.getClass(),
                TrymigrateAssertLints.class).map(TrymigrateAssertLints::failOn).orElse(null);

        LintProcessor lintProcessor = new LintProcessor(
                excludedLintPatterns(o.getClass()),
                new CompositeLinterRegistry(beanProvider.all(LinterProvider.class)),
                reverse(beanProvider.all(TrymigrateLintersConfigurer.class)),
                new LintsHistory(),
                beanProvider.all(TrymigrateLintsReporter.class),
                new LintsAssert(failOn));

        MigrateProcessor migrateProcessor = new MigrateProcessor(
                resolveJdbcDatabaseContainer(beanProvider),
                beanProvider.all(TrymigrateFlywayCustomizer.class), // TODO: prefer reserved order
                beanProvider.all(TrymigrateDataLoader.class),
                catalogFactory,
                lintProcessor);
        StoreSupport.putMigrateProcessor(extensionContext, migrateProcessor);

        migrateProcessor.prepare();
    }

    private <T> List<T> reverse(List<T> list) {
        List<T> arrayList = new ArrayList<>(list);
        Collections.reverse(arrayList);
        return arrayList;
    }

    private LintPatterns excludedLintPatterns(AnnotatedElement annotatedElement) {
        return new LintPatterns(AnnotationSupport.findRepeatableAnnotations(
                        annotatedElement, TrymigrateExcludeLint.class).stream()
                .map(x -> new LintPattern(x.linterId(), x.objectName())).toList());
    }

    private JdbcDatabaseContainer<?> resolveJdbcDatabaseContainer(TrymigrateBeanProvider beanProvider) {
        try {
            return beanProvider.findOne(JdbcDatabaseContainer.class).orElse(null);
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

}
