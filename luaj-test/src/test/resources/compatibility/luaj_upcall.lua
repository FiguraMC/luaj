local jClass = luajava.newInstance("org.luaj.UpcallTargets");

jClass:call_varargs(print, "New Grammar")
jClass:call_varargs(print, "New Grammar", "ARG_0_0,")
jClass:call_varargs(print, "New Grammar", "ARG_1_0,", "ARG_1_1")
print()

jClass:call_vararg_strings(print, "New Grammar TYPED")
jClass:call_vararg_strings(print, "New Grammar TYPED", "ARG_0_0,")
jClass:call_vararg_strings(print, "New Grammar TYPED", "ARG_1_0,", "ARG_1_1")
print()

-- Test vararg calls with the existing usable grammar
jClass:call_varargs(print, "Old Grammar Compat", {})
jClass:call_varargs(print, "Old Grammar Compat", { "ARG_0_0," })
jClass:call_varargs(print, "Old Grammar Compat", { "ARG_1_0,", "ARG_1_1" })
print()

jClass:call_vararg_strings(print, "Old Grammar Compat TYPED", {})
jClass:call_vararg_strings(print, "Old Grammar Compat TYPED", { "ARG_0_0," })
jClass:call_vararg_strings(print, "Old Grammar Compat TYPED", { "ARG_1_0,", "ARG_1_1" })
print()

-- Test Object[]...
jClass:call_varargs_of_object_arrays(print, "Object[]... TEST")
jClass:call_varargs_of_object_arrays(print, "Object[]... TEST", { "ARG_0_0" }, { "ARG_0_1" })
jClass:call_varargs_of_object_arrays(print, "Object[]... TEST", {})
jClass:call_varargs_of_object_arrays(print, "Object[]... TEST", { "ARG_1_0" })
jClass:call_varargs_of_object_arrays(print, "Object[]... TEST", { "ARG_2_0", 1 }, { "ARG_2_2" }, {})