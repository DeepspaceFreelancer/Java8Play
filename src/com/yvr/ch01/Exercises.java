package com.yvr.ch01;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Exercises {

    //region Chapter 01 Exercise 02 ------------------------------------------------------------------------------------
    /*  Chapter 01 Exercise 02
        Using the listFiles(FileFilter) and isDirectory methods of the java.io.File class, write a method that returns all
        subdirectories of a given directory. Use a lambda expression instead of a FileFilter object. Repeat with a method expression.
    */
    public List<File> ch01Ex02_v1(final String directory_in) {
        List<File> result = Arrays.stream((new File(directory_in)).listFiles()).filter(x -> x.isDirectory()).collect(Collectors.toList());
        return result;
    }

    public List<File> ch01Ex02_v2(final String directory_in) {
        List<File> result = Arrays.stream((new File(directory_in)).listFiles(File::isDirectory)).collect(Collectors.toList());
        return result;
    }
    //endregion Chapter 01 Exercise 02 ---------------------------------------------------------------------------------


}
