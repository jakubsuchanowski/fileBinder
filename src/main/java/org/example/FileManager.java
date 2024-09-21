package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileManager {
    private final Path homePath= Paths.get("HOME");
    private final Path devPath=Paths.get("DEV");
    private final Path testPath=Paths.get("TEST");
    private int allFiles=0;
    private int testFiles=0;
    private int devFiles=0;

    public void monitorHomeDirectory(){
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            homePath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE);
            System.out.println("Obserwowanie katalogu: "+ homePath);
            while (true){
                WatchKey key = watchService.take();
                for(WatchEvent<?> event : key.pollEvents()){
                    if(event.kind()==StandardWatchEventKinds.ENTRY_CREATE){
                        handleNewFile(event.context().toString());
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
    private void handleNewFile(String fileName) throws IOException{
        Path filePath=homePath.resolve(fileName);
        String fileType=getFileType(filePath);

        if(fileType.equals("jar")){
            System.out.println("Plik jar");
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

        }if(fileType.equals("xml")){
            System.out.println("Plik xml-przenoszenie do dev...");
            moveFile(filePath, devPath);
            devFiles++;
        }
        allFiles++;
        updateFileCount(allFiles,devFiles,testFiles);
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
