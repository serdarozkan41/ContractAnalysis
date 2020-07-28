package com.teesteknoloji.contractanalysis.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

import com.github.ybq.android.spinkit.style.CubeGrid;

public class Constants {
    public static final String BASE_URL = "http://test.akinozkan.blog/api/";

    public static void StartLoadingAnim(CubeGrid doubleBounce, View progressOverlay) {
        doubleBounce.setColor(0XFFE2171B);
        doubleBounce.start();
        animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
    }

    public static void StopLoadingAnim(CubeGrid doubleBounce, View progressOverlay) {
        doubleBounce.stop();
        animateView(progressOverlay, View.GONE, 0, 200);
    }

    //region private
    private static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == View.VISIBLE;
        view.bringToFront();
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }
    //endregion

    public static int[] colors = new int[]{
            0XFFD55400,
            0XFF2B3E51,
            0XFF00BD9C,
            0XFF227FBB,
            0XFF7F8C8D,
            0XFFFFCC5C,
            0XFFD55400,
            0XFF1AAF5D,
    };
}


