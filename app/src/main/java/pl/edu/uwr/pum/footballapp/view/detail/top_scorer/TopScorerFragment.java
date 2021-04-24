package pl.edu.uwr.pum.footballapp.view.detail.top_scorer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import pl.edu.uwr.pum.footballapp.databinding.TopscorerListBinding;
//import pl.edu.uwr.pum.footballapp.view.detail.top_scorer.;
import pl.edu.uwr.pum.footballapp.view.detail.top_scorer.TopScorerFragmentArgs;
import pl.edu.uwr.pum.footballapp.viewmodel.TopScorerViewModel;



public class TopScorerFragment extends Fragment {

    private TopscorerListBinding binding;

    private TopScorerViewModel viewModel;
    private TopScorerListAdapter adapter;

    private int id;
    private boolean calledFromTeam;

    public TopScorerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = TopscorerListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null)
        {
            id = TopScorerFragmentArgs.fromBundle(getArguments()).getId();
        }

        viewModel = new ViewModelProvider(this).get(TopScorerViewModel.class);
        viewModel.refreshListFromRemote(id);

        binding.recyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopScorerListAdapter(new ArrayList<>());
        binding.recyclerViewList.setAdapter(adapter);

        binding.swipeToRefresh.setOnRefreshListener(() -> {
            binding.recyclerViewList.setVisibility(View.GONE);
            binding.textViewError.setVisibility(View.GONE);
            binding.loadingProgress.setVisibility(View.VISIBLE);

            viewModel.refreshListFromRemote(id);
            binding.swipeToRefresh.setRefreshing(false);
        });

        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.scorers.observe(
                getViewLifecycleOwner(), scorers -> {
                    binding.recyclerViewList.setVisibility(View.VISIBLE);
                    adapter.updateList(scorers);
                }
        );
        viewModel.comp.observe(
                getViewLifecycleOwner(), competition -> {
                    binding.setCompetition(competition);
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