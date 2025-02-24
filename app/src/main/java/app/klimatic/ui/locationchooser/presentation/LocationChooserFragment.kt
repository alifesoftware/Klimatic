package app.klimatic.ui.locationchooser.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import app.klimatic.R
import app.klimatic.data.model.weather.Location
import app.klimatic.ui.base.BaseFragment
import app.klimatic.ui.home.HomeActivity
import app.klimatic.ui.locationchooser.presentation.adapter.LocationAdapter
import app.klimatic.ui.search.presentation.viewmodel.SearchViewModel
import app.klimatic.ui.utils.handleState
import kotlinx.android.synthetic.main.fragment_location_chooser.etSearchQuery
import kotlinx.android.synthetic.main.fragment_location_chooser.rvLocations
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationChooserFragment : BaseFragment() {

    private val searchViewModel by viewModel<SearchViewModel>()

    override fun getLayoutResource(): Int = R.layout.fragment_location_chooser

    private val locationAdapter by lazy {
        LocationAdapter(onItemClickAction)
    }

    override fun setupView(view: View, savedInstanceState: Bundle?) {
        setupLocationsRecyclerView()
        setupObservers()
        setupSearch()
    }

    private fun setupSearch() {
        etSearchQuery.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(query)) {
                    if (query!!.length > 2) {
                        searchViewModel.searchLocation(query.toString())
                    }
                } else {
                    searchViewModel.searchLocation()
                }
            }
        })
    }

    private fun setupLocationsRecyclerView() {
        rvLocations?.run {
            layoutManager =
                LinearLayoutManager(context)
            adapter = locationAdapter
        }
    }

    private fun setupObservers() {
        searchViewModel.locations.observe(this, Observer { state ->
            handleState(
                state,
                {
                    locationAdapter.submitList(it)
                },
                { errorCode: Int? ->
                    Log.d(javaClass.name, "Error: $errorCode")
                }
            )
        })
    }

    var onItemClickAction: (location: Location) -> Unit = { location ->
        if (!TextUtils.isEmpty(location.name)) {
            searchViewModel.setCurrentSelectedLocation(location.name!!)
        }
        HomeActivity.launchSingleTask(requireContext())
    }
}
