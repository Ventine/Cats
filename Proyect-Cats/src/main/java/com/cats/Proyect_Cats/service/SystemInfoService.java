package com.cats.Proyect_Cats.service;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemInfoService {

    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();

        try {
            InetAddress ip = InetAddress.getLocalHost();
            info.put("hostname", ip.getHostName());
            info.put("ipAddress", ip.getHostAddress());
        } catch (UnknownHostException e) {
            info.put("hostname", "unknown");
            info.put("ipAddress", "unknown");
        }

        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();

        info.put("osName", os.getName());
        info.put("osArch", os.getArch());
        info.put("osVersion", os.getVersion());
        info.put("availableProcessors", os.getAvailableProcessors());
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("javaVendor", System.getProperty("java.vendor"));
        info.put("jvmName", System.getProperty("java.vm.name"));

        info.put("totalMemoryMB", runtime.totalMemory() / (1024 * 1024));
        info.put("freeMemoryMB", runtime.freeMemory() / (1024 * 1024));
        info.put("maxMemoryMB", runtime.maxMemory() / (1024 * 1024));

        return info;
    }
}
