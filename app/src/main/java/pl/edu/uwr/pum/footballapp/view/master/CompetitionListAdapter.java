package pl.edu.uwr.pum.footballapp.view.master;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.edu.uwr.pum.footballapp.R;
import pl.edu.uwr.pum.footballapp.databinding.RecyclerViewItemBinding;
import pl.edu.uwr.pum.footballapp.model.Competition;

public class CompetitionListAdapter
        extends RecyclerView.Adapter<CompetitionListAdapter.CompetitionViewHolder>
        implements CompetitionClickListener {

    private ArrayList<Competition> competitions;

    public CompetitionListAdapter(ArrayList<Competition> competitions) {
        this.competitions = competitions;
    }

    public void updateList(List<Competition> newList) {
        competitions.clear();
        competitions.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CompetitionListAdapter.CompetitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CompetitionViewHolder(
                RecyclerViewItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CompetitionListAdapter.CompetitionViewHolder holder, int position) {
        holder.binding.setCompetition(competitions.get(position));
        holder.binding.setListener(this);
    }

    @Override
    public int getItemCount() {
        return competitions.size();
    }

    @Override
    public void onCompetitionClicked(View view) {
        String uuidString = ((TextView) view.findViewById(R.id.id)).getText().toString();
        int uuid = Integer.parseInt(uuidString);
        ListFragmentDirections.ActionListFragmentToTableFragment action =
                ListFragmentDirections.actionListFragmentToTableFragment(uuid);
        Navigation.findNavController(view).navigate(action);
    }

    static class CompetitionViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewItemBinding binding;

        public CompetitionViewHolder(@NonNull RecyclerViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
