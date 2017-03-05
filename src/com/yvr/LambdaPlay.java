package com.yvr;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LambdaPlay {

    /*
    Using the listFiles(FileFilter) and isDirectory methods of the java.io.File class, write a method that returns all
    subdirectories of a given directory. Use a lambda expression instead of a FileFilter object. Repeat with a method expression.
    */
    public List<File> exercice02(final String directory_in) {
        List<File> result = Arrays.stream((new File(directory_in)).listFiles()).filter(x -> x.isDirectory()).collect(Collectors.toList());
        result.forEach(a -> System.out.println(a));
        return result;
    }
}
