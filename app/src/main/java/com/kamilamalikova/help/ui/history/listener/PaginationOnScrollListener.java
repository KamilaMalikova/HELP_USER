package com.kamilamalikova.help.ui.history.listener;

import android.widget.AbsListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationOnScrollListener implements AbsListView.OnScrollListener {
    private LinearLayoutManager layoutManager;

    public PaginationOnScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItem) >= totalItemCount
                    && firstVisibleItem >= 0) {
                loadMoreItems();
            }
        }
    }

//    @Override
//    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//        super.onScrolled(recyclerView, dx, dy);
//
//        int visibleItemCount = layoutManager.getChildCount();
//        int totalItemCount = layoutManager.getItemCount();
//        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//        if (!isLoading() && !isLastPage()) {
//            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                    && firstVisibleItemPosition >= 0) {
//                loadMoreItems();
//            }
//        }
//    }

    protected abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}
