package np.com.uashim.ststv;

import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;

import org.videolan.libvlc.*;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LivePlayer extends AppCompatActivity implements IVLCVout.Callback{


	public final static String TAG = "LivePlayer";
	private String mFilePath;
	private SurfaceView mSurface;
	private SurfaceHolder holder;
	private LibVLC libvlc;
	private MediaPlayer mMediaPlayer = null;
	private int mVideoWidth;
	private int mVideoHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_live_player);

		mFilePath = "rtmp://137.74.42.154:1935/pradip/live";

		Log.d(TAG, "Playing: " + mFilePath);
		mSurface =  findViewById(R.id.surface);
		holder = mSurface.getHolder();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setSize(mVideoWidth, mVideoHeight);
	}

	@Override
	protected void onResume() {
		super.onResume();
		createPlayer(mFilePath);
	}

	@Override
	protected void onPause() {
		super.onPause();
		releasePlayer();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releasePlayer();
	}

	private void setSize(int width, int height) {
		mVideoWidth = width;
		mVideoHeight = height;
		if (mVideoWidth * mVideoHeight <= 1)
			return;

		if (holder == null || mSurface == null)
			return;

		int w = getWindow().getDecorView().getWidth();
		int h = getWindow().getDecorView().getHeight();
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		if (w > h && isPortrait || w < h && !isPortrait) {
			int i = w;
			w = h;
			h = i;
		}

		float videoAR = (float) mVideoWidth / (float) mVideoHeight;
		float screenAR = (float) w / (float) h;

		if (screenAR < videoAR)
			h = (int) (w / videoAR);
		else
			w = (int) (h * videoAR);

		holder.setFixedSize(mVideoWidth, mVideoHeight);
		ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
		lp.width = w;
		lp.height = h;
		mSurface.setLayoutParams(lp);
		mSurface.invalidate();
	}
	private void createPlayer(String media) {
		releasePlayer();
		try {
			if (media.length() > 0) {
			}

			ArrayList<String> options = new ArrayList<String>();
			//options.add("--subsdec-encoding <encoding>");
			options.add("--aout=opensles");
			options.add("--audio-time-stretch"); // time stretching
			options.add("-vvv"); // verbosity
			libvlc = new LibVLC(this, options);
			holder.setKeepScreenOn(true);

			mMediaPlayer = new MediaPlayer(libvlc);
			mMediaPlayer.setEventListener(mPlayerListener);

			final IVLCVout vout = mMediaPlayer.getVLCVout();
			vout.setVideoView(mSurface);
			//vout.setSubtitlesView(mSurfaceSubtitles);
			vout.addCallback(this);
			vout.attachViews();
			Media m = new Media(libvlc, Uri.parse(media));
			mMediaPlayer.setMedia(m);
			mMediaPlayer.play();
		
		} catch (Exception e) {
			Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show();
		}
	}

	private void releasePlayer() {
		if (libvlc == null)
			return;
		mMediaPlayer.stop();
		final IVLCVout vout = mMediaPlayer.getVLCVout();
		vout.removeCallback(this);
		vout.detachViews();
		holder = null;
		libvlc.release();
		libvlc = null;

		mVideoWidth = 0;
		mVideoHeight = 0;
	}
	private MediaPlayer.EventListener mPlayerListener = new LivePlayer.MyPlayerListener(this);

	@Override
	public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
		if (width * height == 0) {
			return;
		}
		mVideoWidth = width;
		mVideoHeight = height;
		setSize(mVideoWidth, mVideoHeight);
	}



	@Override
	public void onSurfacesCreated(IVLCVout vlcVout) {
		//Don't do anything here
	}

	@Override
	public void onSurfacesDestroyed(IVLCVout vlcVout) {
		//Don't do anything here
	}

	@Override
	public void onHardwareAccelerationError(IVLCVout vlcVout) {
		//Don't do anything here
	}
	private static class MyPlayerListener implements MediaPlayer.EventListener {
		private WeakReference<LivePlayer> mOwner;



		public MyPlayerListener(LivePlayer livePlayer) {
			mOwner = new WeakReference<LivePlayer>(livePlayer);

		}

		@Override
		public void onEvent(MediaPlayer.Event event) {
			LivePlayer player = mOwner.get();

			switch (event.type) {
				case MediaPlayer.Event.EndReached:
					Log.d(TAG, "MediaPlayerEndReached");
					player.releasePlayer();
					break;
				case MediaPlayer.Event.Playing:
				case MediaPlayer.Event.Paused:
				case MediaPlayer.Event.Stopped:
				default:
					break;
			}
		}
	}
}
