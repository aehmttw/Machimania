#version 120
#extension GL_EXT_geometry_shader4 : enable

varying in vec4 lightBiasedClipPosition2[3];
varying in vec4 vertexColor2[3];
varying in vec3 normal2[3];
varying in vec3 position2[3];

varying out vec4 lightBiasedClipPosition;
varying out vec4 vertexColor;
varying out vec3 normal;
varying out vec3 position;

void main()
{
    for (int i = 0; i < gl_VerticesIn; i++)
    {
        gl_Position = gl_PositionIn[i];
        gl_TexCoord[0] = gl_TexCoordIn[i][0];
        lightBiasedClipPosition = lightBiasedClipPosition2[i];
        vertexColor = vertexColor2[i];
        normal = normal2[i];

        EmitVertex();
    }
    EndPrimitive();
}
