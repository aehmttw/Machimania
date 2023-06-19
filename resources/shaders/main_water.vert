#version 120

uniform float time;
uniform vec2 regionPos;
attribute vec4 otherPos;
attribute float waveSize;

#define PI 3.1415926538

mat4 getTransform()
{
    return mat4(1, 0, 0, 0,  0, 1, 0, 0,  0, 0, 1, 0,  0, 0, 0, 1);
}

float mod(float num1, float num2)
{
    return num1 - int(floor(num1 / num2)) * num2;
}

float getWaveOffset(float posX, float posY)
{
    float f = mod((time + (posX + posY / 2.0) * 1.25), (PI * 4.0));
    float f2 = mod((2 * time + (posX - posY / 2.0) * 2.5), (PI * 4.0));
    float f3 = mod((time + (posX + posY / 2.0) / 3), (PI * 4.0));
    float f4 = mod((0.5 * time + (posX - posY / 2.0) / 10), (PI * 4.0));

    return (waveSize) * ((sin(f) + 1.0) * 0.125 + (sin(f2) + 1.0) * 0.0625 + (sin(f3) + 1.0) * 0.25 + (sin(f4) + 1.0) * 0.5);
}

vec4 getPos(mat4 transform)
{
    vec4 vert = gl_Vertex;

    float w = getWaveOffset(gl_Vertex.x + regionPos.x, gl_Vertex.y + regionPos.y);
    vert.z += w;

    return vert;
}

vec3 getNormal(mat4 transform)
{
    float z = gl_Vertex.z;
    vec3 pos1 = vec3(gl_Vertex.x, gl_Vertex.y, z + getWaveOffset(gl_Vertex.x + regionPos.x, gl_Vertex.y + regionPos.y));
    vec3 pos2 = vec3(otherPos[0], otherPos[1], z + getWaveOffset(otherPos[0] + regionPos.x, otherPos[1] + regionPos.y));
    vec3 pos3 = vec3(otherPos[2], otherPos[3], z + getWaveOffset(otherPos[2] + regionPos.x, otherPos[3] + regionPos.y));
    return normalize(cross(pos2 - pos1, pos3 - pos1));
}

void getVertVecs(out vec4 pos, out vec3 normal)
{
    pos = getPos(getTransform());
    normal = getNormal(getTransform());
}