package io.github.bekoenig.trymigrate.core.internal.lint.config;

import schemacrawler.tools.lint.*;

import java.util.*;

public class CompositeLinterRegistry implements LinterInitializer {

    private final Map<String, LinterProvider> linterProviders = new HashMap<>();

    public void register(LinterProvider linterProvider) {
        linterProviders.put(linterProvider.getLinterId(), linterProvider);
    }

    @Override
    public Set<String> getRegisteredLinters() {
        HashSet<String> linterIds = new HashSet<>(linterProviders.keySet());
        linterIds.addAll(LinterRegistry.getLinterRegistry().getRegisteredLinters());
        return linterIds;
    }

    @Override
    public Linter newLinter(String linterId, LintCollector lintCollector) {
        LinterProvider linterProvider = linterProviders.get(linterId);
        if (Objects.nonNull(linterProvider)) {
            return linterProvider.newLinter(lintCollector);
        }
        return LinterRegistry.getLinterRegistry().newLinter(linterId, lintCollector);
    }
}
