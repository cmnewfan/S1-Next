package me.ykrank.s1next.view.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;

import com.google.common.base.Preconditions;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.SameItem;
import me.ykrank.s1next.util.ErrorUtil;
import me.ykrank.s1next.util.Objects;
import me.ykrank.s1next.view.adapter.delegate.ProgressAdapterDelegate;
import me.ykrank.s1next.view.adapter.item.FooterProgressItem;
import me.ykrank.s1next.view.adapter.item.ProgressItem;

public abstract class BaseRecyclerViewAdapter extends ListDelegationAdapter<List<Object>> {

    private static final int VIEW_TYPE_PROGRESS = 0;

    public BaseRecyclerViewAdapter(Context context) {
        setHasStableIds(false);
        setItems(new ArrayList<>());
        delegatesManager.addDelegate(VIEW_TYPE_PROGRESS, new ProgressAdapterDelegate(context));
    }

    final protected void addAdapterDelegate(AdapterDelegate<List<Object>> adapterDelegate) {
        Preconditions.checkArgument(delegatesManager.getViewType(adapterDelegate) != VIEW_TYPE_PROGRESS);
        delegatesManager.addDelegate(adapterDelegate);
    }

    public final void setHasProgress(boolean hasProgress) {
        if (hasProgress) {
            items.clear();
            items.add(new ProgressItem());
            notifyDataSetChanged();
        } else {
            // we do not need to clear list if we have already changed
            // data set or we have no ProgressItem to been cleared
            if (items.size() == 1 && items.get(0) instanceof ProgressItem) {
                items.clear();
                notifyDataSetChanged();
            }
        }
    }

    public final void showFooterProgress() {
        int position = getItemCount() - 1;
        Preconditions.checkState(getItem(position) != null);
        addItem(new FooterProgressItem());
        notifyItemInserted(position + 1);
    }

    public final void hideFooterProgress() {
        int position = getItemCount() - 1;
        Preconditions.checkState(getItem(position) instanceof FooterProgressItem);
        removeItem(position);
        notifyItemRemoved(position);
    }

    /**
     * diff new dataSet with old, and dispatch update.\n
     * must another object with old.
     *
     * @param newData     new data set
     * @param detectMoves {@link DiffUtil#calculateDiff}
     * @see DiffUtil
     */
    @SuppressWarnings("unchecked")
    public void diffNewDataSet(List<?> newData, boolean detectMoves) {
        if (items == newData) {
            refreshDataSet(newData, detectMoves);
            ErrorUtil.throwNewErrorIfDebug(new IllegalArgumentException("must set new data set"));
        }
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new BaseDiffCallback(items, newData), detectMoves);
        items = (List<Object>) newData;
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * refresh new dataSet.if same object, just notifyDataSetChanged, else {@link #diffNewDataSet}
     *
     * @param newData     new data set
     * @param detectMoves {@link #diffNewDataSet}
     */
    public final void refreshDataSet(List<?> newData, boolean detectMoves) {
        if (items != newData) {
            diffNewDataSet(newData, detectMoves);
        } else {
            notifyDataSetChanged();
        }
    }

    /**
     * swap to new dataSet.only notifyDataSetChanged
     *
     * @param newData new data set
     */
    @SuppressWarnings("unchecked")
    public final void swapDataSet(List<?> newData) {
        items = (List<Object>) newData;
        notifyDataSetChanged();
    }

    public final List<Object> getDataSet() {
        return getItems();
    }

    public final Object getItem(int position) {
        return items.get(position);
    }

    public final void addItem(Object object) {
        items.add(object);
    }

    public final void removeItem(int position) {
        items.remove(position);
    }

    public static class BaseDiffCallback extends DiffUtil.Callback {
        protected List<?> oldData, newData;

        public BaseDiffCallback(List<?> oldData, List<?> newData) {
            this.oldData = oldData;
            this.newData = newData;
        }

        @Override
        public int getOldListSize() {
            return oldData.size();
        }

        @Override
        public int getNewListSize() {
            return newData.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Object oldD = oldData.get(oldItemPosition);
            Object newD = newData.get(newItemPosition);
            if (oldD != null && oldD instanceof SameItem) {
                return ((SameItem) oldD).isSameItem(newD);
            }
            return Objects.equals(oldD, newD);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldData.get(oldItemPosition), newData.get(newItemPosition));
        }
    }
}
