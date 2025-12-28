# ktPixelGameEngine

## Description
ktPixelGameEngine is a lightweight 2D game engine ported from the original C++ [olc::PixelGameEngine](https://github.com/OneLoneCoder/olcPixelGameEngine) to Kotlin. Built on Java's AWT and Swing frameworks for graphics rendering, it provides a simple, intuitive interface for creating pixel-based games and applications. This project also pays tribute to [@DualBrain](https://github.com/DualBrain)'s [vbPixelGameEngine](https://github.com/DualBrain/vbPixelGameEngine).

As a personal project developed while learning Kotlin, the initial codebase received AI assistance, with core functionality and extensions implemented independently. The engine offers a comprehensive set of features for game development:

- **Simplified Game Loop**: Streamlined structure for game initialization, updates, and rendering
- **Pixel-Perfect Drawing**: Functions for lines, rectangles, circles, and polygons at the pixel level
- **RGBA Color System**: Robust `Pixel` class supporting alpha blending and color operations
- **Input Handling**: Comprehensive keyboard and mouse input tracking with state management
- **Text Rendering**: Multiple text rendering options, including the default pixel font via `drawString()`, and system fonts via `drawStringX()`
- **Geometry Utilities**: 2D vector operations, rectangle constructs, and collision detection
- **Advanced Mathematics**: `GameMath` object providing random number generation, distance metrics (Minkowski & Jaccard), and spline interpolation

As a current limitation, the engine lacks built-in audio playback functionality. Developers might easily integrate external libraries (`javax.sound.sampled`, JavaFX Media API etc.) to add comprehensive audio capabilities to their Kotlin projects.

## Getting Started

### Prerequisites
- **Java Development Kit (JDK)**: Version 11 or higher
- **Kotlin**: Version 1.6 or later (compatible with your JDK)
- **Integrated Development Environment (IDE)**: IntelliJ IDEA (recommended), Eclipse, or VS Code with the Kotlin plugin

### Cloning the Repository
Begin by cloning the repository to your local machine:
```bash
git clone https://github.com/Pac-Dessert1436/ktPixelGameEngine.git
cd ktPixelGameEngine
```

Once your Kotlin development environment is configured, you can directly include the `PixelGameEngine.kt` file in your project. The following section guides you through setting up the environment for popular IDEs:

### Setting Up the Development Environment

#### IntelliJ IDEA (Recommended)
1. Launch IntelliJ IDEA
2. Select **Open** and navigate to the cloned repository directory
3. IntelliJ will automatically detect the Kotlin project structure
4. Configure project settings:
   - Go to `File > Project Structure`
   - Verify the correct JDK is selected under "Project SDK"
5. Ensure the Kotlin plugin is enabled:
   - Navigate to `File > Settings > Plugins`
   - Search for "Kotlin" and confirm it's installed and enabled

#### Eclipse
1. Install the Kotlin plugin from the Eclipse Marketplace
2. Import the project:
   - Select `File > Import > Gradle > Existing Gradle Project`
   - Browse to the cloned repository directory
   - Follow the import wizard to complete the setup

#### Visual Studio Code
1. Install required extensions:
   - Kotlin extension (by fwcd)
   - Java Extension Pack
2. Open the cloned repository folder in VS Code
3. Install any additional recommended extensions when prompted

## Core Components

### Pixel Class
The `Pixel` class represents a 32-bit RGBA color with comprehensive functionality:

- **Flexible Construction**: Create colors from individual RGBA values or a single integer
- **Color Operations**: Support for arithmetic operations (+, -, *, /) with automatic 0-255 clamping
- **Utility Functions**: Color inversion (`inv()`), conversion to Java's `Color` type (`toColor()`)

**Example Usage:**
```kotlin
val red = Pixel(255, 0, 0)                     // Solid red
val semiTransparentBlue = Pixel(0, 0, 255, 128) // Semi-transparent blue
val combined = red + semiTransparentBlue        // Color blending
val darkened = combined * 0.5f                   // Color darkening
```

### Vector and Geometry
- **`Vi2d`**: 2D integer vector with full arithmetic operation support
- **`Vf2d`**: 2D floating-point vector for precise calculations
- **`RectF`/`RectI`**: Rectangle structures for collision detection and spatial management, with conversion utilities

### Input System
The engine provides robust input handling through:
- **`HWButton` Class**: Tracks input states (pressed, released, held) for precise control
- **Unified Input API**: Simple methods for keyboard (`getKey()`) and mouse (`getMouse()`) input

## Basic Usage Example

To create a simple game, create a file named `MyGame.kt` in the same directory as `PixelGameEngine.kt`:

```kotlin
class MyGame : PixelGameEngine() {
    private var playerPos = Vf2d()

    // Set the window title
    init {
        appName = "My Kotlin Pixel Game"
    }

    // Initialize game resources
    override fun onUserCreate(): Boolean {
        // Center player at start
        playerPos = Vf2d(screenWidth / 2.0f, screenHeight / 2.0f)
        return true
    }

    // Wrap position around screen edges
    private fun wrapPosition(pos: Vf2d) {
        when {
            pos.x < 0 -> pos.x = screenWidth.toFloat()
            pos.x > screenWidth -> pos.x = 0f
        }
        when {
            pos.y < 0 -> pos.y = screenHeight.toFloat()
            pos.y > screenHeight -> pos.y = 0f
        }
    }

    // Main game loop - update and render
    override fun onUserUpdate(elapsedTime: Float): Boolean {
        // Clear screen to blue background
        clear(Presets.BLUE)
        
        // Draw game text
        drawString(10, 10, "Hello, pixel world!", Presets.CYAN)
        drawString(10, 30, "Use arrow keys to move around", Presets.GRAY)
        
        // Draw player circle
        fillCircle(playerPos, 50f, Presets.MINT)

        // Handle player movement
        if (getKey(Key.LEFT).held) playerPos.x -= 50 * elapsedTime
        if (getKey(Key.RIGHT).held) playerPos.x += 50 * elapsedTime
        if (getKey(Key.UP).held) playerPos.y -= 50 * elapsedTime
        if (getKey(Key.DOWN).held) playerPos.y += 50 * elapsedTime
        
        // Apply screen wrapping
        wrapPosition(playerPos)
        
        // Exit game when ESC is pressed
        return !getKey(Key.ESCAPE).pressed
    }
}

fun main() {
    // Note: Unlike the C++ or VB.NET versions, the Kotlin implementation
    // does not require a separate `construct()` function. The game starts
    // immediately when `start()` is called.
    MyGame().start(800, 600, 1, 1)
}
```

### Compiling and Running

**Compile the game:**
```bash
kotlinc MyGame.kt PixelGameEngine.kt -include-runtime -d MyGame.jar
```

**Run the compiled game:**
```bash
java -jar MyGame.jar
# Alternative:
kotlin MyGame.jar
```

Enjoy creating your own pixel games!

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for full details.