package pl.edu.uwr.pum.footballapp.view.detail.table;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import pl.edu.uwr.pum.footballapp.R;
import pl.edu.uwr.pum.footballapp.databinding.TableListBinding;
import pl.edu.uwr.pum.footballapp.viewmodel.TableViewModel;
import pl.edu.uwr.pum.footballapp.view.detail.table.TableFragmentDirections;

public class    TableFragment extends Fragment
implements TableFragmentListener{

    private TableListBinding binding;

    private TableViewModel viewModel;
    private TableListAdapter adapter;

    private int id;

    public TableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = TableListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null)
            id = TableFragmentArgs.fromBundle(getArguments()).getId();

        viewModel = new ViewModelProvider(this).get(TableViewModel.class);
        viewModel.refreshListFromRemote(id);

        binding.recyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TableListAdapter(new ArrayList<>());
        binding.recyclerViewList.setAdapter(adapter);

        binding.swipeToRefresh.setOnRefreshListener(() -> {
            binding.recyclerViewList.setVisibility(View.GONE);
            binding.textViewError.setVisibility(View.GONE);
            binding.loadingProgress.setVisibility(View.VISIBLE);

            viewModel.refreshListFromRemote(id);
            binding.swipeToRefresh.setRefreshing(false);
        });
        binding.setListener(this);
        binding.setListenerTable(viewModel);

        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.comps.observe(
                getViewLifecycleOwner(), competitions -> {
                    binding.recyclerViewList.setVisibility(View.VISIBLE);
                    adapter.updateList(competitions);
                }
        );

        viewModel.CompetitionLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    binding.textViewError.setVisibility(isError ? View.VISIBLE : View.GONE);
                }
        );

        viewModel.CompetitionLoading.observe(
                getViewLifecycleOwner(), isLoading -> {
                    binding.loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);

                    if(isLoading){
                        binding.textViewError.setVisibility(View.GONE);
                        binding.recyclerViewList.setVisibility(View.GONE);
                    }
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View view) {

        NavDirections action =
                TableFragmentDirections.actionTableFragmentToTopScorerFragment(id); //
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onMatchClick(View view) {
        NavDirections action =
                TableFragmentDirections.actionTableFragmentToMatchFragment(id,false); //
        Navigation.findNavController(view).navigate(action);
    }
}