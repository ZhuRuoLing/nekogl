package icu.takeneko.nekogl

import org.lwjgl.opengl.GL11
import org.lwjgl.system.Configuration

fun main() {
    Configuration.GLFW_CHECK_THREAD0.set(false)

    val renderThread: RenderThread = object : RenderThread() {
        val startTime = System.nanoTime()
        var frameCount = 0
        override fun render() {
            frameCount++
            val elapsedTime = (System.nanoTime() - startTime) * 1e-9
            val fps = frameCount / elapsedTime
            GL11.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
            println("frameCount = $frameCount")
            println("elapsedTime = $elapsedTime")
            println("fps = $fps")

        }
    }
    renderThread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { t: Thread, e: Throwable ->
        System.err.println("Uncaught exception on thread $t")
        e.printStackTrace()
    }
    renderThread.init("1234567890", 854, 480)
    Thread.sleep(10)
    renderThread.start()
}