package dev.yolocat.cpptranspiler.data;

import dev.yolocat.cpptranspiler.TranspilerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CppConstructor {

    public final CppArg[] args;
    public final CppVisibility visibility;
    public final String[] contents;

    public CppConstructor(CppArg[] args, CppVisibility visibility, String[] contents) {
        this.args = args;
        this.visibility = visibility;
        this.contents = contents;
    }

    @Override
    public String toString() {
        return TranspilerUtils.pretty(this);
    }

    public static class Builder {

        public final List<CppArg> args = new ArrayList<>();
        public CppVisibility visibility = CppVisibility.PRIVATE;
        public final List<String> contents = new ArrayList<>();

        public Builder args(CppArg... args) {
            this.args.addAll(List.of(args));
            return this;
        }

        public Builder visibility(CppVisibility visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder contents(String... contents) {
            Arrays.stream(contents).forEach(c -> this.contents.addAll(List.of(c.split("\n"))));
            return this;
        }

        public CppConstructor build() {
            return new CppConstructor(args.toArray(new CppArg[0]), visibility, contents.toArray(new String[0]));
        }

    }

}
