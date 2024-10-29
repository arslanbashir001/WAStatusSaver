package statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.model.Country
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.directMessage.utils.getDrawableFromAssets

class CountriesAdapter(
    private var countryList: ArrayList<Country>,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<CountriesAdapter.CountryHolder>(), FastScroller.SectionIndexer {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.country_list_item, parent, false)
        return CountryHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: CountryHolder, position: Int) {
        val country = countryList[position]
//

        Log.d("countryName", "onBindViewHolder: " + country.name)

        holder.flag.setImageDrawable(holder.itemView.context.getDrawableFromAssets(country.flagResource))
        holder.countryName.text = country.name
        holder.isdCode.text = country.isdCode

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(null, holder.itemView, position, 0)
        }

    }


    override fun getItemCount(): Int = countryList.size

    override fun getSectionText(position: Int): CharSequence = countryList[position].isoCode

    fun updateCountries(newCountries: ArrayList<Country>) {
        countryList = newCountries
        notifyDataSetChanged() // Notify the adapter to refresh the data
    }

    class CountryHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val countryName: TextView = itemView.findViewById(R.id.country_name)
        val isdCode: TextView = itemView.findViewById(R.id.country_code)
        val flag: ImageView = itemView.findViewById(R.id.flag)
    }
}