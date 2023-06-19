#version 120

float getNormalLighting(float minLight, float maxLight, float frac)
{
    float d = (1.0 - (1.0 - frac) * (1.0 - (1.0 - gl_TexCoord[0].y) * 0.3));
    return minLight + (maxLight - minLight) * d;
}