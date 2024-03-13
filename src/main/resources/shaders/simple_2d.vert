#version 150 core
in vec2 position;
in vec4 color;

uniform mat4 projection;

out vec4 fColor;

void main() {
    gl_Position = projection * vec4(position, 0.0, 1.0);
    fColor = color;
}
