# Windroid

Windroid targets a fast, rootless PC desktop experience on the Galaxy S22 Ultra. The performance-first design is inspired by the architecture used by DroidDeck, but Windroid is its own project.

## Two execution modes

### Fast mode (recommended)

- Run an ARM64 Linux userspace natively on Android.
- Provide a lightweight Linux desktop with a Vulkan-capable compositor.
- Use the Adreno 730 through a compatible Vulkan/Turnip driver stack where available.
- Run Windows applications through Wine or ARM64 Proton, with FEX for x86/x64 instruction translation where required.
- Avoid emulating an entire x86 PC for ordinary Windows applications.

This is a Windows application compatibility environment, **not a full Windows 7 operating system**. Some Windows programs will not work, and performance depends on their architecture, graphics API and driver compatibility.

DroidDeck documents support for Android devices with Adreno 730 or newer and describes an ARM64 Linux + gamescope + Proton/FEX architecture. See https://github.com/Droid-Deck/DroidDeck and its GPL-3.0 licence. Any reuse or derivative distribution must comply with the applicable licence and preserve required notices. Windroid must not imply affiliation with DroidDeck or Valve.

### Full Windows 7 mode (optional)

- Guest: Windows 7 32-bit (x86), using installation media the user is licensed to use.
- Emulator: QEMU TCG software CPU emulation on the ARM64 phone.
- Emulated machine: PC chipset, legacy BIOS (SeaBIOS), IDE/SATA disk, basic VGA and emulated network adapter.
- Initial profile: 2 virtual CPUs, 2 GB guest RAM, expandable 64 GB virtual disk.

This boots a genuine Windows 7 installation, but cross-architecture software emulation will be substantially slower than Fast mode. It is not expected to match DroidDeck performance.

## Current implementation status

The Android app currently contains a Compose host-information screen and a conservative host capability probe. It reports Android version, CPU ABI, RAM, free storage, and whether common virtualization device nodes are visible.

**Neither execution mode is integrated yet.** The ARM64 Linux runtime, compositor, Vulkan/Turnip stack, Wine/Proton/FEX components, QEMU x86 engine, firmware, virtual disk, Windows installer, and input/display bridges remain implementation work. The current app is a scaffold, not a working desktop or VM, and no APK has been verified as built.

## Rootless constraints

Windroid targets unrooted Android. It must not assume /dev/kvm, /dev/gunyah, or /dev/gzvm is usable merely because a device node exists. Android app sandbox permissions and kernel support determine access. Fast mode avoids full-PC CPU emulation, but still requires careful Android process, filesystem, graphics and audio integration.

## Build

The GitHub Actions workflow builds a debug APK with JDK 17 and Gradle 8.9. Open the repository's Actions tab and download the windroid-debug-apk artifact after a successful run.

## Device testing required

Test on the actual S22 Ultra. Verify process spawning, Android child-process limits, Vulkan extensions, thermal throttling, memory pressure, audio, controller/touch input and sustained performance. Never infer support solely from the GPU model.
