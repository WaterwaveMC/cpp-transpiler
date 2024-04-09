package dev.yolocat.cpptranspiler;

import dev.yolocat.cpptranspiler.data.CppFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length < 2) err("Target directory not specified");

        File source = new File(args[0]);
        File target = new File(args[1]);

        Transpiler.printFiles = true;
        Transpiler.debug = true;

        CppFile[] cpp = Transpiler.transpile(source);
        writeFiles(cpp, target);
    }

    public static void err(String msg) {
        System.err.println(msg);
        System.exit(-1);
    }

    private static void writeFiles(CppFile[] files, File target) throws IOException {
        boolean safeMode = true;
        int done = 0;
        List<String> paths = new ArrayList<>();
        for (CppFile file : files) {
            paths.add(file.path + "/" + file.name + ".ixx");
            File parent = new File(target, file.path);

            // We don't care ¯\_(ツ)_/¯
            //noinspection ResultOfMethodCallIgnored
            parent.mkdirs();

            File cppFile = new File(parent, file.name + ".ixx");

            try {
                if(cppFile.exists()) {
                    if(safeMode) {
                        System.out.println("Target file already exists at path: " + cppFile.getAbsolutePath());
                        System.out.println("Continuing will destroy and replace this file and all other targeted files.");
                        System.out.print("y = Continue\nY = Continue for All\nn = Skip\nN = Skip All\nContinue? (N) ");

                        Scanner scanner = new Scanner(System.in);
                        String input = scanner.nextLine();

                        if(input.equals("Y")) {
                            safeMode = false;
                        } else if(input.equals("n")) {
                            continue;
                        } else if(!input.equals("y")) {
                            return;
                        }
                    }

                    if(!cppFile.delete()) err("Could not delete file " + cppFile.getAbsolutePath());
                }

                if(!cppFile.createNewFile()) err("Could not create file " + cppFile.getAbsolutePath());
            } catch(IOException e) {
                // TODO: Use better logging system

                // noinspection CallToPrintStackTrace
                e.printStackTrace();
                return;
            }

            try(FileWriter writer = new FileWriter(cppFile)) {
                done++;
                if(Transpiler.printFiles) System.out.println("Writing (" + done + "/" + Transpiler.totalFiles + "): " + cppFile.getName());
                writer.write(Transpiler.transpile(file));
            }
        }

        System.out.print("Create CMake project?\nNote: This will replace your current CMakeLists.txt file if you have one.\nContinue? (y/N) ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        if(!input.equalsIgnoreCase("y")) return;

        StringBuilder cmake = new StringBuilder();
        cmake
                .append("cmake_minimum_required(VERSION 3.28)\n")
                .append("\n")
                .append("if(POLICY CMP0025)\n")
                .append("\tcmake_policy(SET CMP0025 NEW)\n")
                .append("endif()\n")
                .append("\n")
                .append("project(change_me CXX)\n")
                .append("\n")
                .append("set(CMAKE_CXX_STANDARD 20)\n")
                .append("set(CMAKE_CXX_STANDARD_REQUIRED YES)\n")
                .append("\n")
                .append("add_library(change_me_too)\n")
                .append("target_sources(change_me_too PUBLIC\n")
                .append("\tFILE_SET CXX_MODULES FILES\n");

        for(String path : paths) cmake.append("\t\t").append(path).append("\n");

        cmake
                .append(")\n")
                .append("\n")
                .append("add_executable(${PROJECT_NAME} main.cpp)\n")
                .append("target_link_libraries(${PROJECT_NAME} change_me_too)");

        File cmakeFile = new File(target, "CMakeLists.txt");
        if(cmakeFile.exists()) cmakeFile.delete();
        cmakeFile.createNewFile();
        try(FileWriter writer = new FileWriter(cmakeFile)) {
            writer.write(cmake.toString());
        }
    }

}