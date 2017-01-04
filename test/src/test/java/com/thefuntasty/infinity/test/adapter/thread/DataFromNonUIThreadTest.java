package com.thefuntasty.infinity.test.adapter.thread;

import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;

import com.thefuntasty.infinity.InfinityEventListener;
import com.thefuntasty.infinity.InfinityException;
import com.thefuntasty.infinity.test.App;
import com.thefuntasty.infinity.test.BuildConfig;
import com.thefuntasty.infinity.test.User;
import com.thefuntasty.infinity.test.adapter.BaseAdapterTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M, application = App.class)
public class DataFromNonUIThreadTest extends BaseAdapterTest {

	private CountDownLatch lock = new CountDownLatch(1);
	@Mock InfinityEventListener mockEventListener;
	@Captor ArgumentCaptor<Throwable> captor;

	@Test
	public void dataFromBadThread() throws Exception {
		lock.await(1, TimeUnit.SECONDS);

		verify(mockEventListener).onFirstUnavailable(captor.capture(), eq(false));
		assertThat(captor.getValue()).hasMessage("Callback methods onData() & onError() must be called from UI Thread");
		assertThat(captor.getValue()).isExactlyInstanceOf(InfinityException.class);
	}

	@Override
	public Observable<List<User>> getDataObservable(int limit, int offset) {
		return Observable.just(Collections.<User>emptyList())
				.doOnUnsubscribe(() -> lock.countDown())
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io());
	}

	@Override public InfinityEventListener getEventListener(SwipeRefreshLayout refresh) {
		return mockEventListener;
	}
}
