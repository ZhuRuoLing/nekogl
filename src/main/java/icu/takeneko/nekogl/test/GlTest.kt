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
                    triangle(
                        0f,0f,
                        -1f,-1f,
                        1f,-1f,
                        1f,1f,1f,0f
                    )
                }
            }

        }

        override fun render() {
            frameCount++
            simple2DRenderer.viewport(this.context.framebufferWidth, this.context.framebufferHeight)
            GL11.glEnable(GL11.GL_BLEND)
            //GL11.glClearColor((frameCount % 60f) / 60f, (frameCount % 60f) / 60f, (frameCount % 60f) / 60f, 0.0f)
            simple2DRenderer.render()
        }
    }
    renderThread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t: Thread, e: Throwable ->
        System.err.println("Uncaught exception on thread $t")
        e.printStackTrace()
    }
    renderThread.launch(null, 854, 480)
    Thread.sleep(10)
    while (renderThread.isAlive) {
        GLFW.glfwPollEvents()
    }
    renderThread.join()
    //renderThread.start()
}