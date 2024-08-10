package de.geolykt.galimprofiler.uielements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import de.geolykt.galimprofiler.GalimProfiler;
import de.geolykt.galimprofiler.ProfilerData;
import de.geolykt.galimprofiler.ProfilerData.OutputType;
import de.geolykt.galimprofiler.ProfilerData.ProfilerMode;
import de.geolykt.starloader.api.Galimulator;
import de.geolykt.starloader.api.gui.BasicDialogBuilder;
import de.geolykt.starloader.api.gui.Drawing;
import de.geolykt.starloader.api.gui.canvas.Canvas;
import de.geolykt.starloader.api.gui.canvas.CanvasContext;
import de.geolykt.starloader.api.gui.canvas.CanvasManager;
import de.geolykt.starloader.api.gui.canvas.CanvasPosition;
import de.geolykt.starloader.api.gui.canvas.CanvasSettings;
import de.geolykt.starloader.api.gui.canvas.ChildObjectOrientation;
import de.geolykt.starloader.api.gui.canvas.prefab.CanvasCloseButton;
import de.geolykt.starloader.api.gui.canvas.prefab.RunnableCanvasButton;

import one.profiler.AsyncProfiler;
import one.profiler.Events;

public class ProfilerView {

    public static void showView(@NotNull GalimProfiler modInstance) {
        CanvasManager cmgr = CanvasManager.getInstance();
        CanvasSettings canvasSettings = new CanvasSettings(CanvasSettings.NEAR_SOLID_COLOR, "Profiler", Objects.requireNonNull(Color.BLUE));
        ProfilerView viewManager = new ProfilerView();
        RunnableCanvasButton setIntervalButton = new RunnableCanvasButton(viewManager::setInterval, "Set sampling interval", 200, 50);
        RunnableCanvasButton setProfilerMode = new RunnableCanvasButton(viewManager::setProfilerMode, "Set profiler mode", 200, 50);
        RunnableCanvasButton setLagspikeThreshold = new RunnableCanvasButton(viewManager::setLagspikeThreshold, "Set lagspike threshold", 200, 50);
        RunnableCanvasButton setProfilerEvent = new RunnableCanvasButton(viewManager::setProfilerEvent, "Set profiler event", 200, 50);

        @NotNull CanvasContext[] buttonStripUpper = {
                setProfilerMode,
                setIntervalButton,
                setLagspikeThreshold,
                setProfilerEvent
        };

        AtomicReference<RunnableCanvasButton> backtraceViewButtonRef = new AtomicReference<>();
        AtomicReference<RunnableCanvasButton> outputTypeButtonRef = new AtomicReference<>();

        RunnableCanvasButton backtraceViewButton = new RunnableCanvasButton(() -> {
            backtraceViewButtonRef.get().setText("Backtrace: " + (ProfilerData.backtrace = !ProfilerData.backtrace));
        }, "Backtrace: " + ProfilerData.backtrace, 200, 50);
        RunnableCanvasButton outputTypeButton = new RunnableCanvasButton(() -> {
            ProfilerData.output = OutputType.values()[(ProfilerData.output.ordinal() + 1) % OutputType.values().length];
            outputTypeButtonRef.get().setText("Output: " + ProfilerData.output);
        }, "Output: " + ProfilerData.output, 200, 50);

        backtraceViewButtonRef.lazySet(backtraceViewButton);
        outputTypeButtonRef.lazySet(outputTypeButton);

        @NotNull CanvasContext[] buttonStripCenter = {
                backtraceViewButton,
                outputTypeButton
        };

        RunnableCanvasButton startProfilerButton = new RunnableCanvasButton(viewManager::startProfiler, "Start profiler", 200, 50)
                .setButtonColor(Color.GREEN);
        RunnableCanvasButton openResultsButton = new RunnableCanvasButton(viewManager::openProfilerResults, "Open results", 200, 50)
                .setButtonColor(Color.BLUE);
        RunnableCanvasButton stopProfilerButton = new RunnableCanvasButton(viewManager::stopProfiler, "Stop profiler", 200, 50)
                .setButtonColor(Color.FIREBRICK);
        CanvasCloseButton closeButton = new CanvasCloseButton(200, 50)
                .setText("Close window");

        @NotNull CanvasContext[] buttonStripLower = {
                startProfilerButton,
                openResultsButton,
                stopProfilerButton,
                closeButton
        };

        TextStrip profilerStateInfo = new TextStrip(800, 30) {
            @Override
            @NotNull
            public CharSequence getText() {
                return "Profiler state: " + ProfilerData.state.toString();
            }
        };

        TextStrip profilerModeInfo = new TextStrip(800, 30) {
            @Override
            @NotNull
            public CharSequence getText() {
                return "Profiler mode: " + ProfilerData.mode.toString();
            }
        };

        TextStrip profilerSamplesInfo = new TextStrip(800, 30) {
            @Override
            @NotNull
            public CharSequence getText() {
                Canvas c = viewManager.canvas;
                if (c != null) {
                    Galimulator.runTaskOnNextFrame(c::markDirty);
                }
                return "Samples in this session: " + AsyncProfiler.getInstance().getSamples();
            }
        };

        TextStrip samplingIntervalInfo = new TextStrip(800, 30) {
            @Override
            @NotNull
            public CharSequence getText() {
                Canvas c = viewManager.canvas;
                if (c != null) {
                    Galimulator.runTaskOnNextFrame(c::markDirty);
                }
                return "Sampling interval: " + (ProfilerData.samplingInterval / 1_000_000F) + "ms";
            }
        };

        TextStrip lagspikeThreshold = new TextStrip(800, 30) {
            @Override
            @NotNull
            public CharSequence getText() {
                Canvas c = viewManager.canvas;
                if (c != null) {
                    Galimulator.runTaskOnNextFrame(c::markDirty);
                }
                return "Lagspike threshold: " + ProfilerData.lagspikeThreshold + "ms/u (~" + (1000 / ProfilerData.lagspikeThreshold) + " ups)";
            }
        };

        @NotNull Canvas[] children = {
                cmgr.childCanvas(cmgr.dummyContext(1, 480)),
                cmgr.childCanvas(lagspikeThreshold),
                cmgr.childCanvas(samplingIntervalInfo),
                cmgr.childCanvas(profilerSamplesInfo),
                cmgr.childCanvas(profilerModeInfo),
                cmgr.childCanvas(profilerStateInfo),
                cmgr.multiCanvas(cmgr.dummyContext(800, 50), CanvasSettings.CHILD_TRANSPARENT, ChildObjectOrientation.LEFT_TO_RIGHT, buttonStripLower),
                cmgr.multiCanvas(cmgr.dummyContext(800, 50), CanvasSettings.CHILD_TRANSPARENT, ChildObjectOrientation.LEFT_TO_RIGHT, buttonStripCenter),
                cmgr.multiCanvas(cmgr.dummyContext(800, 50), CanvasSettings.CHILD_TRANSPARENT, ChildObjectOrientation.LEFT_TO_RIGHT, buttonStripUpper),
                cmgr.newCanvas(new VersionStringContext(modInstance), CanvasSettings.CHILD_TRANSPARENT)
        };

        Canvas canvas = cmgr.multiCanvas(cmgr.dummyContext(800, 800), canvasSettings, ChildObjectOrientation.BOTTOM_TO_TOP, children);
        viewManager.canvas = canvas;
        closeButton.closesCanvas(canvas);
        cmgr.openCanvas(canvas, CanvasPosition.CENTER);
    }

