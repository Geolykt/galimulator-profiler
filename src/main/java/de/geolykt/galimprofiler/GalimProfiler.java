package de.geolykt.galimprofiler;

import com.badlogic.gdx.Input.Keys;

import de.geolykt.starloader.api.NamespacedKey;
import de.geolykt.starloader.api.event.EventHandler;
import de.geolykt.starloader.api.event.EventManager;
import de.geolykt.starloader.api.event.Listener;
import de.geolykt.starloader.api.event.lifecycle.ApplicationStartedEvent;
import de.geolykt.starloader.api.gui.KeystrokeInputHandler;
import de.geolykt.starloader.mod.Extension;

import one.profiler.AsyncProfiler;

public class GalimProfiler extends Extension {

    @Override
    public void initialize() {
        try {
            this.getLogger().info("Using AsyncProfiler version {}", AsyncProfiler.getInstance().getVersion());
        } catch (Exception | LinkageError e) {
            this.getLogger().error("Unable to start AsyncProfiler. This mod cannot start up.", e);
            return;
        }

        EventManager.registerListener(new Listener() {
            @EventHandler
            public void onAppStarted(ApplicationStartedEvent evt) {
                KeystrokeInputHandler.getInstance().unregisterKeybind(NamespacedKey.fromString("StarloaderAPI", "keybind_open_profiler"));
                KeystrokeInputHandler.getInstance().registerKeybind(new ProfilerViewKeybind(GalimProfiler.this), Keys.P);
            }
        });
    }
}
