package dev.yolocat.cpptranspiler.data;

import dev.yolocat.cpptranspiler.TranspilerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CppMethod {

    public final String name;
    public final CppVisibility visibility;
    public final boolean isStatic;
    public final boolean isAbstract;
    public final String returnType;
    public final String[] contents;
    public final CppArg[] args;

    public CppMethod(String name, CppVisibility visibility, boolean isStatic, boolean isAbstract, String returnType, String[] contents, CppArg[] args) {
        this.name = name;
        this.visibility = visibility;
        this.isStatic = isStatic;
        this.isAbstract = isAbstract;
        this.returnType = returnType;
        this.contents = contents;
        this.args = args;
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
            return new CppMethod(name, visibility, isStatic, isAbstract, returnType, contents.toArray(new String[0]), args.toArray(new CppArg[0]));
        }
    }

}
