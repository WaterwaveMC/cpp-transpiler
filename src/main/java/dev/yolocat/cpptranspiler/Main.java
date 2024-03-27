package dev.yolocat.cpptranspiler;

import dev.yolocat.cpptranspiler.data.CppFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        boolean safeMode = true;

        File source = new File(args[0]);
        if(args.length < 2) {
            System.out.println("Target directory not specified");
            return;
        }
        File target = new File(args[1]);

        CppFile[] cpp = Transpiler.transpile(source);
        for (CppFile file : cpp) {
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
                        System.out.print("Continue? (y/N) ");

                        Scanner scanner = new Scanner(System.in);
                        String input = scanner.nextLine();

                        if(input.equalsIgnoreCase("y")) {
                            safeMode = false;
                        } else {
                            return;
                        }
                    }

                    if(!cppFile.delete()) {
                        System.out.println("Could not delete file " + cppFile.getAbsolutePath());
                        return;
                    }
                }

                if(!cppFile.createNewFile()) {
                    System.out.println("Could not create file " + cppFile.getAbsolutePath());
                }
            } catch(IOException e) {
                // TODO: Use better logging system

                // noinspection CallToPrintStackTrace
                e.printStackTrace();
                return;
            }

            try(FileWriter writer = new FileWriter(cppFile)) {
                writer.write(Transpiler.transpile(file));
            }
        }
    }

}