package pl.edu.uwr.pum.footballapp.view.detail.top_scorer;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.edu.uwr.pum.footballapp.databinding.TopscorerRecyclerViewItemBinding;
import pl.edu.uwr.pum.footballapp.model.Competition;
import pl.edu.uwr.pum.footballapp.model.models.topscorer.ModelScorers;

public class TopScorerListAdapter
        extends RecyclerView.Adapter<TopScorerListAdapter.TopScorerViewHolder> {

    private final ArrayList<ModelScorers> scorers;

    public TopScorerListAdapter(ArrayList<ModelScorers> competitions) {
        this.scorers = competitions;
    }

    public void updateList(List<ModelScorers> newList) {
        scorers.clear();
        scorers.addAll(newList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TopScorerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TopScorerViewHolder(
                TopscorerRecyclerViewItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TopScorerListAdapter.TopScorerViewHolder holder, int position) {
        holder.binding.setPlayer(scorers.get(position));
        //holder.binding.setListener(this);
    }

    @Override
    public int getItemCount() {
        return scorers.size();
    }


    static class TopScorerViewHolder extends RecyclerView.ViewHolder {

        TopscorerRecyclerViewItemBinding binding;

        public TopScorerViewHolder(@NonNull TopscorerRecyclerViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
