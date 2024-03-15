#version 150 core
in vec4 fColor;

out vec4 FragColor;

void main() {
    if (fColor.a < 0.01) {
        discard;
    }
    FragColor = fColor;
}