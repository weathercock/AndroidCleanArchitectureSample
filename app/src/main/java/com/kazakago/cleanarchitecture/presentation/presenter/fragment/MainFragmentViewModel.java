package com.kazakago.cleanarchitecture.presentation.presenter.fragment;

import android.content.Context;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.kazakago.cleanarchitecture.CleanApplication;
import com.kazakago.cleanarchitecture.R;
import com.kazakago.cleanarchitecture.domain.model.city.CityModel;
import com.kazakago.cleanarchitecture.domain.model.weather.ForecastModel;
import com.kazakago.cleanarchitecture.domain.model.weather.LocationModel;
import com.kazakago.cleanarchitecture.domain.model.weather.WeatherModel;
import com.kazakago.cleanarchitecture.domain.usecase.CityUseCase;
import com.kazakago.cleanarchitecture.domain.usecase.WeatherUseCase;
import com.kazakago.cleanarchitecture.presentation.listener.presenter.fragment.MainFragmentViewModelListener;
import com.kazakago.cleanarchitecture.presentation.listener.view.adapter.ForecastRecyclerAdapterListener;
import com.kazakago.cleanarchitecture.presentation.presenter.adapter.CityViewModel;
import com.kazakago.cleanarchitecture.presentation.presenter.adapter.ForecastViewModel;
import com.kazakago.cleanarchitecture.presentation.view.adapter.CitySpinnerAdapter;
import com.kazakago.cleanarchitecture.presentation.view.adapter.ForecastRecyclerAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import icepick.Icepick;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Main Fragment ViewModel
 *
 * @author Kensuke
 */
public class MainFragmentViewModel implements ForecastRecyclerAdapterListener {

    public ObservableField<String> area;
    public ObservableField<String> prefecture;
    public ObservableField<String> city;
    public ObservableField<String> publicTime;
    public ObservableField<CitySpinnerAdapter> citySpinnerAdapter;
    public ObservableField<ForecastRecyclerAdapter> forecastRecyclerAdapter;

    private Context context;
    private MainFragmentViewModelListener listener;
    private CompositeDisposable compositeDisposable;

    @State
    public int selectedPosition;

    @Inject
    public WeatherUseCase weatherUseCase;
    @Inject
    public CityUseCase cityUseCase;

    public MainFragmentViewModel(Context context, MainFragmentViewModelListener listener) {
        CleanApplication.applicationComponent.inject(this);
        this.area = new ObservableField<>();
        this.prefecture = new ObservableField<>();
        this.city = new ObservableField<>();
        this.publicTime = new ObservableField<>();
        CitySpinnerAdapter citySpinnerAdapter = new CitySpinnerAdapter(context);
        try {
            citySpinnerAdapter.setCityViewModelList(getCityViewModelList(cityUseCase.findAll()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.citySpinnerAdapter = new ObservableField<>(citySpinnerAdapter);
        ForecastRecyclerAdapter forecastRecyclerAdapter = new ForecastRecyclerAdapter(context);
        forecastRecyclerAdapter.setForecastRecyclerAdapterListener(this);
        this.forecastRecyclerAdapter = new ObservableField<>(forecastRecyclerAdapter);
        this.listener = listener;
        this.context = context;
        this.selectedPosition = 0;
    }

    public void onCreate(Bundle savedInstanceState) {
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    public void onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            new Handler().post(() -> listener.setCitySpinnerSelection(selectedPosition));
        }
    }

    public void onStart() {
        compositeDisposable = new CompositeDisposable();
        refreshView();
    }

    public void onStop() {
        compositeDisposable.dispose();
    }

    public void onSaveInstanceState(Bundle outState) {
        Icepick.saveInstanceState(this, outState);
    }

    public void onClickRefresh(View view) {
        fetchWeather();
    }

    public void onCitySelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPosition = position;
        fetchWeather();
    }

    private void fetchWeather() {
        String cityId = citySpinnerAdapter.get().getItem(selectedPosition).id.get();
        compositeDisposable.add(weatherUseCase.fetch(cityId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        weather -> {
                            refreshView(weather);
                            showToast(context.getString(R.string.refresh_complete));
                        },
                        error -> {
                            refreshView();
                            showToast(error.getLocalizedMessage());
                        }
                ));
    }

    private void refreshView() {
        String cityId = citySpinnerAdapter.get().getItem(selectedPosition).id.get();
        refreshView(weatherUseCase.find(cityId));
    }

    private void refreshView(WeatherModel weather) {
        if (weather != null) {
            LocationModel location = weather.getLocation();
            if (location != null) {
                area.set(location.getArea());
                prefecture.set(location.getPrefecture());
                city.set(location.getCity());
            }
            try {
                publicTime.set(context.getString(R.string.public_time, getFormattedTime(weather.getPublicTime())));
            } catch (ParseException e) {
                e.printStackTrace();
                publicTime.set(null);
            }
            listener.setActionBarTitle(weather.getTitle());

            forecastRecyclerAdapter.get().setForecastViewModelList(getForecastViewModelList(weather.getForecasts()));
            forecastRecyclerAdapter.get().notifyDataSetChanged();
        }
    }

    private String getFormattedTime(String timeStr) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSSZ", Locale.getDefault());
        return getFormattedTime(formatter.parse(timeStr).getTime());
    }

    private String getFormattedTime(Long timestamp) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        return dateFormat.format(timestamp) + " " + timeFormat.format(timestamp);
    }

    private List<CityViewModel> getCityViewModelList(Observable<CityModel> cityObservable) {
        return cityObservable
                .map(cityModel -> new CityViewModel(context, cityModel))
                .toList()
                .blockingGet();
    }

    private List<ForecastViewModel> getForecastViewModelList(List<ForecastModel> forecastList) {
        return Observable.fromIterable(forecastList)
                .map(forecast -> new ForecastViewModel(context, forecast))
                .toList()
                .blockingGet();
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /* ForecastRecyclerAdapterListener */

    @Override
    public void onItemClick(ForecastViewModel forecastViewModel) {
        showToast(forecastViewModel.telop.get());
    }

}
