package com.yvr;

import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

public class BasicTest {

    private final LambdaPlay lambdaPlay = new LambdaPlay();

    @Test(enabled = false)
    public void testMainFunction() throws Exception {
        Program.main(null);
        System.out.println("Hello Again!");
    }

    @Test
    public void ch01Ex02() {
        List<File> fileList = lambdaPlay.ch01Ex02(".");
        fileList.forEach(x -> System.out.println(x));
    }

    @Test
    public void ch02Ex03() throws Exception {
        lambdaPlay.ch02Ex03_OrderA("src/com/yvr/LambdaPlay.java");
        lambdaPlay.ch02Ex03_OrderB("src/com/yvr/LambdaPlay.java");
    }

    @Test
    public void ch03Ex01() {
        lambdaPlay.ch03Ex01();
    }

    @Test
    public void ch06_01_AtomicValues() {
        lambdaPlay.ch06_01_AtomicValues();
    }
}
