package de.geolykt.galimprofiler.uielements;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.geolykt.starloader.api.gui.Drawing;
import de.geolykt.starloader.api.gui.canvas.CanvasContext;

public class TextStrip implements CanvasContext {

    private final int width;
    private final int height;
    @NotNull
    private CharSequence text;
    @NotNull
    private final BitmapFont font = Drawing.getSpaceFont();

    public TextStrip(int width, int height, @NotNull String text) {
        this.width = width;
        this.height = height;
        this.text = text;
    }

    protected TextStrip(int width, int height) {
        if (this.getClass() == TextStrip.class) {
            throw new IllegalStateException("This constructor may only be used for subclasses.");
        }
        this.width = width;
        this.height = height;
        this.text = Objects.requireNonNull(this.getText(), ".getText() may not return null (this method needs to be overriden when using this constructor)");
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void render(@NotNull SpriteBatch surface, @NotNull Camera camera) {
        this.font.setColor(Color.WHITE);
        this.font.draw(surface, this.getText(), 10, this.getHeight() - 5);
    }

    @NotNull
    public CharSequence getText() {
        return this.text;
    }
}
