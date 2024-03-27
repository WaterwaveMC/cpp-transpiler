package dev.yolocat.cpptranspiler.data;

import dev.yolocat.cpptranspiler.TranspilerUtils;

import java.util.ArrayList;
import java.util.List;

public class CppInterface {

    public final String name;
    public final CppVisibility visibility;
    public final String[] extendsInterfaces;
    public final CppMethod[] methods;

    public CppInterface(String name, CppVisibility visibility, String[] extendsInterfaces, CppMethod[] methods) {
        this.name = name;
        this.visibility = visibility;
        this.extendsInterfaces = extendsInterfaces;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return TranspilerUtils.pretty(this);
    }

    public static class Builder {

        public String name = "";
        public CppVisibility visibility = CppVisibility.PRIVATE;
        public final List<String> extendsInterfaces = new ArrayList<>();
        public final List<CppMethod> methods = new ArrayList<>();

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
        public Builder extendsInterfaces(String... extendsInterfaces) {
            this.extendsInterfaces.addAll(List.of(extendsInterfaces));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder methods(CppMethod... methods) {
            this.methods.addAll(List.of(methods));
            return this;
        }

        public CppInterface build() {
            return new CppInterface(name, visibility, extendsInterfaces.toArray(new String[0]), methods.toArray(new CppMethod[0]));
        }
    }

}
