package dev.yolocat.cpptranspiler.data;

import dev.yolocat.cpptranspiler.TranspilerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CppConstructor extends CppFunction {

    public CppConstructor(CppVisibility visibility, String javaSource, String[] contents, CppArg[] args) {
        super(visibility, javaSource, contents, args);
    }

    @Override
    public String toString() {
        return TranspilerUtils.pretty(this);
    }

    public static class Builder {

        public final List<CppArg> args = new ArrayList<>();
        public CppVisibility visibility = CppVisibility.PRIVATE;
        public final List<String> contents = new ArrayList<>();
        public String javaSource = "";

        @SuppressWarnings("UnusedReturnValue")
        public Builder args(CppArg... args) {
            this.args.addAll(List.of(args));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder visibility(CppVisibility visibility) {
            this.visibility = visibility;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder contents(String... contents) {
            Arrays.stream(contents).forEach(c -> this.contents.addAll(List.of(c.split("\n"))));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder javaSource(String javaSource) {
            this.javaSource = javaSource;
            return this;
        }

        public CppConstructor build() {
            return new CppConstructor(visibility, javaSource, contents.toArray(new String[0]), args.toArray(new CppArg[0]));
        }

    }

}
