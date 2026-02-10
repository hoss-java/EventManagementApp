package com.EventManApp.lib;

import java.lang.StackWalker.StackFrame;
import java.util.Arrays;

public class DebugUtil {
    
    private static final StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static void debug(Object... params) {
        StackFrame frame = stackWalker.walk(frames -> frames.skip(1).findFirst()).orElseThrow();
        
        String className = frame.getDeclaringClass().getSimpleName();
        String methodName = frame.getMethodName();
        int lineNumber = frame.getLineNumber(); // Get line number

        System.out.println("Entering method: " + className + "." + methodName + 
                           " at line: " + lineNumber + 
                           " with parameters: " + Arrays.toString(params));
    }
}
