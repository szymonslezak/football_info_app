package pl.edu.uwr.pum.footballapp.view.detail.club;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import pl.edu.uwr.pum.footballapp.databinding.TeamDetailBinding;


import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeamFav;

import pl.edu.uwr.pum.footballapp.viewmodel.TeamViewModel;


public class    TeamFragment extends Fragment
implements  TeamClickListener{

    private TeamDetailBinding binding;

    private TeamViewModel viewModel;
    private TeamAdapter adapter;

    private int id;

    public TeamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = TeamDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null)
            id = TeamFragmentArgs.fromBundle(getArguments()).getId();

        viewModel = new ViewModelProvider(this).get(TeamViewModel.class);
        viewModel.refreshListFromRemote(id);

        binding.recyclerViewSquad.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeamAdapter(new ArrayList<>());
        binding.recyclerViewSquad.setAdapter(adapter);
        binding.setListener(this);

        observeViewModel();
    }

    private void observeViewModel(){
        viewModel._team.observe(
                getViewLifecycleOwner(), team -> {
                    if(team != null)
                    {
                        if(binding.recyclerViewSquad.getVisibility() == View.GONE)
                    binding.ClubContainer.setVisibility(View.VISIBLE);
                    //binding.recyclerViewSquad.setVisibility(View.VISIBLE);
                    binding.setTeam(team);
                    if(team.squad != null)
                    adapter.updateList(team.squad);
                    }
                }
        );

        viewModel.CompetitionLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    binding.textViewError.setVisibility(isError ? View.VISIBLE : View.GONE);
                }
        );
        viewModel._team_fav.observe(
                getViewLifecycleOwner(), team ->{
                    binding.teamFavourite.setChecked(team.favourite);
                }
        );

        viewModel.CompetitionLoading.observe(
                getViewLifecycleOwner(), isLoading -> {
                    binding.loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);

                    if(isLoading){
                        binding.textViewError.setVisibility(View.GONE);
                        binding.recyclerViewSquad.setVisibility(View.GONE);
                        binding.ClubContainer.setVisibility(View.GONE);
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
    public void ButtonClicked(View view) {
        if(binding.textViewError.getVisibility() != View.VISIBLE) {
            if (binding.ClubContainer.getVisibility() == View.VISIBLE) {
                binding.recyclerViewSquad.setVisibility(View.VISIBLE);
                binding.ClubContainer.setVisibility(View.GONE);
            } else if (binding.recyclerViewSquad.getVisibility() == View.VISIBLE) {
                binding.recyclerViewSquad.setVisibility(View.GONE);
                binding.ClubContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void ButtonMatchClicked(View view) {
        String uuidString = binding.id.getText().toString();
        int uuid = Integer.parseInt(uuidString);
        TeamFragmentDirections.ActionTeamFragmentToMatchFragment action =
                TeamFragmentDirections.actionTeamFragmentToMatchFragment(uuid,true);
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void FavouriteChecked(View view) {
        ModelTeamFav teamFav = new ModelTeamFav(id,binding.teamFavourite.isChecked()); //Lepiej gdyby było w view modelu przenieś
       viewModel.updateTeamFav(teamFav);
       ButtonMatchClicked(view);
    }
}