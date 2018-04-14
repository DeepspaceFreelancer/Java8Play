package com.yvr;

import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class BasicTest {

    @Test(enabled = false)
    public void testMainFunction() throws Exception {
        Program.main(null);
        System.out.println("Hello Again!");
    }

    private final com.yvr.ch01.Exercises ch01 = new com.yvr.ch01.Exercises();
    private final com.yvr.ch02.Exercises ch02 = new com.yvr.ch02.Exercises();
    private final com.yvr.ch03.Exercises ch03 = new com.yvr.ch03.Exercises();
    private final com.yvr.ch06.Exercises ch06 = new com.yvr.ch06.Exercises();

    @Test
    public void ch01Ex02() {
        List<File> fileList = ch01.ch01Ex02_v1(".");
        fileList.forEach(System.out::println);
        fileList = ch01.ch01Ex02_v2(".");
        fileList.forEach(System.out::println);
    }

    @Test
    public void ch02Ex03() throws Exception {
        ch02.ch02Ex03_v1_StepExecutionOrderA("resources/text.txt");
        ch02.ch02Ex03_v1_StepExecutionOrderB("resources/text.txt");
    }

    @Test
    public void ch03Ex01() {
        ch03.ch03Ex01_v1();
        ch03.ch03Ex01_v2();
    }

    @Test
    public void ch06Lecture01_AtomicValues() {
        ch06.ch06_01_AtomicValues();
    }

    @Test
    public void ch06Ex10() throws Exception {
        ch06.ch06ex10_v1();
        ch06.ch06ex10_v2();
    }
}
