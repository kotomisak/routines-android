package cz.kotox.core.media.opengl.video

import org.intellij.lang.annotations.Language

object AlphaShader {

	@Language("GLSL")
	fun getFragmentShader(): String =
		"""
		#extension GL_OES_EGL_image_external : require
		precision highp float;
		varying highp vec2 textureCoordinate;
		varying highp vec2 textureCoordinate2;
		uniform samplerExternalOES inputImageTexture;
		uniform samplerExternalOES inputImageTexture2;
		void main()
		{
			vec4 textureColorAlpha = texture2D(inputImageTexture, textureCoordinate);// alpha
			vec4 textureColorFX = texture2D(inputImageTexture2, textureCoordinate2); // fx
			gl_FragColor = textureColorFX;
			gl_FragColor.a = (textureColorAlpha.r + textureColorAlpha.g + textureColorAlpha.b) / 3.0;
		}
		""".trimIndent()

	@Language("GLSL")
	fun getVertexShader(): String =
		"""
		uniform mat4 uMVPMatrix;
		uniform mat4 uSTMatrix;
		
		attribute vec4 position;
		
		attribute vec4 inputTextureCoordinate;
		attribute vec4 inputTextureCoordinate2;
		
		varying vec2 textureCoordinate;
		varying vec2 textureCoordinate2;
		
		void main() {
		   gl_Position = uMVPMatrix * position;
		   textureCoordinate = (uSTMatrix * inputTextureCoordinate).xy;
		   textureCoordinate2 = (uSTMatrix * inputTextureCoordinate2).xy;
		}
		""".trimIndent()
}