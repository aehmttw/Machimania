#version 120

uniform float time;
uniform vec2 regionPos;
uniform vec3 playerPos;

#define PI 3.1415926538

mat4 getTransform()
{
    return mat4(1, 0, 0, 0,  0, 1, 0, 0,  0, 0, 1, 0,  0, 0, 0, 1);
}

float mod(float num1, float num2)
{
    return num1 - int(floor(num1 / num2)) * num2;
}

float getWaveOffset()
{
    float f = mod((time + (regionPos.x + gl_Vertex.x) / 2.0), (PI * 4.0));
    float m = 1.0;

    if (f >= PI * 1.5 && f <= PI * 3.5)
        m = 0.25;

    return -(sin(f) + 1.0) * m;
}

vec4 getPos(mat4 transform)
{
    vec4 vert = gl_Vertex;

    float w = getWaveOffset();
    vert.x += w * (1 - gl_MultiTexCoord0.y) / 4.0;
    vert.y += w * (1 - gl_MultiTexCoord0.y) / 8.0;

    vec3 globalPos = vert.xyz;
    globalPos.xy += regionPos;
    vec2 diff = globalPos.xy - playerPos.xy;
    float l = length(diff);

    float hdiff = 1.0 - max(0, min(1.0, abs(globalPos.z - 1.0 - playerPos.z) - 0.5));

    if (l < 1.0)
        vert.z -= hdiff * (1.0 - l) * (1.0 - gl_MultiTexCoord0.y * 0.75);

    if (l < 1.0)
    {
        vert.xy += hdiff * 0.5 * (1 - l) * normalize(diff).xy * (1.0 - gl_MultiTexCoord0.y);
    }

    return vert;
}

vec3 getNormal(mat4 transform)
{
    return gl_Normal;
}

void getVertVecs(out vec4 pos, out vec3 normal)
{
    pos = getPos(getTransform());
    normal = gl_Normal;
}