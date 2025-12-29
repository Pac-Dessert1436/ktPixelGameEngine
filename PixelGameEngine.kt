// o------------------------------------------------------------------------------o
// | Kotlin Port of olcPixelGameEngine v2.30                                      |
// | Original C++ version by javidx9 (OneLoneCoder)                               |
// | Ported to Kotlin by Pac-Dessert1436                                          |
// o------------------------------------------------------------------------------o
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO
import javax.swing.*
import kotlin.math.*

// o------------------------------------------------------------------------------o
// | Vi2d - Represents a 2D geometric vector of integer type                      |
// o------------------------------------------------------------------------------o
data class Vi2d(var x: Int = 0, var y: Int = 0) {
    constructor(other: Vi2d) : this(other.x, other.y)
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    val area: Int
        get() = x * y
    val mag2: Int
        get() = x * x + y * y
    val mag: Int
        get() = sqrt(mag2.toFloat()).toInt()

    fun norm(): Vi2d {
        return if (mag == 0) Vi2d(0, 0) else Vi2d(x / mag, y / mag)
    }

    fun perp(): Vi2d = Vi2d(-y, x)
    fun floor(): Vi2d = Vi2d(floor(x.toFloat()).toInt(), floor(y.toFloat()).toInt())
    fun ceil(): Vi2d = Vi2d(ceil(x.toFloat()).toInt(), ceil(y.toFloat()).toInt())

    fun max(other: Vi2d): Vi2d = Vi2d(max(x, other.x), max(y, other.y))
    fun min(other: Vi2d): Vi2d = Vi2d(min(x, other.x), min(y, other.y))

    fun dot(other: Vi2d): Int = x * other.x + y * other.y
    fun cross(other: Vi2d): Int = x * other.y - y * other.x

    fun cart(): Vf2d = Vf2d(cos(y.toFloat()) * x, sin(y.toFloat()) * x)
    fun polar(): Vf2d = Vf2d(mag.toFloat(), atan2(y.toFloat(), x.toFloat()))

    fun clamp(min: Vi2d, max: Vi2d): Vi2d = this.max(min).min(max)
    fun lerp(other: Vi2d, t: Float): Vf2d {
        return Vf2d(x * (1.0f - t) + other.x * t, y * (1.0f - t) + other.y * t)
    }

    companion object {
        fun dist(vec1: Vi2d, vec2: Vi2d): Int = (vec1 - vec2).mag
        fun dist2(vec1: Vi2d, vec2: Vi2d): Int = (vec1 - vec2).mag2
        fun taxiDist(vec1: Vi2d, vec2: Vi2d): Int = abs(vec1.x - vec2.x) + abs(vec1.y - vec2.y)
        fun chebDist(vec1: Vi2d, vec2: Vi2d): Int = max(abs(vec1.x - vec2.x), abs(vec1.y - vec2.y))
        fun angle(vec1: Vi2d, vec2: Vi2d): Int =
                atan2((vec1.y - vec2.y).toFloat(), (vec1.x - vec2.x).toFloat()).toInt()
    }

    override fun toString(): String = "($x, $y)"
    override fun equals(other: Any?): Boolean = other is Vi2d && x == other.x && y == other.y
    override fun hashCode(): Int = 31 * x + y

    operator fun plus(other: Vi2d): Vi2d = Vi2d(x + other.x, y + other.y)
    operator fun plus(scalar: Int): Vi2d = Vi2d(x + scalar, y + scalar)
    operator fun minus(other: Vi2d): Vi2d = Vi2d(x - other.x, y - other.y)
    operator fun minus(scalar: Int): Vi2d = Vi2d(x - scalar, y - scalar)
    operator fun times(other: Vi2d): Vi2d = Vi2d(x * other.x, y * other.y)
    operator fun times(scalar: Int): Vi2d = Vi2d(x * scalar, y * scalar)
    operator fun div(scalar: Int): Vi2d = Vi2d(x / scalar, y / scalar)

    operator fun plusAssign(other: Vi2d) {
        x += other.x
        y += other.y
    }
    operator fun minusAssign(other: Vi2d) {
        x -= other.x
        y -= other.y
    }
    operator fun timesAssign(scalar: Int) {
        x *= scalar
        y *= scalar
    }
    operator fun divAssign(scalar: Int) {
        x /= scalar
        y /= scalar
    }
}

// o------------------------------------------------------------------------------o
// | Vf2d - Represents a 2D geometric vector of floating-point type               |
// o------------------------------------------------------------------------------o
data class Vf2d(var x: Float = 0.0f, var y: Float = 0.0f) {
    constructor(other: Vf2d) : this(other.x, other.y)
    constructor(pair: Pair<Float, Float>) : this(pair.first, pair.second)
    constructor(intVec: Vi2d) : this(intVec.x.toFloat(), intVec.y.toFloat())

    val area: Float
        get() = x * y
    val mag2: Float
        get() = x * x + y * y
    val mag: Float
        get() = sqrt(mag2)

    fun norm(): Vf2d {
        return if (mag == 0.0f) Vf2d(0.0f, 0.0f) else Vf2d(x / mag, y / mag)
    }

    fun perp(): Vf2d = Vf2d(-y, x)
    fun floor(): Vf2d = Vf2d(floor(x), floor(y))
    fun ceil(): Vf2d = Vf2d(ceil(x), ceil(y))

    fun max(other: Vf2d): Vf2d = Vf2d(max(x, other.x), max(y, other.y))
    fun min(other: Vf2d): Vf2d = Vf2d(min(x, other.x), min(y, other.y))

    fun dot(other: Vf2d): Float = x * other.x + y * other.y
    fun cross(other: Vf2d): Float = x * other.y - y * other.x

    fun cart(): Vf2d = Vf2d(cos(y) * x, sin(y) * x)
    fun polar(): Vf2d = Vf2d(mag, atan2(y, x))

    fun clamp(min: Vf2d, max: Vf2d): Vf2d = this.max(min).min(max)
    fun lerp(other: Vf2d, t: Float): Vf2d {
        return Vf2d(x * (1.0f - t) + other.x * t, y * (1.0f - t) + other.y * t)
    }

    companion object {
        fun dist(vec1: Vf2d, vec2: Vf2d): Float = (vec1 - vec2).mag
        fun dist2(vec1: Vf2d, vec2: Vf2d): Float = (vec1 - vec2).mag2
        fun taxiDist(vec1: Vf2d, vec2: Vf2d): Float = abs(vec1.x - vec2.x) + abs(vec1.y - vec2.y)
        fun chebDist(vec1: Vf2d, vec2: Vf2d): Float =
                max(abs(vec1.x - vec2.x), abs(vec1.y - vec2.y))
        fun angle(vec1: Vf2d, vec2: Vf2d): Float = atan2(vec1.y - vec2.y, vec1.x - vec2.x)
    }

    override fun toString(): String = "($x, $y)"
    override fun equals(other: Any?): Boolean = other is Vf2d && x == other.x && y == other.y
    override fun hashCode(): Int = 31 * x.hashCode() + y.hashCode()

