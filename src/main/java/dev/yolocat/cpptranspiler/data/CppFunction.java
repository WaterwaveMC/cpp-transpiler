package dev.yolocat.cpptranspiler.data;

public abstract class CppFunction {

    public final CppVisibility visibility;
    public final String javaSource;
    public final String[] contents;
    public final CppArg[] args;

    public CppFunction(CppVisibility visibility, String javaSource, String[] contents, CppArg[] args) {
        this.visibility = visibility;
        this.javaSource = javaSource;
        this.contents = contents;
        this.args = args;
    }

}
