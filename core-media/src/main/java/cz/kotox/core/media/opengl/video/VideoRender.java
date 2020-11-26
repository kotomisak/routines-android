package cz.kotox.core.media.opengl.video;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.Surface;

import cz.kotox.core.media.opengl.OpenGl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLTextureView;
import timber.log.Timber;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;

public class VideoRender implements GLTextureView.Renderer, SurfaceTexture.OnFrameAvailableListener {

	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
	private final float[] mTriangleVerticesData = {
			// X, Y, Z, U, V
			-1.0f, -1.0f, 0, 0.f, 0.f, 1.0f, -1.0f, 0, 1.f, 0.f, -1.0f,
			1.0f, 0, 0.f, 1.f, 1.0f, 1.0f, 0, 1.f, 1.f,};
	private int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

	private FloatBuffer mTriangleVertices;
	private float[] mMVPMatrix = new float[16];
	private float[] mSTMatrix = new float[16];
	private int mProgram;
	private String mVertexShader;

	private int mMaskTextureID[] = new int[1];
	private int mOriginalTextureID[] = new int[1];

	private int muMVPMatrixHandle;
	private int muSTMatrixHandle;
	private int maPositionHandle;

	private int maMaskTextureHandle;
	private int maOriginalTextureHandle;

	private SurfaceTexture mMaskSurface;
	private SurfaceTexture mOriginalSurface;

	private MediaPlayer mMaskPlayer;
	private MediaPlayer mOriginalPlayer;

	private int mSecondTextureLocation;
	private boolean mIsInitialized;
	private boolean updateSurface = false;

	private PlayerState mState = PlayerState.NOT_PREPARED;

	public VideoRender() {
		mVertexShader = AlphaShader.INSTANCE.getVertexShader();
		mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangleVertices.put(mTriangleVerticesData).position(0);
		Matrix.setIdentityM(mSTMatrix, 0);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		initSurface();

		initPlayerSurface();

		play();
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		drawFrame();
	}

	@Override
	synchronized public void onFrameAvailable(SurfaceTexture surface) {
//		Timber.d("Frame available " + System.currentTimeMillis());
		updateSurface = true;
	}

	public void playVideo(MediaPlayer maskPlayer, MediaPlayer originalPlayer) {
		mMaskPlayer = maskPlayer;
		mOriginalPlayer = originalPlayer;

		initPlayerSurface();

		try {
			mMaskPlayer.prepare();
			mOriginalPlayer.prepare();

			mState = PlayerState.PREPARED;
		} catch (IOException | IllegalStateException t) {
			Timber.e("Media player prepare failed");
		}

		mIsInitialized = true;

		play();
	}

	public void onResume() {
		// GLThread is resumed, so onSurfaceCreated() is called and playback resumes.
	}

	public void onPause() {
		pauseVideo();
	}

	public void onDestroy() {
		if (mMaskPlayer != null) {
			mMaskPlayer.release();
		}

		if (mOriginalPlayer != null) {
			mOriginalPlayer.release();
		}

		mState = PlayerState.RELEASED;
	}

	private void initSurface() {
		// Init OpenGL
		mProgram = OpenGl.INSTANCE.loadProgram(mVertexShader, new AlphaFilter().getShader());
		if (mProgram == 0) {
			return;
		}

		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "position");
		if (maPositionHandle == -1) {
			throw new RuntimeException("Could not get attrib location for position");
		}

		maMaskTextureHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
		maOriginalTextureHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate2");

		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		if (muMVPMatrixHandle == -1) {
			throw new RuntimeException("Could not get attrib location for uMVPMatrix");
		}

		muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
		if (muSTMatrixHandle == -1) {
			throw new RuntimeException("Could not get attrib location for uSTMatrix");
		}

		mSecondTextureLocation = GLES20.glGetUniformLocation(mProgram, "inputImageTexture2");

		GLES20.glGenTextures(1, mMaskTextureID, 0);
		GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mMaskTextureID[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		GLES20.glGenTextures(1, mOriginalTextureID, 0);
		GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mOriginalTextureID[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	}

	private void initPlayerSurface() {
		// Init player surfaces

		synchronized (this) {
			updateSurface = false;
		}

		if (mState != PlayerState.NOT_PREPARED) {
			mMaskSurface = new SurfaceTexture(mMaskTextureID[0]);
			mMaskSurface.setOnFrameAvailableListener(this);
			Surface maskSurface = new Surface(mMaskSurface);
			mMaskPlayer.setSurface(maskSurface);
			maskSurface.release();

			mOriginalSurface = new SurfaceTexture(mOriginalTextureID[0]);
			mOriginalSurface.setOnFrameAvailableListener(this);
			Surface originalSurface = new Surface(mOriginalSurface);
			mOriginalPlayer.setSurface(originalSurface);
			originalSurface.release();
		}
	}

	private void drawFrame() {
		//		Timber.d("Frame draw " + System.currentTimeMillis());

		if (!mIsInitialized) {
			return;
		}

		synchronized (this) {
			if (updateSurface) {
				mMaskSurface.updateTexImage();
				mMaskSurface.getTransformMatrix(mSTMatrix);
				mOriginalSurface.updateTexImage();
				mOriginalSurface.getTransformMatrix(mSTMatrix);
				updateSurface = false;
			}
		}
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		GLES20.glUseProgram(mProgram);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mMaskTextureID[0]);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mOriginalTextureID[0]);
		GLES20.glUniform1i(mSecondTextureLocation, 2);

		mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES,
				mTriangleVertices);
		GLES20.glEnableVertexAttribArray(maPositionHandle);

		mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
		GLES20.glVertexAttribPointer(maMaskTextureHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
		GLES20.glEnableVertexAttribArray(maMaskTextureHandle);

		GLES20.glVertexAttribPointer(maOriginalTextureHandle, 3, GLES20.GL_FLOAT, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
		GLES20.glEnableVertexAttribArray(maOriginalTextureHandle);

		Matrix.setIdentityM(mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix,
				0);
		GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

		glEnable(GL_BLEND);
		glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glFinish();
	}

	private void play() {
		switch (mState) {
			case PREPARED: {
				mMaskPlayer.start();
				mOriginalPlayer.start();
				mState = PlayerState.STARTED;
			}
			case PAUSED: {
				mMaskPlayer.start();
				mOriginalPlayer.start();
				mState = PlayerState.STARTED;
			}
		}
	}

	private void pauseVideo() {
		if (mState == PlayerState.STARTED) {
			mMaskPlayer.pause();
			mOriginalPlayer.pause();
			mState = PlayerState.PAUSED;
		}
	}

	private enum PlayerState {
		NOT_PREPARED, PREPARED, STARTED, PAUSED, RELEASED
	}
}
