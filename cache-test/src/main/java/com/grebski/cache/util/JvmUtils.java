package com.grebski.cache.util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Jvm utility class
 */
public class JvmUtils {

    private static final List<String> jvmArgs;
    private static final String HOTSPOT_BEAN_NAME =
            "com.sun.management:type=HotSpotDiagnostic";


    static {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();

        jvmArgs = ImmutableList.copyOf(arguments);
    }

    /**
     * Verifies that all arguments are NOT present in JVM args.
     * <p>Checks if any of JVM arguments starts with <code>unwantedArgs</code> elements</p>
     * @param unwantedArgs unwanted args
     */
    public static void verifyJvmArgumentsNotPresent(List<String> unwantedArgs) {
        List<String> argumentsPresentButNotWanted = jvmArgs.stream()
                .filter(jvmArg -> containsStartingWith(unwantedArgs, jvmArg))
                .collect(toList());

        if (!argumentsPresentButNotWanted.isEmpty()) {
            throw new IllegalStateException(String.format("JVM parameter(s) not wanted to be present: %s",
                    Joiner.on("\n").join(argumentsPresentButNotWanted)));
        }
    }

    private static boolean containsStartingWith(List<String> args, String jvmArg) {
        return args.stream()
                .filter(arg -> jvmArg.startsWith(arg))
                .findAny()
                .isPresent();
    }

    /**
     * Verifies jvm arguments are present
     * @param args expected args
     */
    public static void verifyJvmArgumentsPresent(List<String> args) {
        List<String> argsCopy = Lists.newArrayList(args);
        argsCopy.removeAll(jvmArgs);

        if (!argsCopy.isEmpty()) {
            throw new IllegalStateException(String.format("Following JVM arguments are missing:\n %s",
                    Joiner.on(" ").join(argsCopy)));
        }
    }
}
