package machimania.region;

import basewindow.BaseWindow;
import basewindow.IBaseShader;
import basewindow.ShaderBase;

import static basewindow.BaseShaderUtil.FLOAT;

public class ShaderWater extends ShaderBase implements IBaseShader
{
    public Uniform1f time;
    public Uniform2f regionPos;
    public Attribute otherPos;
    public Attribute waveSize;

    public ShaderWater(BaseWindow window)
    {
        super(window);
    }

    @Override
    public void initialize() throws Exception
    {
        this.setUp("/shaders/main.vert", new String[]{"/shaders/main_water.vert"}, "/shaders/main.frag", new String[]{"/shaders/main_default.frag"});
    }

    @Override
    public void initializeUniforms()
    {
        this.depthTexture.set(1);
    }

    @Override
    public void initializeAttributeParameters()
    {
        this.otherPos.setDataType(FLOAT, 4);
        this.waveSize.setDataType(FLOAT, 1);
    }
}
