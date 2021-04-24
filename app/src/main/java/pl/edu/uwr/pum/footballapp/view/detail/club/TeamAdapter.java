package pl.edu.uwr.pum.footballapp.view.detail.club;

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
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeamSquad;
import pl.edu.uwr.pum.footballapp.databinding.TeamRecyclerViewItemBinding;

public class TeamAdapter
        extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder>
    {

    private final ArrayList<ModelTeamSquad> squads;

    public TeamAdapter(ArrayList<ModelTeamSquad> squads) {
        this.squads = squads;
    }

    public void updateList(List<ModelTeamSquad> newList) {
        squads.clear();
        squads.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TeamViewHolder(
                TeamRecyclerViewItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        holder.binding.setPlayer(squads.get(position));
    }

    @Override
    public int getItemCount() {
        return squads.size();
    }


    static class TeamViewHolder extends RecyclerView.ViewHolder {

        TeamRecyclerViewItemBinding binding;

        public TeamViewHolder(@NonNull TeamRecyclerViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
