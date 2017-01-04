package com.thefuntasty.infinity;

import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"UnusedParameters", "unused"})
public abstract class InfinityAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements InfinityEventInterface {

	private static final int HEADER_VIEW_TYPE_OFFSET = 100001;
	private static final int FOOTER = -1;

	private @InfinityConstant.Status int loadingStatus = InfinityConstant.IDLE;

	private List<T> content = new ArrayList<>();
	private InfinityFillerImpl<T> filler;
	private InfinityEventListener eventListener;

	private int limit = 20;
	private int offset = 0;
	private int visibleThreshold = 5;

	private boolean footerVisible = false;
	private boolean pullToRefresh = false;
	private boolean startCalled = false;
	private boolean initialContent = false;
	private boolean isRestarted = false;

	private RecyclerView.OnScrollListener onScrollListener;
	private RecyclerView recyclerView;

	@SuppressWarnings("unchecked")
	@Override public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType > HEADER_VIEW_TYPE_OFFSET) {
			return (VH) onCreateHeaderViewHolder(parent, viewType - HEADER_VIEW_TYPE_OFFSET);
		} else if (viewType == FOOTER) {
			return (VH) onCreateFooterViewHolder(parent);
		} else {
			return onCreateContentViewHolder(parent, viewType);
		}
	}

	@Override public final void onBindViewHolder(VH holder, int position) {
		int viewType = getItemViewType(position);
		if (viewType > HEADER_VIEW_TYPE_OFFSET) {
			onBindHeaderViewHolder(holder, position);
		} else if (viewType == FOOTER) {
			onBindFooterViewHolder(holder);
		} else {
			onBindContentViewHolder(holder, position - getHeaderCount());
		}
	}

	@Override public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
		int viewType = getItemViewType(position);
		if (viewType > HEADER_VIEW_TYPE_OFFSET) {
			if (payloads.isEmpty()) {
				onBindHeaderViewHolder(holder, position);
			} else {
				onBindHeaderViewHolder(holder, position, payloads);
			}
		} else if (viewType == FOOTER) {
			onBindFooterViewHolder(holder);
		} else {
			if (payloads.isEmpty()) {
				onBindContentViewHolder(holder, position - getHeaderCount());
			} else {
				onBindContentViewHolder(holder, position - getHeaderCount(), payloads);
			}
		}
	}

	/**
	 * Return total item count in adapter including headers and loading/refresh footer
	 *
	 * @return number of items in adapter
	 */
	@Override public final int getItemCount() {
		return getHeaderCount() + (content != null ? content.size() : 0) + (footerVisible ? 1 : 0);
	}

	/**
	 * Returns content (!) items count. Do not include headers and loading/refresh footer
	 *
	 * @return number of content items
	 */
	public final int getContentItemCount() {
		return content.size();
	}

	@Override public final int getItemViewType(int position) {
		if (position < getHeaderCount()) {
			return getHeaderItemViewType(position) + HEADER_VIEW_TYPE_OFFSET;
		} else if (position < getHeaderCount() + content.size()) {
			return getContentItemViewType(position);
		} else {
			return FOOTER;
		}
	}

	/**
	 * Sets initial collection what should be show before loading has been started
	 *
	 * @param initialCollection reference to initial collection
	 */
	public void setInitialContent(List<T> initialCollection) {
		initialContent = true;
		content.clear();
		content.addAll(initialCollection);
		notifyDataSetChanged();
	}

	/**
	 * Get number of headers
	 *
	 * @return number of headers
	 */
	public int getHeaderCount() {
		return 0;
	}

	/**
	 * Gets header view type at specified position. Must not be negative value. Must not collide with
	 * content view types
	 *
	 * @param position item position
	 * @return header header(!) item view type
	 */
	public @IntRange(from = 50, to = 100) int getHeaderItemViewType(int position) {
		return 50;
	}

	/**
	 * Create ViewHolder for header item
	 *
	 * @param parent ViewHolder's parent
	 * @return created header item's ViewHolder
	 */
	public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
		return null;
	}

	/**
	 * Bind header with provided ViewHolder at specified position
	 *
	 * @param holder   header's view holder
	 * @param position header's position
	 */
	public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

	}

	/**
	 * Bind header with provided ViewHolder at specified position
	 *
	 * @param holder   header's view holder
	 * @param position header's position
	 * @param payloads custom update payloads
	 */
	public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
		onBindHeaderViewHolder(holder, position);
	}

	/**
	 * Provides FooterViewHolder after onBind action
	 *
	 * @param footerViewHolder footer's view holder
	 */
	protected void onUpdateFooterViewHolder(FooterViewHolder footerViewHolder) { }

	/**
	 * Create ViewHolder for content item
	 *
	 * @param parent ViewHolder's parent
	 * @return created content item's ViewHolder
	 */
	public abstract VH onCreateContentViewHolder(ViewGroup parent, int viewType);

	/**
	 * Bind content with provided ViewHolder at specified position
	 *
	 * @param holder   header's view holder
	 * @param position header's position
	 */
	public abstract void onBindContentViewHolder(VH holder, int position);

	/**
	 * Bind content with provided ViewHolder at specified position
	 *
	 * @param holder   header's view holder
	 * @param position header's position
	 * @param payloads custom update payloads
	 */
	public void onBindContentViewHolder(VH holder, int position, List<Object> payloads) {
		onBindContentViewHolder(holder, position);
	}

	/**
	 * Gets item view type at specified position. Must not be negative value. Must not collide with
	 * headers' view types
	 *
	 * @param position item position
	 * @return content item view type
	 */
	public @IntRange(from = 0, to = 49) int getContentItemViewType(int position) {
		return 0;
	}

	private RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
		View footer = LayoutInflater.from(parent.getContext()).inflate(getFooterLayout(), parent, false);
		footer.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if (loadingStatus == InfinityConstant.ERROR) {
					tryAgain();
				}
			}
		});

		return new FooterViewHolder(footer);
	}

	private void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
		if (holder instanceof FooterViewHolder) {
			FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
			onBindFooterViewHolder(footerViewHolder);
		}
	}

	private void onBindFooterViewHolder(FooterViewHolder footerViewHolder) {
		if (loadingStatus == InfinityConstant.ERROR) {
			footerViewHolder.loading.setVisibility(View.GONE);
			footerViewHolder.retry.setVisibility(View.VISIBLE);
		} else {
			footerViewHolder.loading.setVisibility(View.VISIBLE);
			footerViewHolder.retry.setVisibility(View.GONE);
		}
		if (recyclerView != null && recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
			StaggeredGridLayoutManager.LayoutParams layoutParams =
					(StaggeredGridLayoutManager.LayoutParams) footerViewHolder.itemView.getLayoutParams();
			layoutParams.setFullSpan(true);
		}

		onUpdateFooterViewHolder(footerViewHolder);
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		this.recyclerView = recyclerView;
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		if (layoutManager instanceof GridLayoutManager) {
			onAttachedGridLayoutManager(recyclerView);
		} else if (layoutManager instanceof LinearLayoutManager) {
			onAttachedLinearLayoutManager(recyclerView);
		} else if (layoutManager instanceof StaggeredGridLayoutManager) {
			onAttachedStaggeredGridManager(recyclerView);
		}
	}

	private void onAttachedStaggeredGridManager(RecyclerView recyclerView) {
		final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
		onScrollListener = new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				int visibleItemCount = recyclerView.getChildCount();
				int totalItemCount = staggeredGridLayoutManager.getItemCount();
				int[] positions = new int[staggeredGridLayoutManager.getSpanCount()];

				staggeredGridLayoutManager.findLastVisibleItemPositions(positions);
				int lastVisibleItem = findMax(positions);
				staggeredGridLayoutManager.findFirstVisibleItemPositions(positions);
				int firstVisibleItem = findMin(positions);

				if (!initialContent && loadingStatus == InfinityConstant.IDLE && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
					footerVisible = true;
					requestNextPostponed(recyclerView);
				}
			}
		};
		recyclerView.addOnScrollListener(onScrollListener);
	}

	private void onAttachedGridLayoutManager(final RecyclerView recyclerView) {
		final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override public int getSpanSize(int position) {
				int viewType = getItemViewType(position);
				if (viewType > HEADER_VIEW_TYPE_OFFSET || viewType == FOOTER) {
					return gridLayoutManager.getSpanCount();
				} else {
					return 1;
				}
			}
		});
		onScrollListener = new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
				int visibleItemCount = recyclerView.getChildCount();
				int totalItemCount = gridLayoutManager.getItemCount();

				if (!initialContent && loadingStatus == InfinityConstant.IDLE && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
					footerVisible = true;
					requestNextPostponed(recyclerView);
				}
			}
		};
		recyclerView.addOnScrollListener(onScrollListener);
	}

	private void onAttachedLinearLayoutManager(RecyclerView recyclerView) {
		final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
		onScrollListener = new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
				int visibleItemCount = recyclerView.getChildCount();
				int totalItemCount = linearLayoutManager.getItemCount();

				if (!initialContent && loadingStatus == InfinityConstant.IDLE && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
					footerVisible = true;
					requestNextPostponed(recyclerView);
				}
			}
		};
		recyclerView.addOnScrollListener(onScrollListener);
	}



	@Override
	public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
		recyclerView.removeOnScrollListener(onScrollListener);
	}

	private void requestFirst() {
		offset = 0;

		setLoading(InfinityConstant.FIRST_PAGE);
		onPreLoad(InfinityConstant.FIRST_PAGE);
		refreshFooter();
		filler.resetCallbacks(getFirstPageCallback(), getNextPageCallback());
		filler.onLoad(limit, offset, filler.getFirstPageCallback());
	}

	private void requestNextPostponed(RecyclerView recyclerView) {
		setLoading(InfinityConstant.NEXT_PAGE);
		recyclerView.post(new Runnable() {
			@Override public void run() {
				onPreLoad(InfinityConstant.NEXT_PAGE);
				filler.onLoad(limit, offset, filler.getNextPageCallback());
			}
		});
	}

	@NonNull private InfinityFiller.Callback<T> getFirstPageCallback() {
		return new InfinityFiller.Callback<T>() {
			@Override public void onData(List<T> list) {
				if (!interrupted) {
					if (isRestarted) {
						content.clear();
						notifyDataSetChanged();
					}
					addDataAndResolveState(list, InfinityConstant.FIRST_PAGE);
				}
			}

			@Override public void onError(Throwable error) {
				if (!interrupted) {
					setError();
					onFirstUnavailable(error, pullToRefresh);
				}
			}
		};
	}

	@NonNull private InfinityFiller.Callback<T> getNextPageCallback() {
		return new InfinityFiller.Callback<T>() {
			@Override public void onData(List<T> list) {
				if (!interrupted) {
					addDataAndResolveState(list, InfinityConstant.NEXT_PAGE);
				}
			}

			@Override public void onError(Throwable error) {
				if (!interrupted) {
					setError();
					onNextUnavailable(error);
				}
			}
		};
	}

	public boolean isStarted() {
		return startCalled;
	}

	public void start() {
		if (!startCalled) {
			requestFirst();
			startCalled = true;
		} else {
			throw new IllegalStateException("start() can be called only once. Use restart instead");
		}
	}

	public void restart() {
		restart(false);
	}

	public void restart(boolean pullToRefresh) {
		footerVisible = false;
		this.pullToRefresh = pullToRefresh;
		this.isRestarted = true;
		requestFirst();
	}

	/**
	 * Method to define threshold indicating if next page should be loaded. If you want to increase
	 * amount of invisible items, increase this value.
	 *
	 * @param visibleThreshold threshold size. Default value = 5
	 */
	public void setVisibleThreshold(int visibleThreshold) {
		this.visibleThreshold = visibleThreshold;
	}

	/**
	 * Sets limit value. If limit is not exceeded after data load, infinity expects there is no more
	 * data and onFinished() is called
	 *
	 * @param limit limit value
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Sets event listener what provides info during data load
	 *
	 * @param eventListener instance of
	 */
	public void setEventListener(InfinityEventListener eventListener) {
		this.eventListener = eventListener;
	}

	/**
	 * Sets data filler
	 *
	 * @param filler filler from what data are obtained
	 */
	public void setFiller(@NonNull InfinityFiller<T> filler) {
		this.filler = new InfinityFillerImpl<>(filler);
	}

	/**
	 * Gets content item at particular position.
	 *
	 * @param position index of returned item
	 * @return content item
	 */
	public T getContentItem(int position) {
		return content.get(position);
	}

	/**
	 * Gets all content items
	 *
	 * @return list of current content items
	 */
	public List<T> getContentItems() {
		return content;
	}

	/**
	 * Add item to content at specific position. If restart is called, item is removed with rest of content.
	 *
	 * @param pos  position where item will be added. If position is bigger than content size, item will be added to last position.
	 * @param item item which will be added to the content
	 */
	public void addItem(int pos, T item) {
		if (pos >= content.size()) {
			content.add(item);
			notifyItemInserted(getHeaderCount() + content.size() - 1);
		} else {
			content.add(pos, item);
			notifyItemInserted(getHeaderCount() + pos);
		}
		offset += 1;
	}

	/**
	 * Retrieves current status of item load
	 *
	 * @return one of the statuses: ERROR, IDLE, LOADING, FINISHED
	 */
	public @InfinityConstant.Status int getCurrentLoadingStatus() {
		return loadingStatus;
	}

	private void addDataAndResolveState(@NonNull List<T> data, @InfinityConstant.Part int part) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new InfinityException("Callback methods onData() & onError() must be called from UI Thread");
		}

		content.addAll(data);

		initialContent = false;
		setIdle();
		notifyItemRangeInserted(offset + getHeaderCount(), data.size());

		offset += data.size();

		if (part == InfinityConstant.FIRST_PAGE && data.size() == 0) { // no data
			onFirstEmpty(pullToRefresh);
			setFinished();
		} else { // we have some data
			onLoad(part);

			if (data.size() < limit) {
				setFinished();
			} else {
				// notify scroller about possibility loading next parts
				onScrollListener.onScrolled(recyclerView, recyclerView.getScrollX(), recyclerView.getScrollY());
			}
		}
	}

	private void refreshFooter() {
		if (!footerVisible) { // add footer
			footerVisible = true;
			notifyItemInserted(getItemCount());
		} else { // refresh content
			notifyItemChanged(getItemCount() - 1);
		}
	}

	private void removeFooter() {
		if (footerVisible) {
			footerVisible = false;
			notifyItemRemoved(getItemCount());
		}
	}

	private void setError() {
		loadingStatus = InfinityConstant.ERROR;
		refreshFooter();
	}

	private void setIdle() {
		loadingStatus = InfinityConstant.IDLE;
		refreshFooter();
	}

	private void setLoading(@InfinityConstant.Part int part) {
		loadingStatus = InfinityConstant.LOADING;
		refreshFooter();
	}

	private void onLoad(@InfinityConstant.Part int part) {
		if (part == InfinityConstant.FIRST_PAGE) {
			recyclerView.scrollToPosition(0);
			onFirstLoaded(pullToRefresh);
		} else {
			onNextLoaded();
		}
	}

	private void onPreLoad(@InfinityConstant.Part int part) {
		if (part == InfinityConstant.FIRST_PAGE) {
			onPreLoadFirst(pullToRefresh);
		} else {
			onPreLoadNext();
		}
	}

	private void setFinished() {
		loadingStatus = InfinityConstant.FINISHED;
		removeFooter();
		onFinished();
	}

	private void tryAgain() {
		requestNextPostponed(recyclerView);
	}

	public @LayoutRes int getFooterLayout() {
		return R.layout.footer_layout;
	}

	public final void onPreLoadFirst(boolean pullToRefresh) {
		if (eventListener != null) {
			eventListener.onPreLoadFirst(pullToRefresh);
		}
	}

	@Override
	public final void onPreLoadNext() {
		if (eventListener != null) {
			eventListener.onPreLoadNext();
		}
	}

	@Override
	public final void onFirstLoaded(boolean pullToRefresh) {
		if (eventListener != null) {
			eventListener.onFirstLoaded(pullToRefresh);
		}
	}

	@Override
	public final void onNextLoaded() {
		if (eventListener != null) {
			eventListener.onNextLoaded();
		}
	}

	@Override
	public final void onFirstUnavailable(Throwable error, boolean pullToRefresh) {
		if (eventListener != null) {
			eventListener.onFirstUnavailable(error, pullToRefresh);
		}
	}

	@Override
	public final void onFirstEmpty(boolean pullToRefresh) {
		if (eventListener != null) {
			eventListener.onFirstEmpty(pullToRefresh);
		}
	}

	@Override
	public final void onNextUnavailable(Throwable error) {
		if (eventListener != null) {
			eventListener.onNextUnavailable(error);
		}
	}

	@Override
	public final void onFinished() {
		if (eventListener != null) {
			eventListener.onFinished();
		}
	}

	private int findMax(int[] lastPositions) {
		int max = Integer.MIN_VALUE;
		for (int value : lastPositions) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	private int findMin(int[] lastPositions) {
		int min = Integer.MAX_VALUE;
		for (int value : lastPositions) {
			if (value != RecyclerView.NO_POSITION && value < min) {
				min = value;
			}
		}
		return min;
	}
}
