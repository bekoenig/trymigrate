package io.github.bekoenig.trymigrate.core.internal.schemacrawler.lint;

import schemacrawler.tools.lint.Lints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LintsHistory {

    private final List<LintPattern> ignoredLints;

    private final Map<String, Lints> lints;

    private final List<String> order;

    public LintsHistory(List<LintPattern> ignoredLints) {
        this.ignoredLints = ignoredLints;
        this.lints = new HashMap<>();
        this.order = new ArrayList<>();
    }

    public boolean isAnalysed(String descriptor) {
        return lints.containsKey(descriptor);
    }

    public void putLints(String descriptor, Lints lints) {
        this.lints.put(descriptor, lints);
        this.order.add(descriptor);
    }

    public Lints getLints(String descriptor) {
        return this.lints.get(descriptor);
    }

    public Lints diff(String fromDescriptor, String toDescriptor) {
        Lints beforeMigrate = getLints(fromDescriptor);
        Lints afterMigrate = getLints(toDescriptor);

        return new Lints(afterMigrate.stream()
                // drop known lints
                .filter(l -> !beforeMigrate.getLints().contains(l))
                // drop ignored lints
                .filter(l -> ignoredLints.stream().noneMatch(x -> x.matches(l)))
                .collect(Collectors.toList()));
    }

    public String getLastAnalyzedVersion() {
        return this.order.get(this.order.size() - 1);
    }

}
