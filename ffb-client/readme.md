# Native Windows Installer (MSI) Build Pipeline Documentation

This project configures the `jpackage-maven-plugin` to generate a native Windows MSI installer for the client application. To manage deployment lifecycles and ensure seamless application upgrades during local development or production updates, a custom Windows Installer XML (WiX) configuration template is utilized.

---

## Prerequisites: WiX Toolset System Requirement

The `jpackage` utility included in the OpenJDK framework does not natively compile Windows installer binaries. Instead, it acts as an orchestrator that requires an external installation of the **WiX Toolset (v3.x)** on the host operating system.

### Why It Is Required
When the Maven build reaches the `verify` phase, the `jpackage-maven-plugin` generates the necessary application image and layout structures. It then sweeps the local system environment variables to invoke the WiX compiler tools. If the WiX Toolset binaries are not accessible via the operating system's system environment path, the build pipeline will fail with an explicit `ConfigException` stating that the tools cannot be found.

### Installation & Configuration
1. Download and install the **WiX Toolset v3.x** binaries on the host Windows system.
2. Ensure that the installation directory containing the executables (specifically `candle.exe` and `light.exe`) is explicitly appended to the system **`PATH`** environment variable.
3. Verify the installation via a standard terminal command:
   ```cmd
   candle -version

## How the Custom `main.wxs` Template Integrates Into the Build

The project maintains a custom WiX source file located at `src/main/jpackage/resources/main.wxs`. This file dictates exactly how the installer registers, modifies, and upgrades the software layout on target machines.

The build pipeline manages this asset through a specific three-phase lifecycle:

### 1. Resource Redirection and Preprocessing
When running `mvn clean verify -Ppackage-installation-msi`, the `jpackage-maven-plugin` reads the `<resourceDir>` element pointing to `src/main/jpackage/resources`. This explicit mapping instructs the `jpackage` tool to skip its stock internal installer blueprints and copy our custom `main.wxs` configuration sheet into the intermediate build directory (`target/jpackage-temp/config/`).
The `mvn clean verify -Ppackage-installation-msi` command produces msi installer at `ffb-client\target\jpackage` path.

### 2. Macro Substitution via the POM Configuration
The `jpackage` engine functions as a preprocessor for the WiX file. It scans the XML content for predefined project macros formatted as `$(var.Jp...)` and text-substitutes them with the static configurations defined in the `pom.xml` plugin block:

| WiX Template Variable | Source Configuration Element in `pom.xml` | Evaluated Value Example |
| :--- | :--- | :--- |
| `$(var.JpAppName)` | `<name>` | `FantasyFootballClient` |
| `$(var.JpAppVendor)` | `<vendor>` | `pasha@programmer-underworlds.dev` |
| `$(var.JpProductUpgradeCode)` | `<winUpgradeUuid>` | `7e4b91c0-2b15-463a-ba62-f19876543210` |
| `$(var.JpAppVersion)` | `<appVersion>` | `1.0.0` |

### 3. Native Compilation and Linking
Once the variable mappings are filled, the plugin calls the underlying system installation of the WiX Toolset:
* **`candle.exe`** compiles the populated `main.wxs` text sheet into a binary intermediate object layer (`main.wixobj`).
* **`light.exe`** binds the object metadata, packages the fat application JAR alongside the customized runtime dependencies into a compressed archive wrapper (`Data.cab`), and links the combined footprint into the final `.msi` package.

## How the Custom `main.wxs` Template Integrates Into the Build

The project maintains a custom WiX source file located at `src/main/jpackage/resources/main.wxs`. This file dictates exactly how the installer registers, modifies, and upgrades the software layout on target machines.

The build pipeline manages this asset through a specific three-phase lifecycle:

### 1. Resource Redirection and Preprocessing
When running `mvn clean verify -Ppackage-installation-msi`, the `jpackage-maven-plugin` reads the `<resourceDir>` element pointing to `src/main/jpackage/resources`. This explicit mapping instructs the `jpackage` tool to skip its stock internal installer blueprints and copy our custom `main.wxs` configuration sheet into the intermediate build directory (`target/jpackage-temp/config/`).

### 2. Macro Substitution via the POM Configuration
The `jpackage` engine functions as a preprocessor for the WiX file. It scans the XML content for predefined project macros formatted as `$(var.Jp...)` and text-substitutes them with the static configurations defined in the `pom.xml` plugin block:

| WiX Template Variable | Source Configuration Element in `pom.xml` | Evaluated Value Example |
| :--- | :--- | :--- |
| `$(var.JpAppName)` | `<name>` | `FantasyFootballClient` |
| `$(var.JpAppVendor)` | `<vendor>` | `pasha@programmer-underworlds.dev` |
| `$(var.JpProductUpgradeCode)` | `<winUpgradeUuid>` | `7e4b91c0-2b15-463a-ba62-f19876543210` |
| `$(var.JpAppVersion)` | `<appVersion>` | `1.0.0` |

### 3. Native Compilation and Linking
Once the variable mappings are filled, the plugin calls the underlying system installation of the WiX Toolset:
* **`candle.exe`** compiles the populated `main.wxs` text sheet into a binary intermediate object layer (`main.wixobj`).
* **`light.exe`** binds the object metadata, packages the fat application JAR alongside the customized runtime dependencies into a compressed archive wrapper (`Data.cab`), and links the combined footprint into the final `.msi` package.

## Why a Custom `main.wxs` Template is Required

During local development and rapid deployment iterations, developers frequently rebuild and reinstall the client application under the same version footprint (e.g., version `1.0.0`). Standard, out-of-the-box `jpackage` behavior prevents seamless overwrites, resulting in deployment blockages unless a custom `main.wxs` template is explicitly implemented.

### The Problem with Stock `jpackage` Behavior
By default, the `jpackage` utility isolates builds by generating a brand-new, randomized Windows Installer **Product Code** GUID on every single compilation. However, the application version number (`<appVersion>`) usually remains static during active development cycles.

When a developer attempts to install a newly compiled MSI over an existing local environment, the native Windows Installer engine interprets the package as an entirely separate product trying to overwrite a locked destination path. Because the version strings match identically, the stock WiX logic rules treat the operation as a conflicting collision and halt the process, throwing an explicit error message:

> *“Another version of this product is already installed. Installation of this version cannot continue.”*

To clear this error without a custom template, developers are forced to manually open the Windows Control Panel, uninstall the older client build, and manually scrub residual system tracking structures prior to every single launch test.

### The Custom Template Solution
The project's custom `main.wxs` template resolves this iteration bottleneck by explicitly modifying the core upgrade rules inside the Windows Installer metadata sequence:

1. **Static Family Tracking via `UpgradeCode`**: The template binds the installer to a predictable `<winUpgradeUuid>` pulled directly from the `pom.xml`. This ensures Windows recognizes all sequential builds as members of the exact same application lineage, regardless of fluctuating internal Product Code GUIDs.
2. **Aggressive Force-Overwrite Logic**: The template modifies the stock `<UpgradeVersion>` criteria to use `IncludeMaximum="yes"` and `OnlyDetect="no"`. This explicit instruction forces the installer engine to actively scan the host registry for existing deployments that are less than *or exactly identical to* the current package runtime version.
3. **Pre-Execution Eviction (`RemoveExistingProducts`)**: By positioning the `RemoveExistingProducts` directive before `CostInitialize` in the `<InstallExecuteSequence>`, the installer engine identifies the previous installation footprint and cleanly purges its files and registry tracking records *before* it begins evaluating disk space or writing new application data.

Consequently, the custom `main.wxs` transforms the installer into an idempotent pipeline tool. Developers can execute `mvn clean verify -Ppackage-installation-msi` and launch the resulting MSI repeatedly; the installer will automatically tear down the stale local development footprint and deploy the fresh state smoothly without requiring manual intervention.