    private Canvas canvas;

    private ProfilerView() { }

    private void openProfilerResults() {
        if (ProfilerData.state == ProfilerMode.STOPPED) {
            Drawing.toast("The profiler is stopped - nothing to open!");
            return;
        }

        // Available options defined in:
        // https://github.com/async-profiler/async-profiler/blame/9660e15b1e4cb38389aff56f7a9e4aea8decb779/src/arguments.cpp#L48
        try {
            String command;
            if (ProfilerData.output == OutputType.CALLGRAPH) {
                command = "tree";
            } else if (ProfilerData.output == OutputType.FLAMEGRAPH) {
                command = "flamegraph";
            } else {
                throw new IllegalStateException("Unknown output type: " + ProfilerData.output);
            }

            command += ",sig";

            if (ProfilerData.backtrace) {
                command += ",reverse";
            }

            this.writeAndOpenHtml(AsyncProfiler.getInstance().execute(command));
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            LoggerFactory.getLogger(ProfilerView.class).error("Unable to dump profiler results", e);
            Drawing.toast("Unable to dump profiler results. See logs for details.");
        }
    }

    private void setInterval() {
        if (ProfilerData.state != ProfilerMode.STOPPED) {
            Drawing.toast("Cannot set sampling interval: The profiler is already running.");
            return;
        }

        Canvas canvas = this.canvas;
        canvas.closeCanvas();
        Drawing.textInputBuilder("Set sampling interval", "", "Sampling interval (microseconds).\nWarning: Small values can impact performance drastically.")
            .addHook((input) -> {
                if (input != null) {
                    try {
                        ProfilerData.samplingInterval = Long.parseLong(input) * 1_000L;
                    } catch (NumberFormatException nfe) {
                        Drawing.toast("Invalid input. Value must be an integer number (i.e. no fractional numbers).");
                    }
                }
                CanvasManager.getInstance().openCanvas(canvas, CanvasPosition.CENTER);
            })
            .build();
    }

