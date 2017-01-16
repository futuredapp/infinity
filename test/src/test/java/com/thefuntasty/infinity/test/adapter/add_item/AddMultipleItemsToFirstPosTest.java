package com.thefuntasty.infinity.test.adapter.add_item;

import android.os.Build;
import android.widget.TextView;

import com.thefuntasty.infinity.InfinityAdapter;
import com.thefuntasty.infinity.InfinityConstant;
import com.thefuntasty.infinity.test.App;
import com.thefuntasty.infinity.test.BuildConfig;
import com.thefuntasty.infinity.test.SampleUserAdapter;
import com.thefuntasty.infinity.test.User;
import com.thefuntasty.infinity.test.adapter.BaseRxAdapterTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M, application = App.class)
public class AddMultipleItemsToFirstPosTest extends BaseRxAdapterTest {

	private SampleUserAdapter adapter;
	private User user1 = new User("alfa", "beta");
	private User user2 = new User("gama", "delta");
	private User user3 = new User("epsilon", "phi");

	@Test
	public void addToFirst() {
		ArrayList<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);

		adapter.addItems(0, users);
		invalidateRecyclerView();

		TextView zeroPos = ((TextView) recyclerView.findViewHolderForAdapterPosition(0).itemView);
		TextView firstPos = ((TextView) recyclerView.findViewHolderForAdapterPosition(1).itemView);
		TextView thirdPos = ((TextView) recyclerView.findViewHolderForAdapterPosition(2).itemView);

		assertThat(recyclerView.getAdapter().getItemCount()).isEqualTo(3);
		assertThat(zeroPos.getText()).isEqualTo(user1.toString());
		assertThat(firstPos.getText()).isEqualTo(user2.toString());
		assertThat(thirdPos.getText()).isEqualTo(user3.toString());

		assertThat(adapter.getCurrentLoadingStatus()).isEqualTo(InfinityConstant.FINISHED);
	}

	@Override public Observable<List<User>> getDataObservable(int limit, int offset) {
		ArrayList<User> users = new ArrayList<>();
		users.add(user3);
		return Observable.just(users);
	}

	@Override public InfinityAdapter<User, ?> getAdapter() {
		this.adapter = new SampleUserAdapter() {
			@Override public int getHeaderCount() {
				return 0;
			}
		};
		return adapter;
	}
}
