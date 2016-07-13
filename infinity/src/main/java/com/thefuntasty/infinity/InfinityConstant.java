package com.thefuntasty.infinity;

import android.support.annotation.IntDef;

public class InfinityConstant {
	public static final int IDLE = 0;
	public static final int LOADING = 1;
	public static final int FINISHED = 2;

	@IntDef({IDLE, LOADING, FINISHED})
	public @interface Status {}

	public static final int FIRST_PAGE = 0;
	public static final int NEXT_PAGE = 1;

	@IntDef({FIRST_PAGE, NEXT_PAGE})
	public @interface Part {}
}
