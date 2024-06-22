# galimulator-profiler

> [!IMPORTANT]
> As AsyncProfiler is only available for Linux and MacOS, this mod will refuse
> to work under Windows. You can still use certain profilers like VisualVM
> for your profiling needs under Windows though.

A mod for Galimulator that integrates a user-friendly interface for AsyncProfiler
into Galimulator. The interface can be opened using `p`, overriding the rather
primitive built-in profiler of vanilla Galimulator.

There isn't much to talk about. The core functionality of this mod can also be
obtained by plainly using AsyncProfiler manually. This mod just repackages it
in a way where it is nicer to use. The idea will be to add more functionality
to better be able to debug lagspikes by integrating it with the core tick
loop - but at the moment that is not the case.
