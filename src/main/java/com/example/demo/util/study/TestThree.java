package com.example.demo.util.study;

public class TestThree {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.println(i*j);
                if(i*j==6){
                    System.out.println("返回");
                    return;
                }
            }
        }
    }
}
