package cz.kotox.core.media.opengl.video.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.AttributeSet;

import java.io.IOException;

import cz.kotox.core.media.opengl.video.VideoRender;
import android.opengl.GLTextureView;

public class AlphaVideoTextureView extends GLTextureView {

	public interface VideoListener {
		void onLoopingSequenceStarted();
	}

	private static final float VIEW_ASPECT_RATIO = 9f / 16f;
	private float mVideoAspectRatio = VIEW_ASPECT_RATIO;

	private VideoRender mRenderer;

	private Boolean mVideoInitialized = false;

	public AlphaVideoTextureView(Context context) {
		this(context, null);
	}

	public AlphaVideoTextureView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setEGLContextClientVersion(2);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		mRenderer = new VideoRender();
		setRenderer(mRenderer);

		setOpaque(false);
	}

	@Override
	public void onResume() {
		super.onResume();

		mRenderer.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();

		mRenderer.onPause();
	}

	public Boolean isVideoInitialized() {
		return mVideoInitialized;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		mRenderer.onDestroy();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		double currentAspectRatio = (double) widthSize / heightSize;
		if (currentAspectRatio > mVideoAspectRatio) {
			widthSize = (int) (heightSize * mVideoAspectRatio);
		} else {
			heightSize = (int) (widthSize / mVideoAspectRatio);
		}

		super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, widthMode), MeasureSpec.makeMeasureSpec(heightSize, heightMode));
	}

	public void playVideo(
			AssetFileDescriptor maskAsset,
			AssetFileDescriptor originalAsset,
			boolean looping) throws IOException {
		MediaPlayer maskMediaPlayer = new MediaPlayer();
		maskMediaPlayer.setDataSource(maskAsset.getFileDescriptor(), maskAsset.getStartOffset(), maskAsset.getLength());
		maskMediaPlayer.setLooping(looping);

		MediaPlayer originalMediaPlayer = new MediaPlayer();
		originalMediaPlayer.setDataSource(originalAsset.getFileDescriptor(), originalAsset.getStartOffset(), originalAsset.getLength());
		originalMediaPlayer.setLooping(looping);

		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(maskAsset.getFileDescriptor(), maskAsset.getStartOffset(), maskAsset.getLength());

		calculateVideoAspectRatio(retriever);

		queueEvent(() -> mRenderer.playVideo(maskMediaPlayer, originalMediaPlayer));
		mVideoInitialized = true;
	}

	public void playCombinedVideo(
			AssetFileDescriptor oneTimeFirstMaskAsset,
			AssetFileDescriptor oneTimeFirstOriginalAsset,
			AssetFileDescriptor loopingNextMaskAsset,
			AssetFileDescriptor loopingNextOriginalAsset,
			VideoListener listener) throws IOException {
		MediaPlayer maskMediaPlayer = new MediaPlayer();
		maskMediaPlayer.setDataSource(oneTimeFirstMaskAsset.getFileDescriptor(), oneTimeFirstMaskAsset.getStartOffset(), oneTimeFirstMaskAsset.getLength());
		maskMediaPlayer.setLooping(false);

		MediaPlayer originalMediaPlayer = new MediaPlayer();
		originalMediaPlayer.setDataSource(oneTimeFirstOriginalAsset.getFileDescriptor(), oneTimeFirstOriginalAsset.getStartOffset(), oneTimeFirstOriginalAsset.getLength());
		originalMediaPlayer.setLooping(false);

		if (listener != null) {
			originalMediaPlayer.setOnCompletionListener(
					mediaPlayer -> {
						maskMediaPlayer.stop();
						originalMediaPlayer.stop();
						maskMediaPlayer.reset();
						originalMediaPlayer.reset();
						try {
							maskMediaPlayer.setDataSource(loopingNextMaskAsset.getFileDescriptor(), loopingNextMaskAsset.getStartOffset(), loopingNextMaskAsset.getLength());
							originalMediaPlayer.setDataSource(loopingNextOriginalAsset.getFileDescriptor(), loopingNextOriginalAsset.getStartOffset(), loopingNextOriginalAsset.getLength());
							maskMediaPlayer.setLooping(true);
							originalMediaPlayer.setLooping(true);
							maskMediaPlayer.prepare();
							originalMediaPlayer.prepare();
						} catch (IOException e) {
							e.printStackTrace();
						}

						maskMediaPlayer.start();
						originalMediaPlayer.start();

						listener.onLoopingSequenceStarted();
					}
			);
		}

		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(oneTimeFirstMaskAsset.getFileDescriptor(), oneTimeFirstMaskAsset.getStartOffset(), oneTimeFirstMaskAsset.getLength());

		calculateVideoAspectRatio(retriever);

		queueEvent(() -> mRenderer.playVideo(maskMediaPlayer, originalMediaPlayer));
		mVideoInitialized = true;
	}

	private void calculateVideoAspectRatio(MediaMetadataRetriever retriever) {
		int videoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
		int videoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

		if (videoWidth > 0 && videoHeight > 0) {
			mVideoAspectRatio = (float) videoWidth / videoHeight;
		}

		requestLayout();
		invalidate();
	}
}