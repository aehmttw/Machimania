#define DEPTH_OFFSET 0.0005

uniform vec3 lightVec;

uniform bool customLight;
uniform vec3 lightDiffuse;
uniform vec3 lightAmbient;
uniform vec3 lightSpecular;
uniform float shininess;

uniform float minBrightness;
uniform float maxBrightness;
uniform bool negativeBrightness;

uniform sampler2D depthTexture;
uniform vec3 lightPosition;

varying vec4 lightBiasedClipPosition;

uniform sampler2D tex;

uniform bool texture;
uniform bool depthtest;
uniform float glow;

uniform float light;
uniform float glowLight;
uniform float shade;
uniform float glowShade;

uniform float celsections;

uniform int shadowres;
varying vec4 vertexColor;

uniform bool shadow;

uniform bool vbo;
uniform vec4 originalColor;

uniform bool useNormal;
varying vec3 normal;

varying vec3 position;

uniform mat4 lightViewProjectionMatrix;

mat3 toMat3(mat4 matrix)
{
    return mat3(matrix[0].xyz, matrix[1].xyz, matrix[2].xyz);
}

mat4 toMat4(mat3 matrix)
{
    return mat4(vec4(matrix[0].xyz, 0), vec4(matrix[1].xyz, 0), vec4(matrix[2].xyz, 0), vec4(0, 0, 0, 1));
}

mat3 transpose(mat3 mat)
{
    return mat3(vec3(mat[0].x, mat[1].x, mat[2].x), vec3(mat[0].y, mat[1].y, mat[2].y), vec3(mat[0].z, mat[1].z, mat[2].z));
}

float det(mat2 matrix)
{
    return matrix[0].x * matrix[1].y - matrix[0].y * matrix[1].x;
}

mat3 inverse(mat3 matrix)
{
    vec3 row0 = matrix[0];
    vec3 row1 = matrix[1];
    vec3 row2 = matrix[2];

    vec3 m0 = vec3(
    det(mat2(row1.y, row1.z, row2.y, row2.z)),
    det(mat2(row1.z, row1.x, row2.z, row2.x)),
    det(mat2(row1.x, row1.y, row2.x, row2.y)));

    vec3 m1 = vec3(
    det(mat2(row2.y, row2.z, row0.y, row0.z)),
    det(mat2(row2.z, row2.x, row0.z, row0.x)),
    det(mat2(row2.x, row2.y, row0.x, row0.y)));

    vec3 m2 = vec3(
    det(mat2(row0.y, row0.z, row1.y, row1.z)),
    det(mat2(row0.z, row0.x, row1.z, row1.x)),
    det(mat2(row0.x, row0.y, row1.x, row1.y)));

    mat3 adj = transpose(mat3(m0, m1, m2));

    return (1.0 / dot(row0, m0)) * adj;
}

void main(void)
{
    vec4 color = texture2D(tex, gl_TexCoord[0].st);

    if (texture)
    {
        gl_FragColor = color * vertexColor;

        if (color.a <= 0.0)
            discard;
    }
    else
        gl_FragColor = vertexColor;

    if (vbo)
        gl_FragColor *= originalColor;

    if (shadow)
    {
        vec4 lightNDCPosition = lightBiasedClipPosition / lightBiasedClipPosition.w;

        vec4 depth = texture2D(depthTexture, lightNDCPosition.xy);

        if (!depthtest)
            gl_FragColor *= vec4(1.0, 1.0, 1.0, 1.0);
        else
        {
            bool lit = depth.z >= lightNDCPosition.z - DEPTH_OFFSET * 2048.0 / float(shadowres);

            float maxLight = 1.0;
            float minLight = 0.0;

            if (!customLight)
            {
                maxLight = light * (1.0 - glow) + glowLight * glow;
                minLight = shade * (1.0 - glow) + glowShade * glow;
            }

            float col;

            if (useNormal)
            {
                float d = dot(normal, lightVec);

                if (negativeBrightness)
                {
                    if (d < minBrightness)
                        d = -1.0;
                    else if (d > maxBrightness)
                        d = 1.0;
                    else
                        d = 2.0 * (d - minBrightness) / (maxBrightness - minBrightness) - 1.0;
                }
                else
                {
                    if (d < minBrightness)
                        d = 0.0;
                    else if (d > maxBrightness)
                        d = 1.0;
                    else
                        d = (d - minBrightness) / (maxBrightness - minBrightness);
                }

                if (celsections > 0.0)
                    d = float(int(celsections * d + celsections / 10.0)) / celsections;

                col = getNormalLighting(minLight, maxLight, d);

                if (!lit)
                    col = min(col, minLight);
            }
            else
            {
                if (lit)
                    col = maxLight;
                else
                    col = minLight;
            }

            if (customLight)
                gl_FragColor.xyz *= (lightDiffuse * col + lightAmbient);
            else
                gl_FragColor.xyz *= col;

            if (useNormal && lit)
            {
                vec3 cam = normalize((inverse(toMat3(gl_ProjectionMatrix)) * vec3(0, 0, 1)).xyz);
                vec3 h = normalize(cam + normalize(lightVec));
                float specular = pow(0.5 + dot(normalize(normal), h) / 2.0, shininess);

                gl_FragColor.xyz += lightSpecular * specular;
            }
        }
    }
}