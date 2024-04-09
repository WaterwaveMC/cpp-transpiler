package dev.yolocat.cpptranspiler.data;

import dev.yolocat.cpptranspiler.TranspilerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CppMethod extends CppFunction {

    public final String name;
    public final boolean isStatic;
    public final boolean isAbstract;
    public final String returnType;

    public CppMethod(CppVisibility visibility, String javaSource, String[] contents, CppArg[] args, String name, boolean isStatic, boolean isAbstract, String returnType) {
        super(visibility, javaSource, contents, args);
        this.name = name;
        this.isStatic = isStatic;
        this.isAbstract = isAbstract;
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        return TranspilerUtils.pretty(this);
    }

    public static class Builder {
        private String name = "";
        private CppVisibility visibility = CppVisibility.PRIVATE;
        private boolean isStatic = false;
        private boolean isAbstract = false;
        private String returnType = "";
        private String javaSource = "";
        private final List<String> contents = new ArrayList<>();
        private final List<CppArg> args = new ArrayList<>();

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
        public Builder isStatic(boolean isStatic) {
            this.isStatic = isStatic;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder isAbstract(boolean isAbstract) {
            this.isAbstract = isAbstract;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder returnType(String returnType) {
            this.returnType = returnType;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder javaSource(String javaSource) {
            this.javaSource = javaSource;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder contents(String... contents) {
            Arrays.stream(contents).forEach(c -> this.contents.addAll(List.of(c.split("\n"))));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder args(CppArg... args) {
            this.args.addAll(List.of(args));
            return this;
        }

        public CppMethod build() {
            return new CppMethod(visibility, javaSource, contents.toArray(new String[0]), args.toArray(new CppArg[0]), name, isStatic, isAbstract, returnType);
        }
    }

}
