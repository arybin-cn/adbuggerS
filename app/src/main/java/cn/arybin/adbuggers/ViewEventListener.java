package cn.arybin.adbuggers;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class ViewEventListener implements OnLongClickListener, OnClickListener {

	private MainActivity bindingActivity = null;

	private ViewEventListener(MainActivity activity) {
		this.bindingActivity = activity;
	}

	public static ViewEventListener newInstance(MainActivity activity) {
		return new ViewEventListener(activity);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textViewMain:
			if (bindingActivity.textViewMain.getAnimation() == null
					|| bindingActivity.textViewMain.getAnimation().hasEnded()) {
				bindingActivity.textViewMain
						.startAnimation(bindingActivity.cachedSimpleAnimation);
				bindingActivity.showToast("Long click to quit! :)");
			}
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.textViewMain:

			bindingActivity.textViewMain.setText("Bye bye!! :)");
			bindingActivity.textViewMain
					.setAnimation(bindingActivity.cachedAnimationSet);

			((Vibrator) bindingActivity
					.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(
					new long[] { 10, 10 }, 0);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					bindingActivity.finish();
				}
			}).start();

			break;

		default:
			break;
		}
		return true;
	}

}
