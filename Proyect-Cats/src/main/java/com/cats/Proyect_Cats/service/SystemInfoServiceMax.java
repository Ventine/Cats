package com.cats.Proyect_Cats.service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class SystemInfoServiceMax {

    private final SystemInfo si = new SystemInfo();

    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();

        // Sistema operativo
        OperatingSystem os = si.getOperatingSystem();
        info.put("osFamily", os.getFamily());
        info.put("osVersion", os.getVersionInfo().toString());

        // CPU
        CentralProcessor cpu = si.getHardware().getProcessor();
        info.put("cpuModel", cpu.getProcessorIdentifier().getName());
        info.put("cpuPhysicalCores", cpu.getPhysicalProcessorCount());
        info.put("cpuLogicalCores", cpu.getLogicalProcessorCount());

        // Memoria RAM
        GlobalMemory memory = si.getHardware().getMemory();
        info.put("memoryTotalMB", memory.getTotal() / (1024 * 1024));
        info.put("memoryAvailableMB", memory.getAvailable() / (1024 * 1024));

        // Discos
        List<HWDiskStore> diskStores = si.getHardware().getDiskStores();
        for (int i = 0; i < diskStores.size(); i++) {
            HWDiskStore disk = diskStores.get(i);
            info.put("disk" + i + "_name", disk.getModel());
            info.put("disk" + i + "_sizeGB", disk.getSize() / (1024 * 1024 * 1024));
        }

        // Interfaces de red
        List<NetworkIF> networks = si.getHardware().getNetworkIFs();
        for (int i = 0; i < networks.size(); i++) {
            NetworkIF net = networks.get(i);
            info.put("network" + i + "_name", net.getName());
            info.put("network" + i + "_ipv4", String.join(",", net.getIPv4addr()));
            info.put("network" + i + "_mac", net.getMacaddr());
        }

        // Batería (si existe)
        si.getHardware().getPowerSources().forEach(b -> {
            info.put("battery_name", b.getName());
            info.put("battery_capacity", b.getRemainingCapacityPercent() * 100);
        });

        return info;
    }
}