    operator fun plus(other: Vf2d): Vf2d = Vf2d(x + other.x, y + other.y)
    operator fun plus(scalar: Float): Vf2d = Vf2d(x + scalar, y + scalar)
    operator fun minus(other: Vf2d): Vf2d = Vf2d(x - other.x, y - other.y)
    operator fun minus(scalar: Float): Vf2d = Vf2d(x - scalar, y - scalar)
    operator fun times(other: Vf2d): Vf2d = Vf2d(x * other.x, y * other.y)
    operator fun times(scalar: Float): Vf2d = Vf2d(x * scalar, y * scalar)
    operator fun div(scalar: Float): Vf2d = Vf2d(x / scalar, y / scalar)

    operator fun plusAssign(other: Vf2d) {
        x += other.x
        y += other.y
    }
    operator fun minusAssign(other: Vf2d) {
        x -= other.x
        y -= other.y
    }
    operator fun timesAssign(scalar: Float) {
        x *= scalar
        y *= scalar
    }
    operator fun divAssign(scalar: Float) {
        x /= scalar
        y /= scalar
    }

    fun toInt(): Vi2d = Vi2d(x.toInt(), y.toInt())
}

// o-----------------------------------------------------------------------------------o
// | RectF - Represents a 2D rectangle of floating-point type, for collision detection |
// o-----------------------------------------------------------------------------------o
data class RectF(var x: Float, var y: Float, var width: Float, var height: Float) {
    constructor(pos: Vf2d, size: Vf2d) : this(pos.x, pos.y, size.x, size.y)
    constructor() : this(0.0f, 0.0f, 0.0f, 0.0f)

    val left: Float
        get() = x
    val right: Float
        get() = x + width
    val top: Float
        get() = y
    val bottom: Float
        get() = y + height

    val location: Vf2d
        get() = Vf2d(x, y)
    val size: Vf2d
        get() = Vf2d(width, height)
    val center: Vf2d
        get() = Vf2d(x + width * 0.5f, y + height * 0.5f)

    val isEmpty: Boolean
        get() = width <= 0f || height <= 0f

    fun contains(px: Float, py: Float): Boolean =
            px >= left && px < right && py >= top && py < bottom

    fun contains(p: Vf2d): Boolean = contains(p.x, p.y)
    fun contains(r: RectF): Boolean =
            r.left >= left && r.right <= right && r.top >= top && r.bottom <= bottom

    fun intersects(r: RectF): Boolean =
            left < r.right && right > r.left && top < r.bottom && bottom > r.top

    fun offset(dx: Float, dy: Float): RectF = RectF(x + dx, y + dy, width, height)

    fun offset(p: Vf2d): RectF = offset(p.x, p.y)

    fun inflate(dx: Float, dy: Float): RectF =
            RectF(x - dx, y - dy, width + dx * 2f, height + dy * 2f)

    companion object {
        fun intersect(a: RectF, b: RectF): RectF {
            val l = max(a.left, b.left)
            val t = max(a.top, b.top)
            val r = min(a.right, b.right)
            val bo = min(a.bottom, b.bottom)
            return if (r <= l || bo <= t) RectF() else RectF(l, t, r - l, bo - t)
        }

        fun union(a: RectF, b: RectF): RectF {
            val l = min(a.left, b.left)
            val t = min(a.top, b.top)
            val r = max(a.right, b.right)
            val bo = max(a.bottom, b.bottom)
            return RectF(l, t, r - l, bo - t)
        }

        fun toRectI(r: RectF): RectI =
                RectI(r.x.toInt(), r.y.toInt(), r.width.toInt(), r.height.toInt())
    }
}

// o------------------------------------------------------------------------------o
// | RectI - Represents a 2D rectangle of integer type, for collision detection   |
// o------------------------------------------------------------------------------o
data class RectI(var x: Int, var y: Int, var width: Int, var height: Int) {
    constructor(pos: Vi2d, size: Vi2d) : this(pos.x, pos.y, size.x, size.y)
    constructor() : this(0, 0, 0, 0)

    val left: Int
        get() = x
    val right: Int
        get() = x + width
    val top: Int
        get() = y
    val bottom: Int
        get() = y + height

    val location: Vi2d
        get() = Vi2d(x, y)
    val size: Vi2d
        get() = Vi2d(width, height)
    val center: Vi2d
        get() = Vi2d((x + width * 0.5f).toInt(), (y + height * 0.5f).toInt())

    val isEmpty: Boolean
        get() = width <= 0 || height <= 0

    fun contains(px: Int, py: Int): Boolean = px >= left && px < right && py >= top && py < bottom

    fun contains(p: Vi2d): Boolean = contains(p.x, p.y)
    fun contains(r: RectI): Boolean =
            r.left >= left && r.right <= right && r.top >= top && r.bottom <= bottom

    fun intersects(r: RectI): Boolean =
            left < r.right && right > r.left && top < r.bottom && bottom > r.top

    fun offset(dx: Int, dy: Int): RectI = RectI(x + dx, y + dy, width, height)

    fun offset(p: Vi2d): RectI = offset(p.x, p.y)

    fun inflate(dx: Int, dy: Int): RectI = RectI(x - dx, y - dy, width + dx * 2, height + dy * 2)

    companion object {
        fun intersect(a: RectI, b: RectI): RectI {
            val l = max(a.left, b.left)
            val t = max(a.top, b.top)
            val r = min(a.right, b.right)
            val bo = min(a.bottom, b.bottom)
            return if (r <= l || bo <= t) RectI() else RectI(l, t, r - l, bo - t)
        }

        fun union(a: RectI, b: RectI): RectI {
            val l = min(a.left, b.left)
            val t = min(a.top, b.top)
            val r = max(a.right, b.right)
            val bo = max(a.bottom, b.bottom)
            return RectI(l, t, r - l, bo - t)
        }

        fun toRectF(r: RectI): RectF =
                RectF(r.x.toFloat(), r.y.toFloat(), r.width.toFloat(), r.height.toFloat())
    }
}

// o-----------------------------------------------------------------------------o
// | Pixel - Represents a 32-Bit RGBA color                                      |
// o-----------------------------------------------------------------------------o
class Pixel(var r: Int = 0, var g: Int = 0, var b: Int = 0, var a: Int = 255) {
    constructor(
            n: Int
    ) : this((n shr 0) and 0xFF, (n shr 8) and 0xFF, (n shr 16) and 0xFF, (n shr 24) and 0xFF)

    enum class Mode {
        NORMAL,
        MASK,
        ALPHA,
        CUSTOM
    }

    val value: Int
        get() = (a shl 24) or (b shl 16) or (g shl 8) or r

    override fun equals(other: Any?): Boolean =
            other is Pixel && r == other.r && g == other.g && b == other.b && a == other.a
    override fun hashCode(): Int = value
    override fun toString(): String = "Pixel(r=$r, g=$g, b=$b, a=$a)"

