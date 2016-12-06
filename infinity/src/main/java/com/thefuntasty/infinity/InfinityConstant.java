package com.thefuntasty.infinity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class InfinityConstant {
	public static final int IDLE = 0;
	public static final int LOADING = 1;
	public static final int FINISHED = 2;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({IDLE, LOADING, FINISHED})
	public @interface Status {}

	static final int FIRST_PAGE = 0;
	static final int NEXT_PAGE = 1;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({FIRST_PAGE, NEXT_PAGE})
	@interface Part {}
}
