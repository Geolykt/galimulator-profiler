package de.geolykt.galimprofiler.uielements;

import org.jetbrains.annotations.NotNull;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.geolykt.galimprofiler.GalimProfiler;
import de.geolykt.starloader.api.gui.Drawing;
import de.geolykt.starloader.api.gui.canvas.CanvasContext;

import one.profiler.AsyncProfiler;

public class VersionStringContext implements CanvasContext {

    @NotNull
    private final GalimProfiler modInstance;

    public VersionStringContext(@NotNull GalimProfiler modInstance) {
        this.modInstance = modInstance;
    }

    @Override
    public int getHeight() {
        return 30;
    }

    @Override
    public int getWidth() {
        return 800;
    }

    @Override
    public void render(@NotNull SpriteBatch surface, @NotNull Camera camera) {
        BitmapFont font = Drawing.getSpaceFont();
        font.setColor(Color.WHITE); // Note: black * Color.WHITE = black
        float y = this.getHeight() - 5;
        GlyphLayout layout = font.draw(surface, "Async-profiler version: " + AsyncProfiler.getInstance().getVersion() + "; Mod version: " + this.modInstance.getDescription().getVersion(), 10, y);
        y -= layout.height;
    }
}