    operator fun times(scalar: Float): Pixel =
            Pixel(
                    (r * scalar).toInt().coerceIn(0, 255),
                    (g * scalar).toInt().coerceIn(0, 255),
                    (b * scalar).toInt().coerceIn(0, 255),
                    a
            )
    operator fun div(scalar: Float): Pixel =
            Pixel(
                    (r / scalar).toInt().coerceIn(0, 255),
                    (g / scalar).toInt().coerceIn(0, 255),
                    (b / scalar).toInt().coerceIn(0, 255),
                    a
            )
    operator fun plus(other: Pixel): Pixel =
            Pixel(
                    (r + other.r).coerceIn(0, 255),
                    (g + other.g).coerceIn(0, 255),
                    (b + other.b).coerceIn(0, 255),
                    (a + other.a).coerceIn(0, 255)
            )
    operator fun minus(other: Pixel): Pixel =
            Pixel(
                    (r - other.r).coerceIn(0, 255),
                    (g - other.g).coerceIn(0, 255),
                    (b - other.b).coerceIn(0, 255),
                    (a - other.a).coerceIn(0, 255)
            )
    operator fun times(other: Pixel): Pixel =
            Pixel(
                    (r * other.r / 255).coerceIn(0, 255),
                    (g * other.g / 255).coerceIn(0, 255),
                    (b * other.b / 255).coerceIn(0, 255),
                    (a * other.a / 255).coerceIn(0, 255)
            )

    operator fun timesAssign(scalar: Float) {
        r = (r * scalar).toInt().coerceIn(0, 255)
        g = (g * scalar).toInt().coerceIn(0, 255)
        b = (b * scalar).toInt().coerceIn(0, 255)
    }
    operator fun divAssign(scalar: Float) {
        r = (r / scalar).toInt().coerceIn(0, 255)
        g = (g / scalar).toInt().coerceIn(0, 255)
        b = (b / scalar).toInt().coerceIn(0, 255)
    }
    operator fun plusAssign(other: Pixel) {
        r = (r + other.r).coerceIn(0, 255)
        g = (g + other.g).coerceIn(0, 255)
        b = (b + other.b).coerceIn(0, 255)
        a = (a + other.a).coerceIn(0, 255)
    }
    operator fun minusAssign(other: Pixel) {
        r = (r - other.r).coerceIn(0, 255)
        g = (g - other.g).coerceIn(0, 255)
        b = (b - other.b).coerceIn(0, 255)
        a = (a - other.a).coerceIn(0, 255)
    }
    operator fun timesAssign(other: Pixel) {
        r = (r * other.r / 255).coerceIn(0, 255)
        g = (g * other.g / 255).coerceIn(0, 255)
        b = (b * other.b / 255).coerceIn(0, 255)
        a = (a * other.a / 255).coerceIn(0, 255)
    }

    fun inv(): Pixel = Pixel(255 - r, 255 - g, 255 - b, a)

    fun toColor(): Color = Color(r, g, b, a)
}

// o------------------------------------------------------------------------------o
// | Presets - Predefined Pixel Constants                                         |
// o------------------------------------------------------------------------------o
object Presets {
    val NAVY = Pixel(0, 0, 117)
    val DARK_BLUE = Pixel(0, 0, 139)
    val BLUE = Pixel(0, 0, 255)
    val DARK_GREEN = Pixel(0, 139, 0)
    val DARK_CYAN = Pixel(0, 139, 139)
    val GREEN = Pixel(0, 255, 0)
    val CYAN = Pixel(0, 255, 255)
    val TEAL = Pixel(70, 153, 144)
    val MAROON = Pixel(128, 0, 0)
    val OLIVE = Pixel(128, 128, 0)
    val DARK_RED = Pixel(139, 0, 0)
    val DARK_MAGENTA = Pixel(139, 0, 139)
    val DARK_YELLOW = Pixel(139, 139, 0)
    val DARK_GREY = Pixel(139, 139, 139)
    val PURPLE = Pixel(145, 30, 180)
    val BROWN = Pixel(154, 99, 36)
    val GRAY = Pixel(169, 169, 169)
    val MINT = Pixel(170, 255, 195)
    val LIME = Pixel(191, 239, 69)
    val LAVENDER = Pixel(230, 190, 255)
    val ORANGE = Pixel(245, 130, 49)
    val PINK = Pixel(250, 190, 190)
    val RED = Pixel(255, 0, 0)
    val MAGENTA = Pixel(255, 0, 255)
    val APRICOT = Pixel(255, 216, 177)
    val BEIGE = Pixel(255, 250, 200)
    val SNOW = Pixel(255, 250, 250)
    val YELLOW = Pixel(255, 255, 0)
    val WHITE = Pixel(255, 255, 255)
    val BLACK = Pixel(0, 0, 0)
    val TANGERINE = Pixel(255, 165, 0)
}

// o-----------------------------------------------------------------------------o
// | Key - Represents keyboard keys                                              |
// o-----------------------------------------------------------------------------o
enum class Key {
    NONE,
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J,
    K,
    L,
    M,
    N,
    O,
    P,
    Q,
    R,
    S,
    T,
    U,
    V,
    W,
    X,
    Y,
    Z,
    K0,
    K1,
    K2,
    K3,
    K4,
    K5,
    K6,
    K7,
    K8,
    K9,
    F1,
    F2,
    F3,
    F4,
    F5,
    F6,
    F7,
    F8,
    F9,
    F10,
    F11,
    F12,
    UP,
    DOWN,
    LEFT,
    RIGHT,
    SPACE,
    TAB,
    SHIFT,
    CTRL,
    INS,
    DEL,
    HOME,
    END,
    PGUP,
    PGDN,
    BACK,
    ESCAPE,
    RETURN,
    ENTER,
    PAUSE,
    SCROLL,
    NP0,
    NP1,
    NP2,
    NP3,
    NP4,
    NP5,
    NP6,
    NP7,
    NP8,
    NP9,
    NP_MUL,
    NP_DIV,
    NP_ADD,
    NP_SUB,
    NP_DECIMAL,
    PERIOD,
    EQUALS,
    COMMA,
    MINUS,
    OEM_1,
    OEM_2,
    OEM_3,
    OEM_4,
    OEM_5,
    OEM_6,
    OEM_7,
    OEM_8,
    CAPS_LOCK,
    ENUM_END
}

// o-----------------------------------------------------------------------------o
// | Mouse - Represents mouse buttons                                            |
// o-----------------------------------------------------------------------------o
object Mouse {
    const val LEFT = 0
    const val RIGHT = 1
    const val MIDDLE = 2
}

// o-----------------------------------------------------------------------------o
// | HWButton - Represents the state of a hardware button                        |
// o-----------------------------------------------------------------------------o
class HWButton {
    var pressed: Boolean = false
    var released: Boolean = false
    var held: Boolean = false
}

// o-----------------------------------------------------------------------------o
// | Sprite - An image represented by a 2D array of Pixel                        |
// o-----------------------------------------------------------------------------o
class Sprite {
    var width: Int = 0
    var height: Int = 0
    private lateinit var pixels: Array<Array<Pixel>>

    constructor()
    constructor(imageFile: String) {
        loadFromFile(imageFile)
    }

    constructor(w: Int, h: Int) {
        width = w
        height = h
        pixels = Array(h) { Array(w) { Pixel() } }
    }

