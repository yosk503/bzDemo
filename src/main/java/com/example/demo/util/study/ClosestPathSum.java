package com.example.demo.util.study;

import java.util.ArrayList;
import java.util.List;

class TreeNode {
    double val;
    TreeNode left;
    TreeNode right;

    TreeNode(double val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }
}

public class ClosestPathSum {
    static double closestSum = Double.MAX_VALUE;
    static List<Double> closestPath = new ArrayList<>();

    public static List<Double> findClosestPath(double[] dataArray) {
        if (dataArray == null || dataArray.length == 0) {
            return closestPath;
        }
        TreeNode root = buildBinaryTree(dataArray, 0, dataArray.length - 1);
        List<Double> currentPath = new ArrayList<>();
        dfs(root, 0, currentPath);
        return closestPath;
    }

    private static TreeNode buildBinaryTree(double[] dataArray, int start, int end) {
        if (start > end) {
            return null;
        }
        int mid = (start + end) / 2;
        TreeNode root = new TreeNode(dataArray[mid]);
        root.left = buildBinaryTree(dataArray, start, mid - 1);
        root.right = buildBinaryTree(dataArray, mid + 1, end);
        return root;
    }

    private static void dfs(TreeNode node, double currentSum, List<Double> currentPath) {
        if (node == null) {
            return;
        }
        currentSum += node.val;
        currentPath.add(node.val);

        if (Math.abs(currentSum - 100) < Math.abs(closestSum - 100)) {
            closestSum = currentSum;
            closestPath = new ArrayList<>(currentPath);
        }

        dfs(node.left, currentSum, currentPath);
        dfs(node.right, currentSum, currentPath);

        currentPath.remove(currentPath.size() - 1);
    }

    public static void main(String[] args) {
        double[] dataArray = {13.16, 27.58, 54.53, 54.35, 40.20, 9.70, 23.40, 11.80};
        List<Double> closestPath = findClosestPath(dataArray);
        System.out.println("最接近100的路径和为: " + closestSum);
        System.out.println("路径为: " + closestPath);
    }
}