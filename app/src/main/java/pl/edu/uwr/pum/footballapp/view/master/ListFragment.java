package pl.edu.uwr.pum.footballapp.view.master;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import pl.edu.uwr.pum.footballapp.databinding.FragmentListBinding;
import pl.edu.uwr.pum.footballapp.viewmodel.ListViewModel;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;

    private ListViewModel viewModel;
    private CompetitionListAdapter adapter;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ListViewModel.class);
        viewModel.refreshList();

        binding.recyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CompetitionListAdapter(new ArrayList<>());
        binding.recyclerViewList.setAdapter(adapter);

        binding.swipeToRefresh.setOnRefreshListener(() -> {
            binding.recyclerViewList.setVisibility(View.GONE);
            binding.textViewError.setVisibility(View.GONE);
            binding.loadingProgress.setVisibility(View.VISIBLE);

            viewModel.refreshListFromRemote();
            binding.swipeToRefresh.setRefreshing(false);
        });

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
}