    fun loadFromFile(imageFile: String) {
        try {
            val image = ImageIO.read(File(imageFile))
            width = image.width
            height = image.height
            pixels =
                    Array(height) { y ->
                        Array(width) { x ->
                            val rgb = image.getRGB(x, y)
                            Pixel(
                                    (rgb shr 16) and 0xFF,
                                    (rgb shr 8) and 0xFF,
                                    rgb and 0xFF,
                                    (rgb shr 24) and 0xFF
                            )
                        }
                    }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load image: $imageFile", e)
        }
    }

    fun saveToFile(imageFile: String) {
        try {
            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixel = pixels[y][x]
                    val rgb = (pixel.a shl 24) or (pixel.r shl 16) or (pixel.g shl 8) or pixel.b
                    image.setRGB(x, y, rgb)
                }
            }
            ImageIO.write(image, "png", File(imageFile))
        } catch (e: IOException) {
            throw RuntimeException("Failed to save image: $imageFile", e)
        }
    }

    fun getPixel(x: Int, y: Int): Pixel {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Pixel()
        }
        return pixels[y][x]
    }

    fun setPixel(x: Int, y: Int, pixel: Pixel) {
        if (x in 0 until width && y in 0 until height) {
            pixels[y][x] = pixel
        }
    }

    fun clear(pixel: Pixel = Presets.BLACK) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y][x] = pixel
            }
        }
    }

    fun duplicate(): Sprite {
        val newSprite = Sprite(width, height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                newSprite.setPixel(x, y, getPixel(x, y))
            }
        }
        return newSprite
    }
}

// o-----------------------------------------------------------------------------o
// | PixelGameEngine - The main game engine class                                |
// o-----------------------------------------------------------------------------o
abstract class PixelGameEngine {
    // Constants
    private val mouseButtons = 5
    private val defaultAlpha = 255
    private val defaultPixel = defaultAlpha shl 24
    private val tabSizeInSpaces = 4
    private val maxVerts = 128

    enum class rcode {
        FAIL,
        OK,
        NO_FILE
    }
    val screenWidth: Int
        get() = screenSize.x
    val screenHeight: Int
        get() = screenSize.y

    // Core properties
    var appName: String = "ktPixelGameEngine"

    private var screenSize = Vi2d(256, 240)
    private var invScreenSize = Vf2d(1.0f / 256.0f, 1.0f / 240.0f)
    private var pixelSize = Vi2d(4, 4)
    private var screenPixelSize = Vi2d(4, 4)
    private var mousePos = Vi2d(0, 0)
    private var mouseWheelDelta = 0
    private var windowPos = Vi2d(0, 0)
    private var windowSize = Vi2d(0, 0)
    private var viewPos = Vi2d(0, 0)
    private var viewSize = Vi2d(0, 0)
    private var fullScreen = false
    private var pixel = Vf2d(1.0f, 1.0f)
    private var hasInputFocus = false
    private var hasMouseFocus = false
    private var enableVSYNC = false
    private var realWindowMode = false
    private var resizeRequested = false
    private var resizeRequestedSize = Vi2d(0, 0)
    private var frameTimer = 1.0f
    private var lastElapsed = 0.0f
    private var frameCount = 0
    private var suspendTextureTransfer = false
    private var targetLayer = 0u
    private var lastFPS = 0
    private var manualRenderEnable = false
    private var pixelCohesion = false

    // Input states
    private val keyNewState = BooleanArray(256) { false }
    private val keyOldState = BooleanArray(256) { false }
    private val keyboardState = Array(256) { HWButton() }

    private val mouseNewState = BooleanArray(mouseButtons) { false }
    private val mouseOldState = BooleanArray(mouseButtons) { false }
    private val mouseState = Array(mouseButtons) { HWButton() }

    // Drawing properties
    private var drawTarget: Sprite? = null
    private var pixelMode = Pixel.Mode.NORMAL
    private var blendFactor = 1.0f

    // Engine thread and state
    private var engineThread: Thread? = null
    private val atomActive = AtomicBoolean(false)

    // Swing components
    private lateinit var frame: JFrame
    private lateinit var canvas: Canvas
    private var buffer: BufferedImage? = null

    // Font
    private lateinit var fontSprite: Sprite

    // o-----------------------------------------------------------------------------o
    // | User Override Functions                                                     |
    // o-----------------------------------------------------------------------------o
    abstract fun onUserCreate(): Boolean
    abstract fun onUserUpdate(elapsedTime: Float): Boolean

    // o-----------------------------------------------------------------------------o
    // | Engine Control Functions                                                    |
    // o-----------------------------------------------------------------------------o
    fun start(
            screenWidth: Int,
            screenHeight: Int,
            pixelWidth: Int,
            pixelHeight: Int,
            fullScreen: Boolean = false,
            vsync: Boolean = false
    ) {
        screenSize = Vi2d(screenWidth, screenHeight)
        invScreenSize = Vf2d(1.0f / screenWidth, 1.0f / screenHeight)
        pixelSize = Vi2d(pixelWidth, pixelHeight)
        screenPixelSize = Vi2d(pixelWidth, pixelHeight)
        this.fullScreen = fullScreen
        enableVSYNC = vsync

        // Initialize Swing components
        frame = JFrame(appName)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(screenWidth * pixelWidth, screenHeight * pixelHeight)
        frame.setLocationRelativeTo(null)

        canvas = Canvas()
        canvas.setSize(screenWidth * pixelWidth, screenHeight * pixelHeight)
        canvas.background = Color.BLACK
        frame.add(canvas)

        // Initialize buffer
        buffer = BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB)

        // Initialize font
        constructFontSheet()

        // Setup input listeners
        setupInputListeners()

        // Make frame visible
        frame.isVisible = true
        canvas.isVisible = true

