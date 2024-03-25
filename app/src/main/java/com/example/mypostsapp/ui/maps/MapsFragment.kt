package com.example.mypostsapp.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mypostsapp.R
import com.example.mypostsapp.databinding.FragmentDashboardBinding
import com.example.mypostsapp.entities.Post
import com.example.mypostsapp.ui.posts.MarkerPostDialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import java.util.Locale


class MapsFragment : Fragment() {

    private var mapViewModel: MapViewModel ?= null
    private var _binding: FragmentDashboardBinding? = null
    private var myLocation: Location ?= null
    private var mMap: GoogleMap ?= null
    private var address: Address ?= null

    private val binding get() = _binding!!

    private var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findMyLocationIfHasAccess()
        val mapFragment =  childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync(OnMapReadyCallback { googleMap: GoogleMap ->
            mMap = googleMap
            mMap?.uiSettings?.isZoomControlsEnabled = true
            mMap?.setOnMarkerClickListener { p0 ->
                if (p0.tag != -1) {
                    showPostByClick(p0.tag as? Int)
                }
                p0.tag != -1
            }
            mapViewModel?.postsLD?.observe(viewLifecycleOwner) {
                it.forEachIndexed { index, post ->
                    markLocation(post, index)
                }
            }
        })

    }

    private fun showPostByClick(tag: Int?) {
        tag?.let { position ->
            mapViewModel?.postsLD?.value?.get(position)?.let {
                MarkerPostDialogFragment.newInstance(it).show(this.childFragmentManager, "")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findMyLocationIfHasAccess() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            findMyLocation()
        }
    }

    private fun findMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener(
                    requireActivity(),
                    OnSuccessListener<Location> { location -> // Got last known location. In some rare situations this can be null.
                        myLocation = location
                        markMyLocation()
                    })
        }
    }

    private fun markMyLocation() {
        val latitude: Double = myLocation?.latitude ?: 0.0
        val longitude: Double = myLocation?.longitude ?: 0.0
        val latLng = LatLng(latitude, longitude)
        val marker = mMap?.addMarker(MarkerOptions().title(getString(R.string.my_Location)).position(latLng))
        marker?.tag = -1
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val address = geocoder.getFromLocation(latitude, longitude, 1)!![0]
            this.address = address
        } catch (e: Exception) {
        }
    }

    private fun markLocation(post: Post, position: Int) {
        mMap?.let {map->
            post.let { post ->
                val latitude: Double = post.location?.latitude ?: 0.0
                val longitude: Double = post.location?.longitude ?: 0.0
                val latLng = LatLng(latitude, longitude)
                val marker = map.addMarker(MarkerOptions().title(post.createdUser?.name).position(latLng))
                marker?.tag = position
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                try {
                    val address = geocoder.getFromLocation(latitude, longitude, 1)!![0]
                    this.address = address
                } catch (e: Exception) {
                }
            }
        }

    }
}