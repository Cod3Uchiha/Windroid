# Windroid

Windroid is an Android app project intended to run a genuine Windows 11 ARM64 guest in a custom virtual ThinkPad profile on a Galaxy S22 Ultra.

## Current status: 0.2.0 scaffold

The current app contains a Compose host-information screen and a conservative virtualization capability probe. It reports the Android version, host ABI, RAM, free storage, and whether common virtualization device nodes are visible.

**This is not yet a working Windows VM.** The QEMU engine, UEFI firmware, virtual disk, Windows installer flow, virtual TPM, network/storage drivers, and display/input bridge have not been integrated. The app intentionally does not show a fake Start button or claim that Windows is booting.

## Rootless execution model

The target is an unrooted Android installation. Windroid must not assume that /dev/kvm, /dev/gunyah, or /dev/gzvm is usable just because a device node exists. Android app sandbox permissions and the host kernel determine whether hardware virtualization can actually be opened.

For a stock Snapdragon 8 Gen 1 phone, the planned compatibility path is ARM64 QEMU TCG software emulation. This avoids requiring root but is substantially slower than hardware-assisted virtualization. A host capability probe is not proof that acceleration is available.

The app must package or load the emulator in a way compatible with the Android version and target SDK; simply downloading an executable and trying to launch it is not a reliable Android 16 strategy.

## Planned virtual machine

- Guest: Windows 11 ARM64 (user-provided or official Microsoft installation media)
- Machine: ARM64 virt machine with UEFI
- Initial resources: 4 vCPUs, 4 GB guest RAM, expandable 64 GB virtual disk
- Devices: virtual display, USB tablet, keyboard, virtio storage and networking where Windows drivers are available
- TPM 2.0 and Secure Boot: only after compatible firmware and TPM integration are implemented and validated
- Graphics: basic framebuffer first; 3D acceleration is optional and must be capability-tested

## Build

The GitHub Actions workflow builds a debug APK with JDK 17 and Gradle 8.9. Open the repository's Actions tab and download the windroid-debug-apk artifact after a successful run.

## Device testing required

The app must be installed and tested on the actual S22 Ultra. Emulator success alone does not validate QEMU execution, Android sandbox behavior, Windows boot, thermal behavior, memory pressure, or sustained performance on the phone.
