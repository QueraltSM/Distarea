package com.disoft.distarea.extras;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LockableViewPager extends ViewPager {
  private boolean enabled;

  public LockableViewPager(Context context, AttributeSet attrs) {
      super(context, attrs); this.enabled = true; }

  @Override public boolean onTouchEvent(MotionEvent event) {
      if(enabled){return false;}
      return super.onTouchEvent(event); }

  @Override public boolean onInterceptTouchEvent(MotionEvent event) {
      if (enabled) {return false;}
      return super.onInterceptTouchEvent(event); }

  public void setPageLock(boolean enabled) {this.enabled = enabled;}
}