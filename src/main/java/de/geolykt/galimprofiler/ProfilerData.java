package de.geolykt.galimprofiler;

import org.jetbrains.annotations.NotNull;

import one.profiler.Events;

public class ProfilerData {

    public static enum ProfilerMode {
        CONTINUOUS,
        STOPPED;
    }

    public static long samplingInterval = 10_000_000; // Default of 10ms
    public static long lagspikeThreshold = 200; // Default of 200 ms (<= 5 updates per second)
    @NotNull
    public static ProfilerMode mode = ProfilerMode.CONTINUOUS;
    @NotNull
    public static ProfilerMode state = ProfilerMode.STOPPED;
    @NotNull
    public static String profilerEvent = Events.CPU;
}