        // Start engine thread
        atomActive.set(true)
        engineThread = Thread { engineThread() }
        engineThread?.start()
    }

    fun shutdown() {
        atomActive.set(false)
        engineThread?.join()
        frame.dispose()
    }

    // o-----------------------------------------------------------------------------o
    // | Drawing Functions                                                           |
    // o-----------------------------------------------------------------------------o
    fun draw(x: Int, y: Int, pixel: Pixel = Presets.WHITE) {
        if (x < 0 || x >= screenSize.x || y < 0 || y >= screenSize.y) return

        val g = buffer?.graphics as? Graphics2D ?: return
        g.color = pixel.toColor()
        g.fillRect(x, y, 1, 1)
    }

    fun draw(pos: Vi2d, pixel: Pixel = Presets.WHITE) {
        draw(pos.x, pos.y, pixel)
    }

    fun draw(x: Float, y: Float, pixel: Pixel = Presets.WHITE) {
        draw(x.toInt(), y.toInt(), pixel)
    }

    fun draw(pos: Vf2d, pixel: Pixel = Presets.WHITE) {
        draw(pos.x.toInt(), pos.y.toInt(), pixel)
    }

    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int, pixel: Pixel = Presets.WHITE) {
        // Bresenham's line algorithm
        val temp = Vi2d(x1, y1)
        val dx = abs(x2 - x1)
        val dy = abs(y2 - y1)
        val sx = if (x1 < x2) 1 else -1
        val sy = if (y1 < y2) 1 else -1
        var err = dx - dy

        while (true) {
            draw(temp.x, temp.y, pixel)

            if (temp.x == x2 && temp.y == y2) break
            val e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                temp.x += sx
            }
            if (e2 < dx) {
                err += dx
                temp.y += sy
            }
        }
    }

    fun drawLine(pos1: Vi2d, pos2: Vi2d, pixel: Pixel = Presets.WHITE) {
        drawLine(pos1.x, pos1.y, pos2.x, pos2.y, pixel)
    }

    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, pixel: Pixel = Presets.WHITE) {
        drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), pixel)
    }

    fun drawLine(pos: Vf2d, pos2: Vf2d, pixel: Pixel = Presets.WHITE) {
        drawLine(pos.x.toInt(), pos.y.toInt(), pos2.x.toInt(), pos2.y.toInt(), pixel)
    }

    fun fillRect(x: Int, y: Int, w: Int, h: Int, pixel: Pixel = Presets.WHITE) {
        val g = buffer?.graphics as? Graphics2D ?: return
        g.color = pixel.toColor()
        g.fillRect(x, y, w, h)
    }

    fun fillRect(pos: Vf2d, size: Vf2d, pixel: Pixel = Presets.WHITE) {
        fillRect(pos.x.toInt(), pos.y.toInt(), size.x.toInt(), size.y.toInt(), pixel)
    }

    fun fillRect(x: Float, y: Float, w: Float, h: Float, pixel: Pixel = Presets.WHITE) {
        fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt(), pixel)
    }

    fun fillRect(pos: Vi2d, size: Vi2d, pixel: Pixel = Presets.WHITE) {
        fillRect(pos.x, pos.y, size.x, size.y, pixel)
    }

    fun drawRect(pos: Vi2d, size: Vi2d, pixel: Pixel = Presets.WHITE) {
        drawRect(pos.x, pos.y, size.x, size.y, pixel)
    }

    fun drawRect(pos: Vf2d, size: Vf2d, pixel: Pixel = Presets.WHITE) {
        drawRect(pos.x.toInt(), pos.y.toInt(), size.x.toInt(), size.y.toInt(), pixel)
    }

    fun drawRect(x: Int, y: Int, w: Int, h: Int, pixel: Pixel = Presets.WHITE) {
        drawLine(x, y, x + w - 1, y, pixel)
        drawLine(x + w - 1, y, x + w - 1, y + h - 1, pixel)
        drawLine(x + w - 1, y + h - 1, x, y + h - 1, pixel)
        drawLine(x, y + h - 1, x, y, pixel)
    }

    fun drawCircle(x: Int, y: Int, radius: Int, pixel: Pixel = Presets.WHITE) {
        val g = buffer?.graphics as? Graphics2D ?: return
        g.color = pixel.toColor()
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2)
    }

    fun drawCircle(pos: Vi2d, radius: Int, pixel: Pixel = Presets.WHITE) {
        drawCircle(pos.x, pos.y, radius, pixel)
    }

    fun drawCircle(x: Float, y: Float, radius: Float, pixel: Pixel = Presets.WHITE) {
        drawCircle(x.toInt(), y.toInt(), radius.toInt(), pixel)
    }

    fun drawCircle(pos: Vf2d, radius: Float, pixel: Pixel = Presets.WHITE) {
        drawCircle(pos.x.toInt(), pos.y.toInt(), radius.toInt(), pixel)
    }

    fun fillCircle(x: Int, y: Int, radius: Int, pixel: Pixel = Presets.WHITE) {
        val g = buffer?.graphics as? Graphics2D ?: return
        g.color = pixel.toColor()
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2)
    }

    fun fillCircle(pos: Vi2d, radius: Int, pixel: Pixel = Presets.WHITE) {
        fillCircle(pos.x, pos.y, radius, pixel)
    }

    fun fillCircle(x: Float, y: Float, radius: Float, pixel: Pixel = Presets.WHITE) {
        fillCircle(x.toInt(), y.toInt(), radius.toInt(), pixel)
    }

    fun fillCircle(pos: Vf2d, radius: Float, pixel: Pixel = Presets.WHITE) {
        fillCircle(pos.x.toInt(), pos.y.toInt(), radius.toInt(), pixel)
    }

    fun drawSprite(pos: Vf2d, sprite: Sprite, scaleX: Float = 1.0f, scaleY: Float = 1.0f) {
        drawSprite(pos.x.toInt(), pos.y.toInt(), sprite, scaleX, scaleY)
    }

    fun drawSprite(x: Float, y: Float, sprite: Sprite, scaleX: Float = 1.0f, scaleY: Float = 1.0f) {
        drawSprite(x.toInt(), y.toInt(), sprite, scaleX, scaleY)
    }

    fun drawSprite(x: Int, y: Int, sprite: Sprite, scaleX: Float = 1.0f, scaleY: Float = 1.0f) {
        val g = buffer?.graphics as? Graphics2D ?: return

        for (sy in 0 until sprite.height) {
            for (sx in 0 until sprite.width) {
                val pixel = sprite.getPixel(sx, sy)
                if (pixel.a > 0) {
                    val dx = (x + sx * scaleX).toInt()
                    val dy = (y + sy * scaleY).toInt()
                    g.color = pixel.toColor()
                    g.fillRect(dx, dy, scaleX.toInt(), scaleY.toInt())
                }
            }
        }
    }

    fun drawSprite(pos: Vi2d, sprite: Sprite, scaleX: Float = 1.0f, scaleY: Float = 1.0f) {
        drawSprite(pos.x, pos.y, sprite, scaleX, scaleY)
    }

    fun clear(pixel: Pixel = Presets.BLACK) {
        val g = buffer?.graphics as? Graphics2D ?: return
        g.color = pixel.toColor()
        g.fillRect(0, 0, screenSize.x, screenSize.y)
    }

    // o---------------------------------------------------------------------------o
    // | "Extended" Text Rendering Function, Using a System Font                   |
    // o---------------------------------------------------------------------------o
    fun drawStringX(
            fontName: String,
            x: Int,
            y: Int,
            text: String,
            pixel: Pixel = Presets.WHITE,
            scale: Float = 1.0f
    ) {
        if (scale < 1.0f) {
            throw IllegalArgumentException("Scale must be greater than or equal to 1.0f")
        }
        val g = buffer?.graphics as? Graphics2D ?: return
        g.color = pixel.toColor()
        g.font = Font(fontName, Font.PLAIN, (12 * scale).toInt())
        g.drawString(text, x, y + (12 * scale).toInt())
    }

    fun drawStringX(
            fontName: String,
            pos: Vi2d,
            text: String,
            pixel: Pixel = Presets.WHITE,
            scale: Float = 1.0f
    ) {
        drawStringX(fontName, pos.x, pos.y, text, pixel, scale)
    }

    fun drawStringX(
            fontName: String,
            x: Float,
            y: Float,
            text: String,
            pixel: Pixel = Presets.WHITE,
            scale: Float = 1.0f
    ) {
        drawStringX(fontName, x.toInt(), y.toInt(), text, pixel, scale)
    }

    fun drawStringX(
            fontName: String,
            pos: Vf2d,
            text: String,
            pixel: Pixel = Presets.WHITE,
            scale: Float = 1.0f
    ) {
        drawStringX(fontName, pos.x.toInt(), pos.y.toInt(), text, pixel, scale)
    }

    // o-----------------------------------------------------------------------------o
    // | Pixel Text Rendering Functions                                              |
    // o-----------------------------------------------------------------------------o
    private fun constructFontSheet() {
        val builder = StringBuilder()
        builder.append("?Q`0001oOch0o01o@F40o0<AGD4090LAGD<090@A7ch0?00O7Q`0600>00000000")
        builder.append("O000000nOT0063Qo4d8>?7a14Gno94AA4gno94AaOT0>o3`oO400o7QN00000400")
        builder.append("Of80001oOg<7O7moBGT7O7lABET024@aBEd714AiOdl717a_=TH013Q>00000000")
        builder.append("720D000V?V5oB3Q_HdUoE7a9@DdDE4A9@DmoE4A;Hg]oM4Aj8S4D84@`00000000")
        builder.append("OaPT1000Oa`^13P1@AI[?g`1@A=[OdAoHgljA4Ao?WlBA7l1710007l100000000")
        builder.append("ObM6000oOfMV?3QoBDD`O7a0BDDH@5A0BDD<@5A0BGeVO5ao@CQR?5Po00000000")
        builder.append("Oc``000?Ogij70PO2D]??0Ph2DUM@7i`2DTg@7lh2GUj?0TO0C1870T?00000000")
        builder.append("70<4001o?P<7?1QoHg43O;`h@GT0@:@LB@d0>:@hN@L0@?aoN@<0O7ao0000?000")
        builder.append("OcH0001SOglLA7mg24TnK7ln24US>0PL24U140PnOgl0>7QgOcH0K71S0000A000")
        builder.append("00H00000@Dm1S007@DUSg00?OdTnH7YhOfTL<7Yh@Cl0700?@Ah0300700000000")
        builder.append("<008001QL00ZA41a@6HnI<1i@FHLM81M@@0LG81?O`0nC?Y7?`0ZA7Y300080000")
        builder.append("O`082000Oh0827mo6>Hn?Wmo?6HnMb11MP08@C11H`08@FP0@@0004@000000000")
        builder.append("00P00001Oab00003OcKP0006@6=PMgl<@440MglH@000000`@000001P00000000")
        builder.append("Ob@8@@00Ob@8@Ga13R@8Mga172@8?PAo3R@827QoOb@820@0O`0007`0000007P0")
        builder.append("O`000P08Od400g`<3V=P0G`673IP0`@3>1`00P@6O`P00g`<O`000GP800000000")
        builder.append("?P9PL020O`<`N3R0@E4HC7b0@ET<ATB0@@l6C4B0O`H3N7b0?P01L3R000000020")
        fontSprite = Sprite(128, 48)
        var px = 0
        var py = 0
        for (b in 0..1023 step 4) {
            val sym1 = builder[b + 0].code - 48
            val sym2 = builder[b + 1].code - 48
            val sym3 = builder[b + 2].code - 48
            val sym4 = builder[b + 3].code - 48
            val r = sym1 shl 18 or (sym2 shl 12) or (sym3 shl 6) or sym4

            for (i in 0..23) {
                val k = if ((r and (1 shl i)) != 0) 255 else 0
                fontSprite.setPixel(px, py, Pixel(k, k, k))
                py++
                if (py == 48) {
                    px++
                    py = 0
                }
            }
        }
    }

    fun drawString(x: Int, y: Int, text: String, pixel: Pixel = Presets.WHITE, scale: Int = 1) {
        if (scale < 1) {
            throw IllegalArgumentException("Scale must be greater than or equal to 1")
        }
        var sx = 0
        var sy = 0
        for (c in text) {
            if (c == '\n') {
                sx = 0
                sy += 8 * scale
                continue
            }
            val ox: Int = (c.code - 32) % 16
            val oy: Int = (c.code - 32) / 16
            for (i in 0..7) {
                for (j in 0..7) {
                    if (fontSprite.getPixel(i + ox * 8, j + oy * 8).r > 0) {
                        for (iIs in 0 until scale) {
                            for (js in 0 until scale) {
                                draw(x + sx + (i * scale) + iIs, y + sy + (j * scale) + js, pixel)
                            }
                        }
                    }
                }
            }
            sx += 8 * scale
        }
    }

    fun drawString(pos: Vi2d, text: String, pixel: Pixel = Presets.WHITE, scale: Int = 1) {
        drawString(pos.x.toInt(), pos.y.toInt(), text, pixel, scale)
    }

    fun drawString(x: Float, y: Float, text: String, pixel: Pixel = Presets.WHITE, scale: Int = 1) {
        drawString(x.toInt(), y.toInt(), text, pixel, scale)
    }

    fun drawString(pos: Vf2d, text: String, pixel: Pixel = Presets.WHITE, scale: Float = 1.0f) {
        drawString(pos.x, pos.y, text, pixel, scale.toInt())
    }

    // o-----------------------------------------------------------------------------o
    // | Input Functions                                                             |
    // o-----------------------------------------------------------------------------o
    fun getKey(key: Key): HWButton {
        val keyCode = keyToKeyCode(key)
        return if (keyCode in keyboardState.indices) keyboardState[keyCode] else HWButton()
    }

    fun getMouse(button: Int): HWButton {
        return if (button in mouseState.indices) mouseState[button] else HWButton()
    }

    fun getMouseX(): Int = mousePos.x
    fun getMouseY(): Int = mousePos.y
    fun getMouseWheel(): Int = mouseWheelDelta

    // o-----------------------------------------------------------------------------o
    // | Utility Functions                                                           |
    // o-----------------------------------------------------------------------------o
    fun screenWidth(): Int = screenSize.x
    fun screenHeight(): Int = screenSize.y
    fun elapsedTime(): Float = lastElapsed

    // o-----------------------------------------------------------------------------o
    // | Internal Engine Functions                                                   |
    // o-----------------------------------------------------------------------------o
    private fun engineThread() {
        var lastTime = System.nanoTime()

        // Call user create function
        if (!onUserCreate()) {
            atomActive.set(false)
            return
        }

        while (atomActive.get()) {
            val currentTime = System.nanoTime()
            lastElapsed = (currentTime - lastTime) / 1_000_000_000.0f
            lastTime = currentTime

            // Update input states
            updateInput()

            // Call user update function
            if (!onUserUpdate(lastElapsed)) {
                atomActive.set(false)
                shutdown()
                break
            }

            // Render to screen and cap frame rate; approximately 60 FPS
            render()
            Thread.sleep(16)
        }
    }

    private fun updateInput() {
        // Update keyboard states
        for (i in keyboardState.indices) {
            val button = keyboardState[i]
            button.pressed = keyNewState[i] && !keyOldState[i]
            button.released = !keyNewState[i] && keyOldState[i]
            button.held = keyNewState[i]
            keyOldState[i] = keyNewState[i]
        }

        // Update mouse states
        for (i in mouseState.indices) {
            val button = mouseState[i]
            button.pressed = mouseNewState[i] && !mouseOldState[i]
            button.released = !mouseNewState[i] && mouseOldState[i]
            button.held = mouseNewState[i]
            mouseOldState[i] = mouseNewState[i]
        }

        // Reset mouse wheel delta
        mouseWheelDelta = 0
    }

    private fun render() {
        val g = canvas.graphics as? Graphics2D ?: return

        // Scale buffer to canvas size
        g.drawImage(buffer, 0, 0, canvas.width, canvas.height, null)
        g.dispose()
    }

    private fun setupInputListeners() {
        // Keyboard listener
        canvas.addKeyListener(
                object : KeyAdapter() {
                    override fun keyPressed(e: KeyEvent) {
                        keyNewState[e.keyCode] = true
                    }

                    override fun keyReleased(e: KeyEvent) {
                        keyNewState[e.keyCode] = false
                    }
                }
        )

        // Mouse listener
        canvas.addMouseListener(
                object : MouseAdapter() {
                    override fun mousePressed(e: MouseEvent) {
                        val button =
                                when (e.button) {
                                    MouseEvent.BUTTON1 -> Mouse.LEFT
                                    MouseEvent.BUTTON2 -> Mouse.MIDDLE
                                    MouseEvent.BUTTON3 -> Mouse.RIGHT
                                    else -> -1
                                }
                        if (button in 0 until mouseButtons) {
                            mouseNewState[button] = true
                        }
                    }

                    override fun mouseReleased(e: MouseEvent) {
                        val button =
                                when (e.button) {
                                    MouseEvent.BUTTON1 -> Mouse.LEFT
                                    MouseEvent.BUTTON2 -> Mouse.MIDDLE
                                    MouseEvent.BUTTON3 -> Mouse.RIGHT
                                    else -> -1
                                }
                        if (button in 0 until mouseButtons) {
                            mouseNewState[button] = false
                        }
                    }
                }
        )

        // Mouse motion listener
        canvas.addMouseMotionListener(
                object : MouseMotionAdapter() {
                    override fun mouseMoved(e: MouseEvent) {
                        val x = (e.x.toFloat() / canvas.width * screenSize.x).toInt()
                        val y = (e.y.toFloat() / canvas.height * screenSize.y).toInt()
                        mousePos = Vi2d(x, y)
                    }

                    override fun mouseDragged(e: MouseEvent) {
                        val x = (e.x.toFloat() / canvas.width * screenSize.x).toInt()
                        val y = (e.y.toFloat() / canvas.height * screenSize.y).toInt()
                        mousePos = Vi2d(x, y)
                    }
                }
        )

        // Mouse wheel listener
        canvas.addMouseWheelListener { e -> mouseWheelDelta = e.wheelRotation }

        // Focus listeners
        canvas.addFocusListener(
                object : FocusAdapter() {
                    override fun focusGained(e: FocusEvent) {
                        hasInputFocus = true
                        hasMouseFocus = true
                    }

                    override fun focusLost(e: FocusEvent) {
                        hasInputFocus = false
                        hasMouseFocus = false
                    }
                }
        )
    }

    private fun keyToKeyCode(key: Key): Int {
        return when (key) {
            Key.A -> KeyEvent.VK_A
            Key.B -> KeyEvent.VK_B
            Key.C -> KeyEvent.VK_C
            Key.D -> KeyEvent.VK_D
            Key.E -> KeyEvent.VK_E
            Key.F -> KeyEvent.VK_F
            Key.G -> KeyEvent.VK_G
            Key.H -> KeyEvent.VK_H
            Key.I -> KeyEvent.VK_I
            Key.J -> KeyEvent.VK_J
            Key.K -> KeyEvent.VK_K
            Key.L -> KeyEvent.VK_L
            Key.M -> KeyEvent.VK_M
            Key.N -> KeyEvent.VK_N
            Key.O -> KeyEvent.VK_O
            Key.P -> KeyEvent.VK_P
            Key.Q -> KeyEvent.VK_Q
            Key.R -> KeyEvent.VK_R
            Key.S -> KeyEvent.VK_S
            Key.T -> KeyEvent.VK_T
            Key.U -> KeyEvent.VK_U
            Key.V -> KeyEvent.VK_V
            Key.W -> KeyEvent.VK_W
            Key.X -> KeyEvent.VK_X
            Key.Y -> KeyEvent.VK_Y
            Key.Z -> KeyEvent.VK_Z
            Key.K0 -> KeyEvent.VK_0
            Key.K1 -> KeyEvent.VK_1
            Key.K2 -> KeyEvent.VK_2
            Key.K3 -> KeyEvent.VK_3
            Key.K4 -> KeyEvent.VK_4
            Key.K5 -> KeyEvent.VK_5
            Key.K6 -> KeyEvent.VK_6
            Key.K7 -> KeyEvent.VK_7
            Key.K8 -> KeyEvent.VK_8
            Key.K9 -> KeyEvent.VK_9
            Key.F1 -> KeyEvent.VK_F1
            Key.F2 -> KeyEvent.VK_F2
            Key.F3 -> KeyEvent.VK_F3
            Key.F4 -> KeyEvent.VK_F4
            Key.F5 -> KeyEvent.VK_F5
            Key.F6 -> KeyEvent.VK_F6
            Key.F7 -> KeyEvent.VK_F7
            Key.F8 -> KeyEvent.VK_F8
            Key.F9 -> KeyEvent.VK_F9
            Key.F10 -> KeyEvent.VK_F10
            Key.F11 -> KeyEvent.VK_F11
            Key.F12 -> KeyEvent.VK_F12
            Key.UP -> KeyEvent.VK_UP
            Key.DOWN -> KeyEvent.VK_DOWN
            Key.LEFT -> KeyEvent.VK_LEFT
            Key.RIGHT -> KeyEvent.VK_RIGHT
            Key.SPACE -> KeyEvent.VK_SPACE
            Key.TAB -> KeyEvent.VK_TAB
            Key.SHIFT -> KeyEvent.VK_SHIFT
            Key.CTRL -> KeyEvent.VK_CONTROL
            Key.INS -> KeyEvent.VK_INSERT
            Key.DEL -> KeyEvent.VK_DELETE
            Key.HOME -> KeyEvent.VK_HOME
            Key.END -> KeyEvent.VK_END
            Key.PGUP -> KeyEvent.VK_PAGE_UP
            Key.PGDN -> KeyEvent.VK_PAGE_DOWN
            Key.BACK -> KeyEvent.VK_BACK_SPACE
            Key.ESCAPE -> KeyEvent.VK_ESCAPE
            Key.RETURN -> KeyEvent.VK_ENTER
            Key.ENTER -> KeyEvent.VK_ENTER
            Key.PAUSE -> KeyEvent.VK_PAUSE
            Key.SCROLL -> KeyEvent.VK_SCROLL_LOCK
            Key.NP0 -> KeyEvent.VK_NUMPAD0
            Key.NP1 -> KeyEvent.VK_NUMPAD1
            Key.NP2 -> KeyEvent.VK_NUMPAD2
            Key.NP3 -> KeyEvent.VK_NUMPAD3
            Key.NP4 -> KeyEvent.VK_NUMPAD4
            Key.NP5 -> KeyEvent.VK_NUMPAD5
            Key.NP6 -> KeyEvent.VK_NUMPAD6
            Key.NP7 -> KeyEvent.VK_NUMPAD7
            Key.NP8 -> KeyEvent.VK_NUMPAD8
            Key.NP9 -> KeyEvent.VK_NUMPAD9
            Key.NP_MUL -> KeyEvent.VK_MULTIPLY
            Key.NP_DIV -> KeyEvent.VK_DIVIDE
            Key.NP_ADD -> KeyEvent.VK_ADD
            Key.NP_SUB -> KeyEvent.VK_SUBTRACT
            Key.NP_DECIMAL -> KeyEvent.VK_DECIMAL
            Key.PERIOD -> KeyEvent.VK_PERIOD
            Key.EQUALS -> KeyEvent.VK_EQUALS
            Key.COMMA -> KeyEvent.VK_COMMA
            Key.MINUS -> KeyEvent.VK_MINUS
            Key.OEM_1 -> KeyEvent.VK_SEMICOLON
            Key.OEM_2 -> KeyEvent.VK_SLASH
            Key.OEM_3 -> KeyEvent.VK_BACK_QUOTE
            Key.OEM_4 -> KeyEvent.VK_OPEN_BRACKET
            Key.OEM_5 -> KeyEvent.VK_BACK_SLASH
            Key.OEM_6 -> KeyEvent.VK_CLOSE_BRACKET
            Key.OEM_7 -> KeyEvent.VK_QUOTE
            Key.OEM_8 -> KeyEvent.VK_BACK_QUOTE
            Key.CAPS_LOCK -> KeyEvent.VK_CAPS_LOCK
            else -> 0
        }
    }
}

