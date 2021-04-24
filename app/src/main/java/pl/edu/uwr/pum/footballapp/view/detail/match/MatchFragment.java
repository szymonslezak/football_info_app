package pl.edu.uwr.pum.footballapp.view.detail.match;

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

import pl.edu.uwr.pum.footballapp.viewmodel.MatchViewModel;

import pl.edu.uwr.pum.footballapp.databinding.MatchListBinding;
import pl.edu.uwr.pum.footballapp.view.detail.match.MatchFragmentArgs;


public class MatchFragment extends Fragment {

    private MatchListBinding binding;

    private MatchViewModel viewModel;
    private MatchListAdapter adapter;

    private int id;
    private boolean calledFromTeam;

    public MatchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = MatchListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null)
        {
            id = MatchFragmentArgs.fromBundle(getArguments()).getId();
            calledFromTeam = MatchFragmentArgs.fromBundle(getArguments()).getCalledTeam();
        }

        viewModel = new ViewModelProvider(this).get(MatchViewModel.class);
        viewModel.refreshListFromRemote(id,calledFromTeam);

        binding.recyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MatchListAdapter(new ArrayList<>());
        binding.recyclerViewList.setAdapter(adapter);

        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.matches.observe(
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