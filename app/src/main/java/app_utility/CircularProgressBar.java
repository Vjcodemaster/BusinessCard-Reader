package app_utility;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.autochip.businesscardocr.R;


public class CircularProgressBar extends Dialog {
    private boolean hasToDisplay = true;

    public CircularProgressBar(@NonNull Context context, boolean hasToDisplay) {
        super(context);
        this.hasToDisplay = hasToDisplay;
    }

    public CircularProgressBar(@NonNull Context context) {
        super(context);
    }
    /*private Activity activity;

    public CircularProgressBar(Activity activity){
        super(activity);
        this.activity = activity;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.circular_progress_dialog);

        TextView tvWait = findViewById(R.id.tv_please_wait);
        if(!hasToDisplay) {
            tvWait.setVisibility(View.GONE);
        } else {
            tvWait.setVisibility(View.VISIBLE);
        }

        //makes background of DialogMultiple transperent so that we can add shadow effect to cardview
        if (getWindow() != null)
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ProgressBar mProgressBar = findViewById(R.id.login_progress);
        ObjectAnimator anim = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 100);
        anim.setDuration(15000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }
}
