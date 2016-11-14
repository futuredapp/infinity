package com.thefuntasty.infinity;

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
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"UnusedParameters", "unused"})
public abstract class InfinityAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements InfinityEventInterface {

	private static final int HEADER_VIEW_TYPE_OFFSET = 100001;
	private static final int FOOTER = -1;

	private @InfinityConstant.Status int loadingStatus = InfinityConstant.IDLE;

	private List<T> content = new ArrayList<>();
	private InfinityFiller<T> filler;
	private InfinityEventListener eventListener;

	private int limit = 20;
	private int offset = 0;
	private int visibleThreshold = 5;

	private boolean errorOccurred = false;
	private boolean footerVisible = false;
	private boolean pullToRefresh = false;
	private boolean startCalled = false;
	private boolean initialContent = false;

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
		onBindHeaderViewHolder(holder, position, Collections.emptyList());
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
				if (errorOccurred && loadingStatus != InfinityConstant.LOADING) {
					errorOccurred = false;
					tryAgain();
				}
			}
		});

		return new FooterViewHolder(footer);
	}

	private void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
		if (holder instanceof FooterViewHolder) {
			if (errorOccurred) {
				((FooterViewHolder) holder).loading.setVisibility(View.GONE);
				((FooterViewHolder) holder).tryAgain.setVisibility(View.VISIBLE);
			} else {
				((FooterViewHolder) holder).tryAgain.setVisibility(View.GONE);
				((FooterViewHolder) holder).loading.setVisibility(View.VISIBLE);
			}
			if (recyclerView != null && recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
				StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
				layoutParams.setFullSpan(true);
			}
		}
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

				if (!errorOccurred && !initialContent && loadingStatus == InfinityConstant.IDLE && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
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

				if (!errorOccurred && !initialContent && loadingStatus == InfinityConstant.IDLE && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
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

				if (!errorOccurred && !initialContent && loadingStatus == InfinityConstant.IDLE && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
					footerVisible = true;
					requestNextPostponed(recyclerView);
				}
			}
		};
		recyclerView.addOnScrollListener(onScrollListener);
	}

	private void requestNextPostponed(RecyclerView recyclerView) {
		recyclerView.post(new Runnable() {
			@Override public void run() {
				requestNext();
			}
		});
	}

	@Override
	public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
		recyclerView.removeOnScrollListener(onScrollListener);
	}

	private void requestFirst() {
		offset = 0;

		setLoading(InfinityConstant.FIRST_PAGE);
		onPreLoad(InfinityConstant.FIRST_PAGE);
		showFooter();
		filler.resetCallbacks(getFirstPageCallback(), getNextPageCallback());
		filler.onLoad(limit, offset, filler.getFirstPageCallback());
	}

	private void requestNext() {
		setLoading(InfinityConstant.NEXT_PAGE);
		onPreLoad(InfinityConstant.NEXT_PAGE);
		showFooter();
		filler.onLoad(limit, offset, filler.getNextPageCallback());
	}

	@NonNull private InfinityFiller.Callback<T> getFirstPageCallback() {
		return new InfinityFiller.Callback<T>() {
			@Override public void onData(List<T> list) {
				if (!interrupted) {
					addDataAndResolveState(list, InfinityConstant.FIRST_PAGE);
				}
			}

			@Override public void onError(Throwable error) {
				if (!interrupted) {
					errorOccurred = true;
					setIdle();
					onFirstUnavailable(error, pullToRefresh);
					showFooter();
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
					errorOccurred = true;
					setIdle();
					onNextUnavailable(error);
					showFooter();
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
		this.filler = filler;
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

	public List<T> getContentItems() {
		return content;
	}

	private void addDataAndResolveState(@NonNull List<T> data, @InfinityConstant.Part int part) {
		if (part == InfinityConstant.FIRST_PAGE) {
			content.clear();
			notifyDataSetChanged();
			content.addAll(data);
		} else {
			content.addAll(data);
		}

		initialContent = false;
		errorOccurred = false;
		showFooter();
		notifyItemRangeInserted(offset + getHeaderCount(), data.size());

		offset += data.size();

		if (part == InfinityConstant.FIRST_PAGE && data.size() == 0) { // no data
			loadingStatus = InfinityConstant.IDLE;
			onFirstEmpty(pullToRefresh);
		} else { // we have some data
			setIdle();
			onLoad(part);

			if (data.size() < limit) {
				setFinished();
			} else {
				// notify scroller about possibility loading next parts
				onScrollListener.onScrolled(recyclerView, recyclerView.getScrollX(), recyclerView.getScrollY());
			}
		}
	}

	private void showFooter() {
		if (!footerVisible) {
			footerVisible = true;
			notifyItemInserted(getItemCount());
		} else {
			refreshFooter();
		}
	}

	private void refreshFooter() {
		if (footerVisible) {
			notifyItemChanged(getItemCount() - 1);
		}
	}

	private void removeFooter() {
		if (footerVisible) {
			footerVisible = false;
			notifyItemRemoved(getItemCount());
		}
	}

	private void setIdle() {
		loadingStatus = InfinityConstant.IDLE;
	}

	private void onLoad(@InfinityConstant.Part int part) {
		if (part == InfinityConstant.FIRST_PAGE) {
			onFirstLoaded(pullToRefresh);
		} else {
			onNextLoaded();
		}
	}

	private void setLoading(@InfinityConstant.Part int part) {
		loadingStatus = InfinityConstant.LOADING;
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
		onFinished();
		removeFooter();
	}

	private void tryAgain() {
		requestNext();
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
