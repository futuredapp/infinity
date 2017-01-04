package com.thefuntasty.infinity.test.adapter.item_count;

import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;

import com.thefuntasty.infinity.InfinityAdapter;
import com.thefuntasty.infinity.InfinityConstant;
import com.thefuntasty.infinity.InfinityEventListener;
import com.thefuntasty.infinity.test.App;
import com.thefuntasty.infinity.test.BuildConfig;
import com.thefuntasty.infinity.test.User;
import com.thefuntasty.infinity.test.adapter.BaseRxAdapterTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M, application = App.class)
public class ExactlyOnePageAdapterTest extends BaseRxAdapterTest {

	@Mock InfinityEventListener mockEventListener;

	@Test
	public void exactlyOnePage() {
		verify(mockEventListener).onPreLoadFirst(anyBoolean());
		verify(mockEventListener).onFirstLoaded(anyBoolean());
		verify(mockEventListener).onPreLoadNext();
		verify(mockEventListener).onNextLoaded();
		verify(mockEventListener).onFinished();
		verifyNoMoreInteractions(mockEventListener);

		// 2 headers + 1 item
		assertThat(recyclerView.getAdapter().getItemCount()).isEqualTo(3);
		assertThat(((InfinityAdapter) recyclerView.getAdapter()).getCurrentLoadingStatus()).isEqualTo(InfinityConstant.FINISHED);
	}

	@Override public Observable<List<User>> getDataObservable(int limit, int offset) {
		if (offset == 0) { // first page
			return Observable.just(Collections.singletonList(new User("hello", "world")));
		} else { // other pages
			return Observable.just(Collections.<User>emptyList());
		}
	}

	@Override public int getLimit() {
		return 1;
	}

	@Override public InfinityEventListener getEventListener(SwipeRefreshLayout refresh) {
		return mockEventListener;
	}
}
