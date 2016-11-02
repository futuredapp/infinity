# infinity

Tiny library with ability to provide support for infinite paginated scrolling in `RecyclerView`. It is able to show loading & try again layout at the bottom of the list.

[![](https://jitpack.io/v/thefuntasty/infinity.svg)](https://jitpack.io/#thefuntasty/infinity)
<a href="https://travis-ci.org/thefuntasty/infinity"><img src="https://travis-ci.org/thefuntasty/infinity.svg?branch=master"></a>
<p align="center"><img src="https://github.com/thefuntasty/infinity/blob/master/images/infinity.gif?raw=true" height="426" width="240"></p>

# Usage

## Minimal implementation

1. Implement you own `RecyclerView` adapter which extends `InfinityAdapter<Model, ViewHolder>`

	```java
	public class SampleUserAdapter extends InfinityAdapter<User, ViewHolder> {

		@Override
		public ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			View view = inflater.inflate(R.layout.list_item_user, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindContentViewHolder(ViewHolder holder, int position) {
			((TextView) holder.itemView).setText(getContentItem(position).toString());
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			public ViewHolder(View itemView) {
				super(itemView);
			}
		}
	}
```

2. Create instance of `InfinityFiller<Model>` class. It serves as a paginated data provider to your adapter.

	```java 		
	InfinityFiller<User> filler = new InfinityFiller<User>() {
		@Override public void onLoad(final int limit, final int offset, final Callback<User> callback) {
			// Async data provider
			DataManager.get().getDataObservable(limit, offset)
					.subscribe(new Action1<List<User>>() {
						@Override public void call(List<User> users) {
							callback.onData(users); // call onData() when data available
						}
					}, new Action1<Throwable>() {
						@Override public void call(Throwable throwable) {
							callback.onError(throwable); // call onError() when error occurs
						}
					});
		}
	};
	```

3. Assign filler to adapter and start loading

	```java
	final SampleUserAdapter adapter = new SampleUserAdapter();
	recyclerView.setAdapter(adapter);
	adapter.setFiller(filler);
	adapter.start();
	```

## Handy features

### Change error/loading layout at the bottom of the list

Simply override method `getFooterLayout()` in your adapter and return XML layout resource what contains 2 views with id's `@+id/loading` and `@+id/try_again`.

1. `footer_layout.xml`
	```xml
	<LinearLayout
		android:layout_width="match_parent"
		android:layout_height="wrap_content">
	
		<LinearLayout
			android:id="@+id/loading"
			android:layout_width="...">
	
			<!-- content -->
		</LinearLayout>
	
		<LinearLayout
			android:id="@+id/try_again"
			android:layout_width="...">
	
			<!-- content -->
		</LinearLayout>
	</LinearLayout>
	```

2. Adapter class
	```java
		@Override
		public int getFooterLayout() {
			return R.layout.footer_layout;
		}
	```

### Event listener

If you want to know about data loading events, you can set event listener via `adapter.setEventListener()` method.

```java
adapter.setEventListener(new InfinityEventListener() {

	@Override public void onPreLoadFirst(boolean pullToRefresh) { }

	@Override public void onFirstLoaded(boolean pullToRefresh) { }

	@Override public void onFirstUnavailable(Throwable error, boolean pullToRefresh) { }

	@Override public void onFirstEmpty(boolean pullToRefresh) { }

	@Override public void onPreLoadNext() { }

	@Override public void onNextLoaded() { }

	@Override public void onNextUnavailable(Throwable error) { }

	@Override public void onFinished() { }
});
```
### ViewType support

View types are supported by default. All you need to do is to override `getContentItemViewType()` method of your adapter class. View type value returned by this method must be in range <0,49>.

```java
public static final int LEFT = 0;
public static final int RIGHT = 1;

@Override
public int getContentItemViewType(int position) {
	return position % 2 == 0 ? LEFT : RIGHT;
}
```

### Header support

In scenario you want to add header (or headers) at the top of the recycler, implement particular methods in your adapter class.

```java
public static final int HEADER_TYPE_FIRST = 50;
public static final int HEADER_TYPE_SECOND = 51;

@Override public ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
	if (viewType == HEADER_TYPE_FIRST) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header_type_1, parent, false);
		return new ViewHolder(view);
	} else {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header_type_2, parent, false);
		return new ViewHolder(view);
	}
}

@Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
	// ...
}

@Override public int getHeaderCount() {
	return NUM_OF_HEADERS;
}

@Override public int getHeaderItemViewType(int position) {
	if (position == 0) {
		return HEADER_TYPE_FIRST;
	} else {
		return HEADER_TYPE_SECOND;
	}
}
```

There are two limitations:
- Header view must share `ViewHolder` with content views. You can solve this problem by base `ViewHolder`.
- Header view type values must be in range <50, 100>

# Download

```groovy
dependencies {
	compile 'com.github.thefuntasty:infinity:{version}}'
}
```

# License

	MIT License

	Copyright (c) 2016 FUNTASTY Digital s.r.o.

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	urnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
