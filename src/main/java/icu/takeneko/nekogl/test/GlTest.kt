package icu.takeneko.nekogl.test

import icu.takeneko.nekogl.RenderThread
import icu.takeneko.nekogl.render.Simple2DRenderer
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

fun main() {
    val renderThread = object : RenderThread() {
        var frameCount = 0
        lateinit var simple2DRenderer: Simple2DRenderer

        override fun initRenderer() {
            simple2DRenderer = object : Simple2DRenderer() {
                override fun draw() {
                    rect(
                        -0.5f, -0.5f,
                        0.5f, 0.5f,
                        1f, 1f, 0f, 0.5f
                    )
                }
            }
        }

        override fun render() {
            //GL11.glEnable(GL11.GL_BLEND)
            frameCount++
            simple2DRenderer.viewport(this.context.framebufferWidth, this.context.framebufferHeight)
            //GL11.glClearColor(1f, 1f, 0f, 0f)
            simple2DRenderer.render()
            if (frameCount % 60 == 0) println("frameCount = $frameCount")
            //GL11.glDisable(GL11.GL_BLEND)
        }
    }
    renderThread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t: Thread, e: Throwable ->
        System.err.println("Uncaught exception on thread $t")
        e.printStackTrace()
    }
    renderThread.launch("nekogl", 854, 480)
    Thread.sleep(1000)
    while (renderThread.isAlive) {
        GLFW.glfwPollEvents()
    }
    renderThread.join()
    //renderThread.start()
}