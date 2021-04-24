package pl.edu.uwr.pum.footballapp.view.detail.table;

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
import pl.edu.uwr.pum.footballapp.databinding.TableRecyclerViewItemBinding;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTable;
import pl.edu.uwr.pum.footballapp.view.detail.table.TableFragmentDirections;

public class TableListAdapter
        extends RecyclerView.Adapter<TableListAdapter.TableViewHolder>
        implements TableClickListener {

    private ArrayList<CompetitionTable> competitions;

    public TableListAdapter(ArrayList<CompetitionTable> competitions) {
        this.competitions = competitions;
    }

    public void updateList(List<CompetitionTable> newList) {
        competitions.clear();
        competitions.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TableListAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TableViewHolder(
                TableRecyclerViewItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TableListAdapter.TableViewHolder holder, int position) {
        holder.binding.setCompetitionTable(competitions.get(position));
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
        NavDirections action =
                TableFragmentDirections.actionTableFragmentToTeamFragment(uuid); //
        Navigation.findNavController(view).navigate(action);
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {

        TableRecyclerViewItemBinding binding;

        public TableViewHolder(@NonNull TableRecyclerViewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
