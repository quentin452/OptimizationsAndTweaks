# Building the Rust native libraries (CI)

The mod ships three Rust FFI crates as prebuilt native libraries, loaded at runtime
by `RustFFI` / `NativeLibraryLoader` from
`src/main/resources/assets/optimizationsandtweaks/natives/<os>/`:

| Crate                              | Linux                                     | Windows                              | macOS                                         |
| ---------------------------------- | ----------------------------------------- | ------------------------------------ | --------------------------------------------- |
| `optimizationsandtweaks_shared`    | `liboptimizationsandtweaks_shared.so`     | `optimizationsandtweaks_shared.dll`     | `liboptimizationsandtweaks_shared.dylib`     |
| `optimizationsandtweaks_blocksupdates` | `liboptimizationsandtweaks_blocksupdates.so` | `optimizationsandtweaks_blocksupdates.dll` | `liboptimizationsandtweaks_blocksupdates.dylib` |
| `optimizationsandtweaks_pathfinding` | `liboptimizationsandtweaks_pathfinding.so` | `optimizationsandtweaks_pathfinding.dll` | `liboptimizationsandtweaks_pathfinding.dylib` |

Locally, `build-all.py` cross-compiles all three OSes from one Linux box (mingw +
osxcross). In CI, `.github/workflows/rust-natives.yml` builds each OS on its own
native runner and produces byte-for-byte the same filenames. The macOS `.dylib` is a
universal binary (`x86_64` + `arm64`, merged with `lipo`).

## Run the workflow

It runs automatically on any push that touches a crate directory. To trigger it
manually (one command, via the GitHub CLI):

```bash
gh workflow run rust-natives.yml
```

Or in the browser: **Actions -> Build Rust natives -> Run workflow**.

## Get the artifacts and install them

Each job uploads its libraries as an artifact (`natives-linux`, `natives-windows`,
`natives-macos`). Download the latest run's artifacts:

```bash
gh run download -n natives-linux -n natives-windows -n natives-macos --dir /tmp/ot-natives
```

Then drop each file into its platform folder (overwriting the checked-in binaries):

```
src/main/resources/assets/optimizationsandtweaks/natives/linux/     <- *.so
src/main/resources/assets/optimizationsandtweaks/natives/windows/   <- *.dll
src/main/resources/assets/optimizationsandtweaks/natives/macos/     <- *.dylib
```

Filenames must match the table above exactly -- `NativeLibraryLoader` resolves them
by convention (`lib<crate>.so` / `<crate>.dll` / `lib<crate>.dylib`).

## Revalidate before committing new binaries

Native code is not covered by the Java/mixin tests, so exercise it in game:

1. Rebuild the mod jar so the refreshed natives are packed into resources:
   `./gradlew build`.
2. Launch a client (the pack TEST instance is ideal) and load into a world.
3. Confirm the libraries loaded -- the FML log should show, once per crate:
   `[OptimizationsAndTweaks] Successfully loaded native library: liboptimizationsandtweaks_pathfinding.so`
   (and `..._blocksupdates`, `..._shared`). `logs/optimizationsandtweaks/native.log`
   should contain `JNI_OnLoad: native library loaded`.
4. Verify pathfinding actually works: spawn a few mobs and confirm they navigate to
   the player and route around obstacles (the pathfinding crate is the async Rust
   rewrite of vanilla pathfinding). No native-crash entries in `native.log`, no
   `Failed to load native library` in the FML log.

Only commit the updated binaries once this passes on the platform(s) you rebuilt.

## Notes

- **Toolchains:** the per-crate `.cargo/config.toml` files pin the osxcross / mingw
  cross-linkers used *only* by `build-all.py`. CI overrides the Apple linker back to
  the native `clang` (see the `macos` job's `env`); the Linux and Windows jobs use
  the runner's default target, so those pins never apply.
- **leaktracer:** it is only *used* under `#[cfg(debug_assertions)]`, so release
  builds omit it entirely. The crate itself is pure Rust (only `backtrace`) and
  builds on all three OSes, so no feature-gating or `cfg` guard is needed -- the
  three green CI jobs are the proof.
