package icu.takeneko.nekogl.render;

import java.io.IOException;

import static org.lwjgl.opengl.GL32.*;

public abstract class Simple2DRenderer extends Renderer {
    private final int projectionUniformLocation;

    public Simple2DRenderer() throws IOException {
        super("simple_2d");
        projectionUniformLocation = program.glGetUniformLocation("projection");
    }

    public void viewport(int width, int height) {
        program.glUseProgram();
        glUniformMatrix4fv(projectionUniformLocation, false, new float[]{
                2f / width, 0,            0, 0,
                0,          -2f / height, 0, 0,
                0,          0,            1, 0,
                -1,         1,            0, 1
        });
        glUseProgram(0);
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3, float r, float g, float b, float a) {
        vertexBufferBuilder.vertex(x1, y1, r, g, b, a);
        vertexBufferBuilder.vertex(x2, y2, r, g, b, a);
        vertexBufferBuilder.vertex(x3, y3, r, g, b, a);
    }

    public void rect(float x, float y, float width, float height, float r, float g, float b, float a) {
        this.triangle(x, y, x, y + height, x + width, y, r, g, b, a);
        this.triangle(x + width, y + height, x + width, y, x, y + height, r, g, b, a);
    }
}
