package dev.yolocat.cpptranspiler.data;

import dev.yolocat.cpptranspiler.TranspilerUtils;

public class CppArg {

    public final String name;
    public final String type;
    public final String defaultValue;
    public final boolean isFinal;

    public CppArg(String name, String type, String defaultValue, boolean isFinal) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.isFinal = isFinal;
    }

    @Override
    public String toString() {
        return TranspilerUtils.pretty(this);
    }

    public static class Builder {
        private String name = "";
        private String type = "";
        private String defaultValue = "";
        private boolean isFinal = false;

        @SuppressWarnings("UnusedReturnValue")
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder isFinal(boolean isFinal) {
            this.isFinal = isFinal;
            return this;
        }

        public CppArg build() {
            return new CppArg(name, type, defaultValue, isFinal);
        }
    }

}
