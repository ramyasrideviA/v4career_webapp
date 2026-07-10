package com.learn.learningarea.config;

public class BranchContext {
    private static final ThreadLocal<String> currentBranch = new ThreadLocal<>();

    public static void setBranch(String branch) {
        currentBranch.set(branch);
    }

    public static String getBranch() {
        return currentBranch.get();
    }

    public static void clear() {
        currentBranch.remove();
    }
}