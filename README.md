# Windroid

Windroid is an Android app project intended to run a genuine Windows 7 guest in a custom virtual ThinkPad profile on a Galaxy S22 Ultra.

## Current status: 0.2.1 scaffold

The current app contains a Compose host-information screen and a conservative virtualization capability probe. It reports the Android version, host ABI, RAM, free storage, and whether common virtualization device nodes are visible.

**This is not yet a working Windows VM.** The QEMU x86 engine, PC firmware, virtual disk, Windows installer flow, storage/network devices, and display/input bridge have not been integrated. The app does not claim that Windows is booting.

## Guest architecture and rootless execution

Windows 7 was not released as an ARM64 edition. The selected guest is Windows 7 32-bit (x86), translated by QEMU TCG on the phone's ARM64 CPU. This is software emulation, not hardware virtualization, and performance may be very slow.

The target is an unrooted Android installation. Windroid must not assume that /dev/kvm, /dev/gunyah, or /dev/gzvm is usable just because a device node exists. Android app sandbox permissions and the host kernel determine whether hardware virtualization can actually be opened. An x86 guest cannot use ARM64 KVM as a direct same-architecture acceleration path in any case.

## Planned virtual ThinkPad

- Guest: Windows 7 32-bit (x86)
- Machine: emulated PC chipset with legacy BIOS (SeaBIOS)
- Initial resources: 2 virtual CPUs, 2 GB guest RAM, expandable 64 GB virtual disk
- Storage: IDE or SATA/AHCI initially, to use Windows 7 inbox drivers where possible
- Network: emulated Intel E1000 adapter, subject to driver support
- Display/input: basic VGA framebuffer, USB tablet, keyboard and mouse
- TPM 2.0 and Secure Boot: not required for Windows 7
- Graphics: basic display first; 3D acceleration is not promised

Use installation media that you are licensed to use. The app should verify downloaded images and never silently source modified Windows installers.

## Build

The GitHub Actions workflow builds a debug APK with JDK 17 and Gradle 8.9. Open the repository's Actions tab and download the windroid-debug-apk artifact after a successful run.

## Device testing required

The app must be installed and tested on the actual S22 Ultra. Emulator success alone does not validate QEMU execution, Android sandbox behavior, Windows boot, thermal behavior, memory pressure, or sustained performance on the phone.
