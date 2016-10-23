package com.thefuntasty.infinity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class InfinityConstant {
	static final int IDLE = 0;
	static final int LOADING = 1;
	static final int FINISHED = 2;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({IDLE, LOADING, FINISHED})
	@interface Status {}

	static final int FIRST_PAGE = 0;
	static final int NEXT_PAGE = 1;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({FIRST_PAGE, NEXT_PAGE})
	@interface Part {}
}
