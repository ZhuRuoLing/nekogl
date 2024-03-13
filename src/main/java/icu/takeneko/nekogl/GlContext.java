package icu.takeneko.nekogl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.Locale;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GlContext {

    private final Thread renderThread;
    private long window;

    private volatile int framebufferWidth = 0;
    private volatile int framebufferHeight = 0;
    private volatile int windowWidth = 0;
    private volatile int windowHeight = 0;
    private volatile float scale = 1.0f;

    public GlContext(Thread renderThread) {
        this.renderThread = renderThread;
    }

    void init(String windowTitle, int windowWidth, int windowHeight) {
        if (windowTitle == null) {
            windowTitle = this.getClass().getName();
        }
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1);

        window = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the window");
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            //System.out.printf("key %d (%d)\n", key, scancode);
        });
        final long monitor = glfwGetPrimaryMonitor();
        if (monitor != 0L) {
            GLFWVidMode videoMode = glfwGetVideoMode(monitor);
            if (videoMode != null) {
                glfwSetWindowPos(
                        window,
                        (videoMode.width() - windowWidth) / 2,
                        (videoMode.height() - windowHeight) / 2
                );
            }
        }
        int[] width0 = new int[1];
        int[] height0 = new int[1];
        glfwGetFramebufferSize(window, width0, height0);
        this.framebufferWidth = width0[0];
        this.framebufferHeight = height0[0];
        glfwGetWindowSize(window, width0, height0);
        this.windowWidth = width0[0];
        this.windowHeight = height0[0];
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.framebufferWidth = width;
            this.framebufferHeight = height;
        });
        glfwSetWindowPosCallback(window, (window, xpos, ypos) -> {
        });
        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            this.windowWidth = width;
            this.windowHeight = height;
        });
        glfwSetWindowFocusCallback(window, (window, focused) -> {
        });
        glfwSetCursorEnterCallback(window, (window, entered) -> {
        });
        glfwSetWindowContentScaleCallback(window, (window, xscale, yscale) -> {
           this.scale = Math.max(xscale, yscale);
        });
        glfwPollEvents();
        glfwShowWindow(window);
    }

    public void setSwapInterval(int i) {
        assertOnRenderThread();
        glfwSwapInterval(i);
    }

    public void setWindowTitle(String title) {
        assertOnRenderThread();
        glfwSetWindowTitle(window, title);
    }

    public boolean isOnRenderThread() {
        return renderThread.equals(Thread.currentThread());
    }

    public void assertOnRenderThread() {
        if (!isOnRenderThread()) throw new RuntimeException("assertOnRenderThread");
    }
    boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    long getWindow() {
        return window;
    }

    void terminate() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public int getFramebufferWidth() {
        return framebufferWidth;
    }

    public int getFramebufferHeight() {
        return framebufferHeight;
    }

    public float getScale() {
        return scale;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }
}
