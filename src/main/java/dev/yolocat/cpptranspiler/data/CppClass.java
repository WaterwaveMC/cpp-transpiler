package dev.yolocat.cpptranspiler.data;

import dev.yolocat.cpptranspiler.TranspilerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CppClass {

    public final String name;
    public final CppVisibility visibility;
    public final boolean isAbstract;
    public final String extendsClass;
    public final String javaSource;
    public final String[] implementsInterfaces;
    public final CppMethod[] methods;
    public final CppField[] fields;
    public final CppConstructor[] constructors;
    public final String[] inits;
    public final String[] extras;

    public CppClass(String name, CppVisibility visibility, boolean isAbstract, String extendsClass, String javaSource, String[] implementsInterfaces, CppMethod[] methods, CppField[] fields, CppConstructor[] constructors, String[] inits, String[] extras) {
        this.name = name;
        this.visibility = visibility;
        this.isAbstract = isAbstract;
        this.extendsClass = extendsClass;
        this.javaSource = javaSource;
        this.implementsInterfaces = implementsInterfaces;
        this.methods = methods;
        this.fields = fields;
        this.constructors = constructors;
        this.inits = inits;
        this.extras = extras;
    }

    @Override
    public String toString() {
        return TranspilerUtils.pretty(this);
    }

    public static class Builder {

        public String name = "";
        public CppVisibility visibility = CppVisibility.PRIVATE;
        public boolean isAbstract = false;
        public String extendsClass = "";
        public String javaSource = "";
        public final List<String> implementsInterfaces = new ArrayList<>();
        public final List<CppMethod> methods = new ArrayList<>();
        public final List<CppField> fields = new ArrayList<>();
        public final List<CppConstructor> constructors = new ArrayList<>();
        public final List<String> inits = new ArrayList<>();
        public final List<String> extras = new ArrayList<>();

        @SuppressWarnings("UnusedReturnValue")
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder visibility(CppVisibility visibility) {
            this.visibility = visibility;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder isAbstract(boolean isAbstract) {
            this.isAbstract = isAbstract;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder extendsClass(String extendsClass) {
            this.extendsClass = extendsClass;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder javaSource(String javaSource) {
            this.javaSource = javaSource;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder implementsInterfaces(String... implementsInterfaces) {
            this.implementsInterfaces.addAll(Arrays.asList(implementsInterfaces));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder methods(CppMethod... methods) {
            this.methods.addAll(Arrays.asList(methods));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder fields(CppField... fields) {
            this.fields.addAll(Arrays.asList(fields));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder constructors(CppConstructor... constructors) {
            this.constructors.addAll(Arrays.asList(constructors));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder inits(String... inits) {
            this.inits.addAll(Arrays.asList(inits));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder extras(String... extras) {
            this.extras.addAll(Arrays.asList(extras));
            return this;
        }

        public CppClass build() {
            return new CppClass(name, visibility, isAbstract, extendsClass, javaSource, implementsInterfaces.toArray(new String[0]), methods.toArray(new CppMethod[0]), fields.toArray(new CppField[0]), constructors.toArray(new CppConstructor[0]), inits.toArray(new String[0]), extras.toArray(new String[0]));
        }
    }

}
