package com.sw;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static StackWalker RETAIN_CLASS_SW = StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE));

    private static StackWalker SHOW_REFLECT_SW = StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE, Option.SHOW_REFLECT_FRAMES));

    private static StackWalker SHOW_HIDDEN_SW = StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE, Option.SHOW_HIDDEN_FRAMES));

    private static Class getCallerClass() {
        return RETAIN_CLASS_SW.getCallerClass();
    }

    private static Object printStackFrame() {
        //print stack frame
        forEachByRetainClass();
        //print reflect stack frame
        forEachByShowReflect();
        //print hidden stack frame
        forEachByShowHidden();
        return null;
    }

    private static void forEachByRetainClass() {
        RETAIN_CLASS_SW.forEach(System.out::println);
    }

    private static void forEachByShowReflect() {
        SHOW_REFLECT_SW.forEach(System.out::println);
    }

    private static void forEachByShowHidden() {
        SHOW_HIDDEN_SW.forEach(System.out::println);
    }

    private static void walkStackFrames() {
        //count stack frame
        Function<Stream<StackFrame>, Long> countStackFrameFun = stackFrameStream -> stackFrameStream.count();
        Long stackFrameCount = SHOW_HIDDEN_SW.walk(countStackFrameFun);
        System.out.println(stackFrameCount);

        //StackWalker.forEach
        Function<Stream<StackFrame>, Object> printStackFrameFun = stackFrameStream -> {
            stackFrameStream.forEach(System.out::println);
            return null;
        };
        SHOW_HIDDEN_SW.walk(printStackFrameFun);

        //Dump stack frame to a list
        Function<Stream<StackFrame>, List<StackFrame>> listStackFrameFun = stackFrameStream -> stackFrameStream.collect(Collectors.toList());
        List<StackFrame> list = SHOW_HIDDEN_SW.walk(listStackFrameFun);
        list.forEach(System.out::println);
    }

    private static void assertion(Class callerClass) {
        assert callerClass == Main.class : "Expected Main.class, got " + callerClass;
    }

    /**
     * VM options -enableassertions
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        //direct call getCallerClass
        Class callerClass = getCallerClass();
        assertion(callerClass);

        //lambda call getCallerClass
        Supplier<Class> getCallerClass = Main::getCallerClass;
        callerClass = getCallerClass.get();
        assertion(callerClass);

        //reflection call getCallerClass
        callerClass = (Class) Main.class.getDeclaredMethod("getCallerClass").invoke(Main.class);
        assertion(callerClass);


        //direct call printStackFrame
        printStackFrame();

        //lambda call printStackFrame
        Supplier<Object> printStackFrame = Main::printStackFrame;
        printStackFrame.get();

        //reflection call printStackFrame
        Main.class.getDeclaredMethod("printStackFrame").invoke(Main.class);

        walkStackFrames();
    }
}
