package de.geolykt.galimprofiler;

import org.jetbrains.annotations.NotNull;

import de.geolykt.galimprofiler.uielements.ProfilerView;
import de.geolykt.starloader.api.NamespacedKey;
import de.geolykt.starloader.api.gui.Keybind;

public class ProfilerViewKeybind implements Keybind {

    @NotNull
    private final GalimProfiler modInstance;

    public ProfilerViewKeybind(@NotNull GalimProfiler modInstance) {
        this.modInstance = modInstance;
    }

    @Override
    @NotNull
    public String getDescription() {
        return "Show profiler (Modded)";
    }

    @Override
    @NotNull
    public NamespacedKey getID() {
        return new NamespacedKey(this.modInstance, "keybind_profiler");
    }

    @Override
    public void executeAction() {
        ProfilerView.showView(this.modInstance);
    }
}