    private void setLagspikeThreshold() {
        if (ProfilerData.state != ProfilerMode.STOPPED) {
            Drawing.toast("Cannot set lagspike threshold: The profiler is already running.");
            return;
        }
        Canvas canvas = this.canvas;
        canvas.closeCanvas();
        Drawing.textInputBuilder("Set lagspike threshold", "Lagspike threshold, in milliseconds", "Set lagspike threshold, in milliseconds per update")
            .addHook((input) -> {
                if (input != null) {
                    try {
                        ProfilerData.lagspikeThreshold = Long.parseLong(input);
                    } catch (NumberFormatException nfe) {
                        Drawing.toast("Invalid input. Value must be an integer number (i.e. no fractional numbers).");
                    }
                }
                CanvasManager.getInstance().openCanvas(canvas, CanvasPosition.CENTER);
            })
            .build();
    }

    private void setProfilerEvent() {
        if (ProfilerData.state != ProfilerMode.STOPPED) {
            Drawing.toast("Cannot set profiler event: The profiler is already running.");
            return;
        }
        Canvas canvas = this.canvas;
        canvas.closeCanvas();
        new BasicDialogBuilder("Choose profiler event", "Choose profiler event")
            .setChoices(Arrays.asList(Events.ALLOC, Events.CPU, Events.CTIMER, Events.ITIMER, Events.LOCK, Events.WALL))
            .addCloseListener((cause, option) -> {
                if (option != null) {
                    ProfilerData.profilerEvent = option;
                }
                CanvasManager.getInstance().openCanvas(canvas, CanvasPosition.CENTER);
            })
            .buildAndShowNow();
    }

    private void setProfilerMode() {
        if (ProfilerData.state != ProfilerMode.STOPPED) {
            Drawing.toast("Cannot set profiler mode: The profiler is already running.");
            return;
        }
        Canvas canvas = this.canvas;
        canvas.closeCanvas();
        new BasicDialogBuilder("Choose profiler mode", "Choose profiler mode")
            .setChoices(Arrays.asList(ProfilerMode.values()).stream()
                    .filter(mode -> mode != ProfilerMode.STOPPED)
                    .map(ProfilerMode::name)
                    .collect(Collectors.toList()))
            .addCloseListener((cause, option) -> {
                if (option != null) {
                    ProfilerData.mode = ProfilerMode.valueOf(option);
                }
                CanvasManager.getInstance().openCanvas(canvas, CanvasPosition.CENTER);
            })
            .buildAndShowNow();
    }

    private void startProfiler() {
        if (ProfilerData.state != ProfilerMode.STOPPED) {
            Drawing.toast("Cannot start profiler: The profiler is already running.");
            return;
        }

        switch (ProfilerData.mode) {
        case CONTINUOUS:
            AsyncProfiler.getInstance().start(ProfilerData.profilerEvent, ProfilerData.samplingInterval);
            break;
        default:
            throw new IllegalStateException("Unknown profiler mode: " + ProfilerData.mode);
        }

        ProfilerData.state = ProfilerData.mode;
    }

    private void stopProfiler() {
        if (ProfilerData.state == ProfilerMode.STOPPED) {
            Drawing.toast("The profiler is stopped - nothing to stop!");
            return;
        }

        ProfilerData.state = ProfilerMode.STOPPED;
        AsyncProfiler.getInstance().stop();
    }

    private void writeAndOpenHtml(String code) throws IOException {
        if (code == null) {
            throw new IllegalArgumentException("Flamegraph output is null");
        }

        Path temporaryFile = Files.createTempFile("galimulator-profiler-result", ".html.tmp");
        Files.write(temporaryFile, code.getBytes(StandardCharsets.UTF_8));

        Gdx.net.openURI(temporaryFile.toUri().toString());
    }
}
