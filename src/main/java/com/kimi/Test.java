package com.kimi;

public class Test {

    public static void main(String[] args) {
        RBTree<Integer,Integer> test = new RBTree<Integer, Integer>();
        test.insert(1,2);
        test.insert(2,3);
        test.insert(3,4);
        test.insert(9,4);
        test.insert(8,4);
        test.insert(7,4);
        test.insert(6,4);
        test.insert(4,4);
        System.out.println(test.toString());
    }
}
