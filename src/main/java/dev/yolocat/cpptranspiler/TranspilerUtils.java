package dev.yolocat.cpptranspiler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class TranspilerUtils {

    public static Iterable<? extends CompilationUnitTree> getJavaUnits(File sourceDir) throws IllegalArgumentException, IOException {
        if(!sourceDir.isDirectory()) {
            throw new IllegalArgumentException("sourceDir must be a directory");
        }

        File[] files = recursiveListFiles(sourceDir);
        if(files == null) return List.of(new CompilationUnitTree[0]);

        System.out.println(Arrays.toString(files));

        File[] javaFiles = Arrays.stream(files).filter(f -> f.getName().endsWith(".java")).toList().toArray(new File[0]);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(javaFiles);
        JavacTask task = (JavacTask) compiler.getTask(null, fileManager, null, null, null, compilationUnits);
        return task.parse();
    }

    private static File[] recursiveListFiles(File dir) {
        File[] files = dir.listFiles();
        if(files == null) return null;

        for(File file : files) {
            if(file.isDirectory()) {
                File[] subFiles = recursiveListFiles(file);
                if(subFiles != null) {
                    files = Arrays.copyOf(files, files.length + subFiles.length);
                    System.arraycopy(subFiles, 0, files, files.length - subFiles.length, subFiles.length);
                }
            }
        }

        return files;
    }

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String pretty(Object o) {
        return gson.toJson(o);
    }

}
