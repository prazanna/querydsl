package com.mysema.query;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.mysema.testutil.EmptyStatement;
import com.mysema.testutil.ExcludeIn;
import com.mysema.testutil.IncludeIn;

public class TargetRule implements MethodRule {

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object obj) {
        Target target = Connections.getTarget();
        boolean run = target == null || isExecuted(method.getMethod(), target);
        return run ? base : EmptyStatement.DEFAULT;
    }
    
    private boolean isExecuted(Method method, Target target) {
        ExcludeIn ex = method.getAnnotation(ExcludeIn.class);
        if (ex == null) {
            ex = method.getDeclaringClass().getAnnotation(ExcludeIn.class);
        }
        // excluded in given targets
        if (ex != null && Arrays.asList(ex.value()).contains(target)) {
            return false;
        }
        // included only in given targets
        IncludeIn in = method.getAnnotation(IncludeIn.class);
        if (in == null) {
            in = method.getDeclaringClass().getAnnotation(IncludeIn.class);
        }
        if (in != null && !Arrays.asList(in.value()).contains(target)) {
            return false;
        }
        return true;
    }

}