// o------------------------------------------------------------------------------o
// | GameMath - Math Utilities for the Game Engine                                |
// o------------------------------------------------------------------------------o
object GameMath {
    private val rng = java.util.Random()

    fun randomInt(min: Int, max: Int): Int {
        return rng.nextInt(min, max)
    }

    fun randomFloat(min: Float, max: Float): Float {
        return rng.nextFloat() * (max - min) + min
    }

    fun minkoDist(vec1: Vf2d, vec2: Vf2d, p: Float): Float {
        val absDiffX = abs(vec1.x - vec2.x)
        val absDiffY = abs(vec1.y - vec2.y)
        return when (p) {
            1.0f -> absDiffX + absDiffY
            2.0f -> sqrt(absDiffX * absDiffX + absDiffY * absDiffY)
            Float.MAX_VALUE -> max(absDiffX, absDiffY)
            else -> (absDiffX.pow(p) + absDiffY.pow(p)).pow(1.0f / p)
        }
    }

    fun minkoDist(vec1: Vi2d, vec2: Vi2d, p: Float): Int {
        val absDiffX = abs(vec1.x - vec2.x).toFloat()
        val absDiffY = abs(vec1.y - vec2.y).toFloat()
        val res =
                when (p) {
                    1.0f -> absDiffX + absDiffY
                    2.0f -> sqrt(absDiffX * absDiffX + absDiffY * absDiffY)
                    Float.MAX_VALUE -> max(absDiffX, absDiffY)
                    else -> (absDiffX.pow(p) + absDiffY.pow(p)).pow(1.0f / p)
                }
        return res.toInt()
    }

