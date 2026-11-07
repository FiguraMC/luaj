package org.luaj;

import org.luaj.vm2.LuaFunction;

@SuppressWarnings("unused")
public class UpcallTargets {
    public void call_varargs(LuaFunction printer, String fixedArg, Object... varargs) {
        printer.call(String.format("Fixed arg: %s", fixedArg));
        printVarArgs(printer, varargs);
    }

    public void call_vararg_strings(LuaFunction printer, String fixedArg, String... varargs) {
        call_varargs(printer, fixedArg, (Object[]) varargs);
    }

    public void call_varargs_of_object_arrays(LuaFunction printer, String fixedArg, Object[]... varargs) {
        printer.call(String.format("%s Found %d arrays", fixedArg, varargs.length));
        for (int i = 0; i < varargs.length; i++) {
            Object[] arr = varargs[i];
            printer.call(String.format("Layer %d has %d elements", i, arr.length));
            for (Object entry : arr) {
                printer.call(String.format("  %s", entry));
            }
        }
    }

    private static void printVarArgs(LuaFunction printer, Object... varargs) {
        printer.call(String.format("argc: %d", varargs.length));
        for (Object arg : varargs) {
            printer.call(arg.toString());
        }
    }

    private static void print() {

    }
}
