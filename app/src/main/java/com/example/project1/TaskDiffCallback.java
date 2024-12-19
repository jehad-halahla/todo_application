package com.example.project1;
import androidx.recyclerview.widget.DiffUtil;
import java.util.List;

public class TaskDiffCallback extends DiffUtil.Callback {
    private final List<Task> oldList;
    private final List<Task> newList;

    public TaskDiffCallback(List<Task> oldList, List<Task> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Compare by unique ID or any other unique property
        return oldList.get(oldItemPosition).getTitle() == newList.get(newItemPosition).getTitle();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Compare contents
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