    fun jaccard(rectA: RectF, rectB: RectF): Float {
        if (rectA.isEmpty || rectB.isEmpty) return 0.0f
        val intersect = RectF.intersect(rectA, rectB)
        val overlapArea = if (intersect.isEmpty) 0.0f else intersect.width * intersect.height
        val areaA = rectA.width * rectA.height
        val areaB = rectB.width * rectB.height
        val unionArea = areaA + areaB - overlapArea
        return if (unionArea <= 0.0f) 0.0f else overlapArea / unionArea
    }

    fun jaccard(rectA: RectI, rectB: RectI): Float {
        if (rectA.isEmpty || rectB.isEmpty) return 0.0f
        val intersect = RectI.intersect(rectA, rectB)
        val overlapArea = if (intersect.isEmpty) 0 else intersect.width * intersect.height
        val areaA = rectA.width * rectA.height
        val areaB = rectB.width * rectB.height
        val unionArea = areaA + areaB - overlapArea
        return if (unionArea <= 0) 0.0f else overlapArea.toFloat() / unionArea
    }

    fun jaccardDist(rectA: RectF, rectB: RectF): Float {
        return 1.0f - jaccard(rectA, rectB)
    }

    fun jaccardDist(rectA: RectI, rectB: RectI): Float {
        return 1.0f - jaccard(rectA, rectB)
    }

