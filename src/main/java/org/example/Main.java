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
        DirectoryManager directoryManager = new DirectoryManager();
        directoryManager.setupDirectories();

        FileManager fileManager=new FileManager();
        fileManager.monitorHomeDirectory();
    }



}