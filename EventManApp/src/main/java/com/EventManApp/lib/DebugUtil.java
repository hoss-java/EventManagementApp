package com.EventManApp.lib;

import java.io.IOException;
import java.lang.StackWalker.StackFrame;
import java.util.Scanner;
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

    public static void debugAndWait(Object... params) {
        StackFrame frame = stackWalker.walk(frames -> frames.skip(1).findFirst()).orElseThrow();

        String className = frame.getDeclaringClass().getSimpleName();
        String methodName = frame.getMethodName();
        int lineNumber = frame.getLineNumber(); // Get line number

        // Print the debug message
        System.out.println("Entering method: " + className + "." + methodName + 
                           " at line: " + lineNumber + 
                           " with parameters: " + Arrays.toString(params));
        
        // Wait for user input
        System.out.println("Press any key to continue...");
        try {
            System.in.read(); // Wait for the key press
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