    fun quadraticBezier(p0: Vf2d, p1: Vf2d, p2: Vf2d, t: Float): Vf2d {
        val u = 1.0F - t
        val x = p0.x * (u * u) + p1.x * (2.0f * u * t) + p2.x * (t * t)
        val y = p0.y * (u * u) + p1.y * (2.0f * u * t) + p2.y * (t * t)
        return Vf2d(x, y)
    }

    fun cubicBezier(p0: Vf2d, p1: Vf2d, p2: Vf2d, p3: Vf2d, t: Float): Vf2d {
        val u = 1.0F - t
        val u3 = u * u * u
        val t3 = t * t * t
        val x = p0.x * u3 + p1.x * (3.0f * u * u * t) + p2.x * (3.0f * u * t * t) + p3.x * t3
        val y = p0.y * u3 + p1.y * (3.0f * u * u * t) + p2.y * (3.0f * u * t * t) + p3.y * t3
        return Vf2d(x, y)
    }

    fun catmullRom(
            p0: Vf2d,
            p1: Vf2d,
            p2: Vf2d,
            p3: Vf2d,
            t: Float,
            normalized: Boolean = false
    ): Vf2d {
        val t2 = t * t
        val t3 = t2 * t
        val scale = if (normalized) 0.5F else 1.0F

        val coeff0 = (-t + 2 * t2 - t3) * scale
        val coeff1 = (2 - 5 * t2 + 3 * t3) * scale
        val coeff2 = (t + 4 * t2 - 3 * t3) * scale
        val coeff3 = (-t2 + t3) * scale

        val x = p0.x * coeff0 + p1.x * coeff1 + p2.x * coeff2 + p3.x * coeff3
        val y = p0.y * coeff0 + p1.y * coeff1 + p2.y * coeff2 + p3.y * coeff3
        return Vf2d(x, y)
    }

    fun hermite(p0: Vf2d, t0: Vf2d, p1: Vf2d, t1: Vf2d, t: Float): Vf2d {
        val t2 = t * t
        val t3 = t2 * t
        val h00 = 2.0f * t3 - 3.0f * t2 + 1.0f
        val h10 = t3 - 2.0f * t2 + t
        val h01 = -2.0f * t3 + 3.0f * t2
        val h11 = t3 - t2
        val x = p0.x * h00 + t0.x * h10 + p1.x * h01 + t1.x * h11
        val y = p0.y * h00 + t0.y * h10 + p1.y * h01 + t1.y * h11
        return Vf2d(x, y)
    }
}
