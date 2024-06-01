package com.vm.config;

public class AuthThreadLocal {
    public static ThreadLocal<String> currentUser = new ThreadLocal<>();
    public static ThreadLocal<String> currentRole = new ThreadLocal<>();
}
