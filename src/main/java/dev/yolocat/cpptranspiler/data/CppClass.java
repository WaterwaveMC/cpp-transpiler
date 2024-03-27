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
    public final String[] implementsInterfaces;
    public final CppMethod[] methods;
    public final CppField[] fields;
    public final CppConstructor[] constructors;

    public CppClass(String name, CppVisibility visibility, boolean isAbstract, String extendsClass, String[] implementsInterfaces, CppMethod[] methods, CppField[] fields, CppConstructor[] constructors) {
        this.name = name;
        this.visibility = visibility;
        this.isAbstract = isAbstract;
        this.extendsClass = extendsClass;
        this.implementsInterfaces = implementsInterfaces;
        this.methods = methods;
        this.fields = fields;
        this.constructors = constructors;
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
        public List<String> implementsInterfaces = new ArrayList<>();
        public List<CppMethod> methods = new ArrayList<>();
        public List<CppField> fields = new ArrayList<>();
        public List<CppConstructor> constructors = new ArrayList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder visibility(CppVisibility visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder isAbstract(boolean isAbstract) {
            this.isAbstract = isAbstract;
            return this;
        }

        public Builder extendsClass(String extendsClass) {
            this.extendsClass = extendsClass;
            return this;
        }

        public Builder implementsInterfaces(String... implementsInterfaces) {
            this.implementsInterfaces.addAll(Arrays.asList(implementsInterfaces));
            return this;
        }

        public Builder methods(CppMethod... methods) {
            this.methods.addAll(Arrays.asList(methods));
            return this;
        }

        public Builder fields(CppField... fields) {
            this.fields.addAll(Arrays.asList(fields));
            return this;
        }

        public Builder constructors(CppConstructor... constructors) {
            this.constructors.addAll(Arrays.asList(constructors));
            return this;
        }

        public CppClass build() {
            return new CppClass(name, visibility, isAbstract, extendsClass, implementsInterfaces.toArray(new String[0]), methods.toArray(new CppMethod[0]), fields.toArray(new CppField[0]), constructors.toArray(new CppConstructor[0]));
        }

    }

}
