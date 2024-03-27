package dev.yolocat.cpptranspiler.data;

import dev.yolocat.cpptranspiler.TranspilerUtils;

import java.util.ArrayList;
import java.util.List;

public class CppFile {

    public final String path;
    public final String name;
    public final String[] imports;
    public final CppClass[] classes;
    public final CppInterface[] interfaces;

    public CppFile(String path, String name, String[] imports, CppClass[] classes, CppInterface[] interfaces) {
        this.path = path;
        this.name = name;
        this.imports = imports;
        this.classes = classes;
        this.interfaces = interfaces;
    }

    @Override
    public String toString() {
        return TranspilerUtils.pretty(this);
    }

    public static class Builder {

        private String path = "";
        private String name = "";
        private final List<String> imports = new ArrayList<>();
        private final List<CppClass> classes = new ArrayList<>();
        private final List<CppInterface> interfaces = new ArrayList<>();

        @SuppressWarnings("UnusedReturnValue")
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder imports(String... imports) {
            this.imports.addAll(List.of(imports));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder classes(CppClass... classes) {
            this.classes.addAll(List.of(classes));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder interfaces(CppInterface... interfaces) {
            this.interfaces.addAll(List.of(interfaces));
            return this;
        }

        public CppFile build() {
            return new CppFile(path, name, imports.toArray(new String[0]), classes.toArray(new CppClass[0]), interfaces.toArray(new CppInterface[0]));
        }

    }

}
