package icu.takeneko.nekogl;

import icu.takeneko.nekogl.call.RenderCall;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.GL;

import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public abstract class RenderThread extends Thread {
    private final LinkedBlockingQueue<RenderCall> renderCalls = new LinkedBlockingQueue<>();
    protected final GlContext context = new GlContext(this);

    public void launch(String windowTitle, int windowWidth, int windowHeight){
        context.init(windowTitle, windowWidth, windowHeight);
        start();
        glfwPollEvents();
    }

    public void end(){
        interrupt();
        context.terminate();
    }

    public RenderThread(){
        super("RenderThread");
    }

    @Override
    public void run() {
        loop();
        context.terminate();
    }

    private void invokePendingCalls(){
        if (!renderCalls.isEmpty()) {
            synchronized (renderCalls) {
                for (RenderCall call : renderCalls) {
                    try {
                        call.accept(context);
                    } catch (Throwable t) {
                        UncaughtExceptionHandler handler = getUncaughtExceptionHandler();
                        if (handler != null) {
                            handler.uncaughtException(this, t);
                        } else {
                            throw new RuntimeException("Uncaught Exception on RenderThread.", t);
                        }
                    }
                }
                renderCalls.clear();
            }
        }
    }

    private void loop() {
        long window = context.getWindow();
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        initRenderer();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glfwSwapInterval(1);
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glViewport(0, 0, context.getFramebufferWidth(), context.getFramebufferHeight());
            render();
            invokePendingCalls();
            if (!OperatingSystem.IS_MAC_OS) {
                glfwPollEvents();
            }
            glfwSwapBuffers(window);
        }
        Callbacks.glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwMakeContextCurrent(0);

    }

    protected void initRenderer(){}

    abstract protected void render();

    public void putCall(RenderCall call){
        renderCalls.add(call);
    }
}
