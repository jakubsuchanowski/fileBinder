package org.example;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static java.nio.file.Files.delete;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        String[] directories= {"HOME","DEV","TEST"};

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
        fileOperations();


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
    private static void fileOperations(){
        Path homePath=Paths.get("HOME");
        Path devPath=Paths.get("DEV");
        Path testPath=Paths.get("TEST");
        int allFiles=0;
        int testFiles=0;
        int devFiles=0;
        updateFileCount(allFiles,devFiles,testFiles);
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            homePath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE);
            System.out.println("Obserwowanie katalogu: "+ homePath);
            while (true){
                WatchKey key = watchService.take();
                for(WatchEvent<?> event : key.pollEvents()){
                    if(event.kind()==StandardWatchEventKinds.ENTRY_CREATE){
                        System.out.println("Do katalogu HOME dodano nowy plik: "+event.context());
                        String fileName=event.context().toString();
                        Path filePath=homePath.resolve(fileName);

                        if(getFileType(filePath).equals("jar")){
                            BasicFileAttributes attributes = Files.readAttributes(filePath,BasicFileAttributes.class);
                            FileTime creationTime=attributes.creationTime();
                            LocalDateTime creationDateTime = LocalDateTime.ofInstant(creationTime.toInstant(), ZoneId.systemDefault());
                            int hour = creationDateTime.getHour();
                            if(hour%2==0){
                                System.out.println("Parzysta godzina - przenoszenie do dev...");
                                moveFile(filePath, devPath);
                                devFiles++;
                            }else {
                                System.out.println("Nieparzysta godzina - przenoszenie do test...");
                                moveFile(filePath, testPath);
                                testFiles++;
                            }

                        }if(getFileType(filePath).equals("xml")){
                            System.out.println("Plik xml-przenoszenie do dev...");
                            moveFile(filePath, devPath);
                            devFiles++;
                        }
                        allFiles++;
                        updateFileCount(allFiles,devFiles,testFiles);
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }
        }catch (IOException | InterruptedException e){

        }
    }

    private static void updateFileCount(int allFile, int devFile, int testFile){
        Path countFile= Paths.get("HOME/count.txt");
        try(BufferedWriter writer= Files.newBufferedWriter(countFile,StandardOpenOption.CREATE,StandardOpenOption.WRITE)){
            writer.write("Number of all files: "+allFile);
            writer.newLine();
            writer.write("Number of files in dev's directory: "+devFile);
            writer.newLine();
            writer.write("Number of files in test's directory: "+testFile);
        }catch (IOException e){
            System.out.println("Błąd podczas nadpisywania pliku count.txt. ");
            e.printStackTrace();
        }
    }
    private static String getFileType(Path path){
        String fileName= path.getFileName().toString();
        int index = fileName.lastIndexOf(".");
        String extension = "";
        if(index>0){
            extension=fileName.substring(index+1);
        }
        else {
            extension="";
        }
        return extension;
    }


    private static void moveFile(Path sourcePath, Path targetPath) {
        try {
            Path destinationFile= targetPath.resolve(sourcePath.getFileName());
            Files.move(sourcePath, destinationFile, REPLACE_EXISTING);
            System.out.println("Plik przeniesiony do: " + targetPath);

        }catch (IOException e){
            System.out.println("Nie udało się przenieść pliku.");
            e.printStackTrace();
        }
    }

}