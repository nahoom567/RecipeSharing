package com.example.recipeproject;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class TransitionUtility
{
    public static void startActivityWithTransition(final Activity activity, final Intent intent)
    {
        /*
        these lines instantiate a custom TransitionClass object, obtain the root view of the
        current activity, create layout parameters for the transition view, and then add the
        transition view to the root view with the specified layout parameters.
        This sequence sets up the groundwork for displaying the transition animation
        on top of the current activity's content
        */
        final TransitionClass transition = new TransitionClass(activity);
        final ViewGroup rootView = activity.findViewById(android.R.id.content);
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        rootView.addView(transition, layoutParams);
        // starting the new activity
        activity.startActivity(intent);

        // applying transition animation if needed
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}