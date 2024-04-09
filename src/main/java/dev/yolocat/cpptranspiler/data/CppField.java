package dev.yolocat.cpptranspiler.data;

import dev.yolocat.cpptranspiler.TranspilerUtils;

public class CppField {

    public final String name;
    public final String type;
    public final CppVisibility visibility;
    public final boolean isStatic;
    public final boolean isFinal;
    public final String defaultValue;
    public final String javaSource;

    public CppField(String name, String type, CppVisibility visibility, boolean isStatic, boolean isFinal, String defaultValue, String javaSource) {
        this.name = name;
        this.type = type;
        this.visibility = visibility;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.defaultValue = defaultValue;
        this.javaSource = javaSource;
    }

    @Override
    public String toString() {
        return TranspilerUtils.pretty(this);
    }

    public static class Builder {
        private String name = "";
        private String type = "";
        private CppVisibility visibility = CppVisibility.PRIVATE;
        private boolean isStatic = false;
        private boolean isFinal = false;
        private String defaultValue = "";
        private String javaSource = "";

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
        public Builder isFinal(boolean isFinal) {
            this.isFinal = isFinal;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder javaSource(String javaSource) {
            this.javaSource = javaSource;
            return this;
        }

        public CppField build() {
            return new CppField(name, type, visibility, isStatic, isFinal, defaultValue, javaSource);
        }

    }

}
