package dev.yolocat.cpptranspiler;

import com.sun.source.tree.*;
import dev.yolocat.cpptranspiler.data.*;
import dev.yolocat.cpptranspiler.utils.Pair;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Transpiler {

    public static boolean debug = false;
    public static boolean printFiles = false;
    public static int totalFiles = 0;
    public static int currentFile = 0;

    // Transpiler Step 1: Java -> Transpiler Intermediary

    public static CppFile[] transpile(File sourceDir) throws IOException {
        if(printFiles || debug) System.out.println("Compiling sources...\nNote: This may take some time!");
        Iterable<? extends CompilationUnitTree> units = TranspilerUtils.getJavaUnits(sourceDir);

        if (units instanceof Collection) {
            totalFiles = ((Collection<?>) units).size();
        } else {
            for (CompilationUnitTree unit : units) {
                totalFiles++;
            }
        }

        System.out.println("Found " + totalFiles + " Java Source files");

        List<CppFile> files = new ArrayList<>();
        units.forEach(unit -> files.add(transpile(unit)));

        return files.toArray(new CppFile[0]);
    }

    private static CppFile transpile(CompilationUnitTree tree) {
        currentFile++;
        if(printFiles) System.out.println("Transpiling (" + currentFile + "/" + totalFiles + "): " + tree.getSourceFile().getName());
        if(debug) System.out.println("Transpiling CompilationUnitTree " + tree.getSourceFile().getName());
        CppFile.Builder file = new CppFile.Builder();

        if(tree.getPackageName() == null) file.path(".");
        else file.path(tree.getPackageName().toString().replaceAll("\\.", "/"));
        String path = tree.getSourceFile().getName();
        file.name(path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.')));

        file.imports(tree.getImports().stream().map(Transpiler::transpile).toList().toArray(new String[0]));

        List<? extends Tree> typeDecls = tree.getTypeDecls();
        typeDecls.forEach(decl -> {
            if (decl instanceof ClassTree) {
                if(decl.getKind().equals(Tree.Kind.CLASS)) {
                    file.classes(transpile((ClassTree) decl, false));
                } else if(decl.getKind().equals(Tree.Kind.INTERFACE)) {
                    file.classes(transpile((ClassTree) decl, true));
                } else if(decl.getKind().equals(Tree.Kind.ENUM)) {
                    file.classes(transpile((ClassTree) decl, false));
                } else {
                    if(debug) System.out.println("Unsupported class declaration: " + decl.getKind().toString());
                }
            } else {
                if(debug) System.out.println("Unsupported type declaration: " + decl.getClass().getName());
            }
        });

        return file.build();
    }

    private static CppClass transpile(ClassTree tree, boolean intf) {
        if(debug) System.out.println("Transpiling ClassTree " + tree.getSimpleName().toString() + " (intf: " + intf + ")");

        CppClass.Builder builder = new CppClass.Builder()
                .name(tree.getSimpleName().toString())
                .visibility(transpile(tree.getModifiers().getFlags()))
                .isAbstract(tree.getModifiers().getFlags().contains(Modifier.ABSTRACT) || intf)
                .extendsClass(transpile(tree.getExtendsClause()))
                .implementsInterfaces(transpileImplements(tree.getImplementsClause()))
                .javaSource(tree.toString().substring(1, tree.toString().indexOf("{") + 1));

        List<? extends Tree> members = tree.getMembers();
        members.forEach(member -> {
            if (member instanceof MethodTree) {
                Name name = ((MethodTree) member).getName();
                if(name.isEmpty() || name.toString().equalsIgnoreCase("<init>")) {
                    builder.constructors(transpileConstructor((MethodTree) member));
                } else {
                    builder.methods(transpile((MethodTree) member, intf));
                }
            } else if (member instanceof VariableTree) {
                builder.fields(transpile((VariableTree) member));
            } else if(member instanceof BlockTree) {
                builder.inits(String.join("\n", transpile((BlockTree) member)));
            } else if(member instanceof ClassTree) {
                if(((ClassTree) member).getModifiers().getFlags().contains(Modifier.STATIC)) builder.extras(transpile(transpile((ClassTree) member, false)));
                else builder.inits(member.toString());
            } else {
                if(debug) System.out.println("Unsupported class member: " + member.getClass().getName());
            }
        });

        return builder.build();
    }

    private static String[] transpileImplements(List<? extends Tree> implementsClause) {
        if(debug) System.out.println("Transpiling Implements");

        if(implementsClause == null) return new String[0];
        if(implementsClause.isEmpty()) return new String[0];

        return implementsClause.stream().map(Tree::toString).toList().toArray(new String[0]);
    }

    private static String transpile(Tree extendsClause) {
        if(debug) System.out.println("Transpiling Extends");

        if(extendsClause == null) return "";
        return extendsClause.toString();
    }

    private static CppMethod transpile(MethodTree tree, boolean isInterface) {
        if(debug) System.out.println("Transpiling MethodTree " + tree.getName().toString());

        boolean isAbstract;
        CppMethod.Builder builder = new CppMethod.Builder()
                .name(tree.getName().toString())
                .visibility(transpile(tree.getModifiers().getFlags()))
                .isStatic(tree.getModifiers().getFlags().contains(Modifier.STATIC))
                .isAbstract((isAbstract = tree.getModifiers().getFlags().contains(Modifier.ABSTRACT) || isInterface))
                .returnType(tree.getReturnType().toString())
                .args(transpileArgs(tree.getParameters()))
                .javaSource(tree.toString().substring(1, tree.toString().indexOf(isAbstract ? ";" : "{") + 1));

        if(!tree.getModifiers().getFlags().contains(Modifier.ABSTRACT) && !isInterface) builder.contents(transpile(tree.getBody()));
        return builder.build();
    }

    private static CppArg[] transpileArgs(List<? extends VariableTree> parameters) {
        if(debug) System.out.println("Transpiling Args");

        return parameters.stream().map(Transpiler::transpileArg).toList().toArray(new CppArg[0]);
    }

    private static CppArg transpileArg(VariableTree tree) {
        if(debug) System.out.println("Transpiling VariableTree " + tree.getName().toString());

        return new CppArg.Builder()
                .name(tree.getName().toString())
                .type(tree.getType().toString())
                .defaultValue(tree.getInitializer() == null ? "" : tree.getInitializer().toString())
                .isFinal(tree.getModifiers().getFlags().contains(Modifier.FINAL))
                .build();
    }

    private static String[] transpile(BlockTree tree) {
        if(debug) System.out.println("Transpiling BlockTree");

        return tree.getStatements().stream().map(Tree::toString).toList().toArray(new String[0]);
    }

    private static CppConstructor transpileConstructor(MethodTree tree) {
        if(debug) System.out.println("Transpiling Constructor MethodTree");

        return new CppConstructor.Builder()
                .args(transpileArgs(tree.getParameters()))
                .visibility(transpile(tree.getModifiers().getFlags()))
                .contents(tree.getBody().toString())
                .build();
    }

    private static CppField transpile(VariableTree tree) {
        if(debug) System.out.println("Transpiling VariableTree " + tree.getName().toString());

        return new CppField.Builder()
                .name(tree.getName().toString())
                .type(tree.getType().toString())
                .isFinal(tree.getModifiers().getFlags().contains(Modifier.FINAL))
                .visibility(transpile(tree.getModifiers().getFlags()))
                .isStatic(tree.getModifiers().getFlags().contains(Modifier.STATIC))
                .defaultValue(tree.getInitializer() == null ? "" : tree.getInitializer().toString())
                .javaSource(tree.toString())
                .build();
    }

    private static CppVisibility transpile(Set<Modifier> flags) {
        if(debug) System.out.println("Transpiling Modifiers");

        if(flags.contains(Modifier.PUBLIC)) return CppVisibility.PUBLIC;
        if(flags.contains(Modifier.PROTECTED)) return CppVisibility.PROTECTED;
        if(flags.contains(Modifier.PRIVATE)) return CppVisibility.PRIVATE;

        if(debug) System.out.println("WARN: No access modifier specified, implicitly declaring as public");
        return CppVisibility.PUBLIC;
    }

    private static String transpile(ImportTree tree) {
        if(debug) System.out.println("Transpiling ImportTree");

        return tree.getQualifiedIdentifier().toString().replaceAll("\\.", "/");
    }

    // Transpiler Step 2: Transpiler Intermediary -> C++

    public static String transpile(CppFile file) {
        StringBuilder cpp = new StringBuilder();

        cpp.append("// Generated by yolocat's Java -> C++ transpiler\n")
                .append("// https://github.com/WaterwaveMC/cpp-transpiler\n")
                .append("//\n");

        cpp.append("// Base file: ");
        cpp.append(file.path);
        cpp.append("/");
        cpp.append(file.name);
        cpp.append(".java\n\n");

        cpp.append("module;\n\n");

        // add #include stl

        cpp.append("export module ")
                .append(file.path.replaceAll("/", "."))
                .append(".")
                .append(file.name)
                .append(";\n\n");

        Arrays.stream(file.imports).forEach(i -> cpp.append("import ").append(i.replaceAll("/", ".")).append(";\n"));
        if(file.imports.length > 0) cpp.append("\n");

        for(CppClass clazz : file.classes) {
            cpp.append(transpile(clazz));
            cpp.append("\n\n");
        }

        int len = cpp.length();
        cpp.delete(len - 2, len);

        return cpp.toString();
    }

    private static String transpile(CppClass clazz) {
        StringBuilder cpp = new StringBuilder();

        String[] lines = clazz.javaSource.split("\n");
        for (String line : lines) {
            cpp.append("// ").append(line).append("\n");
        }

        if(clazz.visibility == CppVisibility.PUBLIC) cpp.append("export ");
        cpp.append("class ").append(clazz.name);

        if(!clazz.extendsClass.isEmpty()) {
            cpp.append(" : public ").append(clazz.extendsClass);
        }

        for(int i = 0; i < clazz.implementsInterfaces.length; i++) {
            if(i == 0 && clazz.extendsClass.isEmpty()) cpp.append(" : ");
            else cpp.append(", ");

            cpp.append("public ").append(clazz.implementsInterfaces[i]);
        }

        cpp.append(" {\n");

        List<CppField> privateFields = Arrays.stream(clazz.fields).filter(f -> f.visibility == CppVisibility.PRIVATE).toList();
        List<CppField> protectedFields = Arrays.stream(clazz.fields).filter(f -> f.visibility == CppVisibility.PROTECTED).toList();
        List<CppField> publicFields = Arrays.stream(clazz.fields).filter(f -> f.visibility == CppVisibility.PUBLIC).toList();

        List<CppConstructor> privateConstructors = Arrays.stream(clazz.constructors).filter(c -> c.visibility == CppVisibility.PRIVATE).toList();
        List<CppConstructor> protectedConstructors = Arrays.stream(clazz.constructors).filter(c -> c.visibility == CppVisibility.PROTECTED).toList();
        List<CppConstructor> publicConstructors = Arrays.stream(clazz.constructors).filter(c -> c.visibility == CppVisibility.PUBLIC).toList();

        List<CppMethod> privateMethods = Arrays.stream(clazz.methods).filter(m -> m.visibility == CppVisibility.PRIVATE).toList();
        List<CppMethod> protectedMethods = Arrays.stream(clazz.methods).filter(m -> m.visibility == CppVisibility.PROTECTED).toList();
        List<CppMethod> publicMethods = Arrays.stream(clazz.methods).filter(m -> m.visibility == CppVisibility.PUBLIC).toList();

        boolean hasPrivate = privateFields.size() + privateMethods.size() > 0;
        boolean hasProtected = protectedFields.size() + protectedMethods.size() > 0;
        boolean hasPublic = publicFields.size() + publicMethods.size() > 0;

        List<String> privateInits = null;
        List<String> protectedInits = null;
        List<String> publicInits = null;

        if(hasPrivate) {
            Pair<String, List<String>> transpiled = transpile(privateFields, privateConstructors, privateMethods, clazz.name);
            privateInits = transpiled.second;

            cpp.append("private:\n").append(transpiled.first);
            if(hasProtected || hasPublic) cpp.append("\n");
        }

        if(hasProtected) {
            Pair<String, List<String>> transpiled = transpile(protectedFields, protectedConstructors, protectedMethods, clazz.name);
            protectedInits = transpiled.second;

            cpp.append("protected:\n").append(transpiled.second);
            if(hasPublic) cpp.append("\n");
        }

        if(hasPublic) {
            Pair<String, List<String>> transpiled = transpile(publicFields, publicConstructors, publicMethods, clazz.name);
            publicInits = transpiled.second;

            cpp.append("public:\n").append(transpiled.first);
        }

        cpp.append("};\n");

        boolean hasPrivateInits = hasPrivate && !privateInits.isEmpty();
        boolean hasProtectedInits = hasProtected && !protectedInits.isEmpty();
        boolean hasPublicInits = hasPublic && !publicInits.isEmpty();

        if(hasPrivateInits || hasProtectedInits || hasPublicInits) cpp.append("\n");

        if(hasPrivateInits) {
            privateInits.forEach(init -> cpp.append(init).append("\n"));
            if(hasProtectedInits || hasPublicInits) cpp.append("\n");
        }

        if(hasProtectedInits) {
            protectedInits.forEach(init -> cpp.append(init).append("\n"));
            if(hasPublicInits) cpp.append("\n");
        }

        if(hasPublic) {
            publicInits.forEach(init -> cpp.append(init).append("\n"));
        }

        for(int i = 0; i < clazz.inits.length; i++) {
            if(i == 0) cpp.append("\n");

            String[] init = clazz.inits[i].split("\n");
            for(String line : init) cpp.append("// ").append(line).append("\n");
        }

        if(clazz.extras.length > 0) {
            cpp.append("\n");
            Arrays.stream(clazz.extras).forEach(e -> cpp.append(e).append("\n"));
        }

        return cpp.toString();
    }

    private static Pair<String, List<String>> transpile(List<CppField> fields, List<CppConstructor> constructors, List<CppMethod> methods, String className) {
        StringBuilder cpp = new StringBuilder();
        List<String> inits = new ArrayList<>();

        fields.forEach(f -> {
            Pair<String, String> field = transpile(className, f);
            cpp.append("\t").append(field.first).append("\n");
            if(!field.second.isEmpty()) inits.add(field.second);
        });

        if(!fields.isEmpty() && !constructors.isEmpty()) cpp.append("\n");

        constructors.forEach(c -> cpp.append("\t").append(transpile(c, className)).append("\n\n"));
        if((!fields.isEmpty() || !constructors.isEmpty()) && !methods.isEmpty()) cpp.append("\n");
        methods.forEach(m -> cpp.append("\t").append(transpile(m)).append("\n\n"));
        if(!methods.isEmpty()) cpp.delete(cpp.length() - 1, cpp.length());

        return new Pair<>(cpp.toString(), inits);
    }

    private static Pair<String, String> transpile(String className, CppField field) {
        StringBuilder cpp = new StringBuilder();

        String[] lines = field.javaSource.split("\n");
        for(String line : lines) {
            cpp.append("// ").append(line).append("\n\t");
        }

        if(field.isStatic && !field.isFinal) {
            cpp.append("static ").append(transpile(field.type)).append(" ").append(field.name).append(";");
            if(!field.defaultValue.isEmpty()) return new Pair<>(cpp.toString(), transpile(field.type) + " " + className + "::" + field.name +  " = " + field.defaultValue + ";");
            else return new Pair<>(cpp.toString(), "");
        }

        if(field.isStatic) cpp.append("static ");
        if(field.isFinal) cpp.append("const ");
        cpp.append(transpile(field.type)).append(" ").append(field.name);
        if(!field.defaultValue.isEmpty()) cpp.append(" = ").append(field.defaultValue);
        return new Pair<>(cpp.append(";").toString(), "");
    }

    private static String transpile(String type) {
        int arrayIndex = type.indexOf("[");
        String end = arrayIndex < 0 ? "" : type.substring(arrayIndex);
        switch (arrayIndex < 0 ? type : type.substring(0, arrayIndex)) {
            case "boolean" -> { return "bool" + end; }
            case "String" -> { return "std::string" + end; }
            case "Object" -> { return "std::any" + end; }
            case "long" -> { return "std::int64_t" + end; }
            default -> { return type; }
        }
    }

    private static String transpile(CppMethod method) {
        StringBuilder cpp = new StringBuilder();

        boolean override = false;

        String[] lines = method.javaSource.split("\n");
        for (String line : lines) {
            if(line.equals("@Override")) override = true;
            else cpp.append("// ").append(line).append("\n\t");
        }

        if(method.isAbstract) cpp.append("virtual ");

        cpp.append(transpile(method.returnType)).append(" ").append(method.name);
        return transpile(cpp, method, override);
    }

    private static String transpile(StringBuilder cpp, CppFunction func, boolean override) {
        cpp.append("(");
        Arrays.stream(func.args).forEach(a -> cpp.append(transpile(a)).append(", "));
        if(func.args.length > 0) cpp.delete(cpp.length() - 2, cpp.length());
        cpp.append(") ");
        if(override) cpp.append("override ");
        cpp.append("{\n");
        Arrays.stream(func.contents).forEach(c -> cpp.append("\t\t// ").append(c.replaceAll("\t", "    ")).append("\n"));
        return cpp.append("\t}").toString();
    }

    private static String transpile(CppConstructor constructor, String className) {
        StringBuilder cpp = new StringBuilder();
        String[] lines = constructor.javaSource.split("\n");
        for(int i = 0; i < lines.length - 1; i++) {
            cpp.append("// ").append(lines[i]).append("\n");
        }

        cpp.append(className);
        return transpile(cpp, constructor, false);
    }

    private static String transpile(CppArg arg) {
        StringBuilder cpp = new StringBuilder();
        cpp.append(transpile(arg.type)).append(" ").append(arg.name);
        if(!arg.defaultValue.isEmpty()) cpp.append(" = ").append(arg.defaultValue);
        return cpp.toString();
    }

}
