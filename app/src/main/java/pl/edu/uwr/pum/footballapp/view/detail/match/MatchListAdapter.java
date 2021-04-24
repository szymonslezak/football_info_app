package pl.edu.uwr.pum.footballapp.view.detail.match;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.edu.uwr.pum.footballapp.R;
import pl.edu.uwr.pum.footballapp.model.models.match.ModelMatch;
import pl.edu.uwr.pum.footballapp.databinding.MatchRecyclerViewItemBinding;

public class MatchListAdapter
        extends RecyclerView.Adapter<MatchListAdapter.MatchViewHolder> {

    private final ArrayList<ModelMatch> matches;

    public MatchListAdapter(ArrayList<ModelMatch> competitions) {
        this.matches = competitions;
    }

    public void updateList(List<ModelMatch> newList) {
        matches.clear();
        matches.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MatchViewHolder(
                MatchRecyclerViewItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MatchListAdapter.MatchViewHolder holder, int position) {
        holder.binding.setMatch(matches.get(position));
        //holder.binding.setListener(this);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }


    static class MatchViewHolder extends RecyclerView.ViewHolder {

        MatchRecyclerViewItemBinding binding;

        public MatchViewHolder(@NonNull MatchRecyclerViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
