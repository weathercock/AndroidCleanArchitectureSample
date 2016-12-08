package com.kazakago.cleanarchitecture.presentation.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kazakago.cleanarchitecture.R;
import com.kazakago.cleanarchitecture.databinding.FragmentAboutBinding;
import com.kazakago.cleanarchitecture.presentation.listener.presenter.fragment.AboutFragmentViewModelListener;
import com.kazakago.cleanarchitecture.presentation.presenter.fragment.AboutFragmentViewModel;

/**
 * About Fragment
 *
 * @author Kensuke
 */
public class AboutFragment extends Fragment implements AboutFragmentViewModelListener {

    private FragmentAboutBinding binding;
    private AboutFragmentViewModel viewModel;

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new AboutFragmentViewModel(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
            binding.setViewModel(viewModel);
        }
        return binding.getRoot();
    }

    /* AboutFragmentViewModelListener */

}
