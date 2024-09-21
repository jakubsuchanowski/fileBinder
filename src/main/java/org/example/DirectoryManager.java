package org.example;

import java.io.File;

public class DirectoryManager {
    private final String[] directories={"HOME", "DEV", "TEST"};

    public void setupDirectories(){
        for(String director : directories) {
            File directory = new File("./"+director);
            if (directory.exists()){
                deleteDirectory(directory);
                System.out.println("Usunięto stare dane.");
            }
            else {
                System.out.println("Usuwanie starych plików nie powiodło się.");
            }
            if(directory.mkdirs()){
                System.out.println("Katalog "+directory.getName() + " został utworzony.");
            }else {
                System.out.println("Utworzenie katalogu "+directory.getName()+" nie powiodło się.");
            }
        }
    }

    private static void deleteDirectory(File file){
        if(file.isDirectory()){
            File[] files=file.listFiles();
            if (files!=null) {
                for (File f : files) {
                    deleteDirectory(f);
                }
            }
        }
        file.delete();
    }

}
