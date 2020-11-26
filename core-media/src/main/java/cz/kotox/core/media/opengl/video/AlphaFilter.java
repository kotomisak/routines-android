package cz.kotox.core.media.opengl.video;


public class AlphaFilter implements ShaderInterface {

	@Override
	public String getShader() {
		return AlphaShader.INSTANCE.getFragmentShader();
	}
} 