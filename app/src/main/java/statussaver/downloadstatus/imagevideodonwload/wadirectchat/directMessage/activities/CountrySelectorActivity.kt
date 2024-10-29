package statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityCountrySelectorBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.adapters.CountriesAdapter
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.model.Country
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.JSON_FILE_NAME
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.loadCountriesFromJson
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.Constants
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils

class CountrySelectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountrySelectorBinding
    private lateinit var adapter: CountriesAdapter
    private lateinit var countryList: ArrayList<Country>
    private lateinit var filteredList: ArrayList<Country>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountrySelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        countryList = loadCountriesFromJson(JSON_FILE_NAME)
        filteredList = ArrayList()
        filteredList.addAll(countryList)

        adapter =
            CountriesAdapter(
                filteredList,
                object : OnItemClickListener {
                    override fun onItemClick(p0: AdapterView<*>?, view: View?, pos: Int, p3: Long) {
                        countrySelected(pos)
                    }
                })

        binding.recycler.adapter = adapter
        binding.scroller.attachRecyclerView(binding.recycler)
        binding.scroller.setSectionIndexer(adapter)

        binding.etSearchCountry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnClear.visibility = if (s.toString().isEmpty()) GONE else VISIBLE
                filter(s.toString())
            }
        })

        binding.btnClear.setOnClickListener {
            binding.etSearchCountry.setText("")
            binding.btnClear.visibility = GONE
            adapter.updateCountries(countryList)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun filter(text: String) {
        filteredList.clear()
        for (item in countryList) {
            if (item.name.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        }
        adapter.updateCountries(filteredList)
    }

    private fun countrySelected(position: Int) {
        SharedPrefUtils.putPrefString(
            Constants.SELECTED_COUNTRY_PREF_KEY,
            filteredList[position].isoCode
        )
        SharedPrefUtils.putPrefInt(Constants.SELECTED_COUNTRY_DEVICE_DEFAULT_PREF_KEY, 0)
        Log.d("countrySelector", "countrySelected: " + filteredList[position].isoCode)
        finish()
    }
}