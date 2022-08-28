#version 120

float getNormalLighting(float minLight, float maxLight, float frac)
{
    float d = (1.0 - (1.0 - frac) * (1 + gl_TexCoord[0].y) * 0.5);
    return minLight + (maxLight - minLight) * d;
}