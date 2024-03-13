package icu.takeneko.nekogl.shader;

import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram implements AutoCloseable {
    private final String shaderId;
    private final int shaderProgram;

    public ShaderProgram(String shaderId) throws IOException {
        this.shaderId = shaderId;
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, getShaderSource(false));
        glCompileShader(vertexShader);
        final int[] success = new int[1];
        glGetShaderiv(vertexShader, GL_COMPILE_STATUS, success);
        if (success[0] == 0) {
            RuntimeException e = new RuntimeException("Compile vertex shader failed: " + glGetShaderInfoLog(vertexShader));
            glDeleteShader(vertexShader);
            throw e;
        }
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, getShaderSource(true));
        glCompileShader(fragmentShader);
        glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, success);
        if (success[0] == 0) {
            RuntimeException e = new RuntimeException("Compile fragment shader failed: " + glGetShaderInfoLog(fragmentShader));
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            throw e;
        }
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glGetProgramiv(shaderProgram, GL_LINK_STATUS, success);
        if (success[0] == 0) {
            RuntimeException e = new RuntimeException("Link shader program failed: " + glGetProgramInfoLog(shaderProgram));
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            glDeleteProgram(shaderProgram);
            throw e;
        }
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private String getShaderSource(boolean frag) throws IOException {
        String path = "shaders/" + shaderId + "." + (frag ? "frag" : "vert");
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new RuntimeException("Shader %s not found".formatted(path));
        }
        String content = new String(is.readAllBytes());
        is.close();
        return content;
    }

    public int glGetUniformLocation(String name) {
        return GL20.glGetUniformLocation(shaderProgram, name);
    }

    @Override
    public void close() throws Exception {
        glDeleteProgram(shaderProgram);
    }

    public void glUseProgram(){
        GL20.glUseProgram(shaderProgram);
    }
}
