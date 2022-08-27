package machimania.region;

import basewindow.BaseWindow;
import basewindow.IBaseShader;
import basewindow.ShaderBase;

public class ShaderGrass extends ShaderBase implements IBaseShader
{
    public Uniform1f time;
    public Uniform2f regionPos;
    public Uniform3f playerPos;

    public ShaderGrass(BaseWindow window)
    {
        super(window);
    }

    @Override
    public void initialize() throws Exception
    {
        this.setUp("/shaders/main.vert", new String[]{"/shaders/main_grass.vert"}, "/shaders/main.frag", null);
    }

    @Override
    public void initializeUniforms()
    {
        this.depthTexture.set(1);
    }
}
