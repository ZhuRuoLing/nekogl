package icu.takeneko.nekogl.render;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexBuilder {
    private static final int INITIAL_SIZE = 1024;
    private float[] buffer = new float[INITIAL_SIZE];
    private final int vbo, vao;
    private boolean destroyed = false;
    private boolean building = false;
    private int pos = 0;

    private final Renderer parent;

    public VertexBuilder(Renderer parent) {
        this.parent = parent;
        vbo = glGenBuffers();
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 6 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private void ensureCapacity(int capacity) {
        if (buffer.length < capacity) {
            float[] newBuffer = new float[buffer.length * 2];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            buffer = newBuffer;
        }
    }

    public VertexBuilder begin() {
        if (building) throw new IllegalStateException("Already building");
        building = true;
        pos = 0;
        return this;
    }

    public VertexBuilder vertex(float x, float y, float r, float g, float b, float a) {
        ensureCapacity(pos + 6);
        buffer[pos++] = x;
        buffer[pos++] = y;
        buffer[pos++] = r;
        buffer[pos++] = g;
        buffer[pos++] = b;
        buffer[pos++] = a;
        return this;
    }

    public void end() {
        if (destroyed) throw new IllegalStateException("Already destroyed");
        if (!building) throw new IllegalStateException("Not building");
        building = false;
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void draw() {
        if (destroyed) throw new IllegalStateException("Already destroyed");
        if (building) throw new IllegalStateException("Still building");
        parent.program.glUseProgram();
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, pos / 6);
        glBindVertexArray(0);
        glUseProgram(0);
    }

    public void terminate() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        destroyed = true;
    }

}