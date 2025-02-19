package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint.config;

import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.Linter;
import schemacrawler.tools.lint.LinterInitializer;
import schemacrawler.tools.lint.LinterProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LinterProviderRegistry implements LinterInitializer {

    private final Map<String, LinterProvider> linterProviders = new HashMap<>();

    public void register(LinterProvider linterProvider) {
        linterProviders.put(linterProvider.getLinterId(), linterProvider);
    }

    public boolean isRegistered(String linterId) {
        return linterProviders.containsKey(linterId);
    }

    @Override
    public Set<String> getRegisteredLinters() {
        return new HashSet<>(linterProviders.keySet());
    }

    @Override
    public Linter newLinter(String linterId, LintCollector lintCollector) {
        LinterProvider linterProvider = linterProviders.get(linterId);
        if (linterProvider == null) {
            throw new IllegalArgumentException("No linter with id <%s> registered".formatted(linterId));
        }

        return linterProvider.newLinter(lintCollector);
    }
}
