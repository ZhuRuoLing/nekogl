package icu.takeneko.nekogl.render;

import icu.takeneko.nekogl.shader.ShaderProgram;

import java.io.IOException;

public abstract class Renderer implements AutoCloseable {
    protected final ShaderProgram program;
    protected final VertexBuilder vertexBufferBuilder;

    protected Renderer(String shaderId) throws IOException {
        this.program = new ShaderProgram(shaderId);
        vertexBufferBuilder = new VertexBuilder(this);
    }

    public void render(){
        vertexBufferBuilder.begin();
        draw();
        vertexBufferBuilder.end();
        vertexBufferBuilder.draw();
    }

    protected abstract void draw();

    @Override
    public void close() throws Exception {
        program.close();
        vertexBufferBuilder.terminate();
    }

}
