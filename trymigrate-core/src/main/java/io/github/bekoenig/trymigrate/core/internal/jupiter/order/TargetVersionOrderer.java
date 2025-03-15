package io.github.bekoenig.trymigrate.core.internal.jupiter.order;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrdererContext;


public class TargetVersionOrderer implements MethodOrderer {

    @Override
    public void orderMethods(MethodOrdererContext context) {
        context.getMethodDescriptors().sort(new TargetVersionComparator());
    }

}
