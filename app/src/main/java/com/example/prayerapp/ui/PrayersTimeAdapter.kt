package com.example.prayerapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prayerapp.databinding.ItemPrayerBinding
import com.example.prayerapp.domain.models.PrayersTiming
import java.text.SimpleDateFormat
import java.util.Locale


class PrayersTimeAdapter : RecyclerView.Adapter<PrayersTimeAdapter.PrayersViewHolder>() {

    private var listPrayers = ArrayList<PrayersTiming>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayersViewHolder {
        val binding =
            ItemPrayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrayersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listPrayers.size
    }

    override fun onBindViewHolder(holder: PrayersViewHolder, position: Int) {
        val prayer = listPrayers[position]

        holder.bind(prayer)
    }

    fun updateListPrayers(newListPrayers: ArrayList<PrayersTiming>) {
        listPrayers = newListPrayers
        notifyDataSetChanged()
    }

    inner class PrayersViewHolder(private val binding: ItemPrayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(prayer: PrayersTiming) {
            binding.tvPrayerName.text = prayer.prayersName
            binding.tvPrayersTime.text = convertTo12HourFormat(prayer.prayersTime.split(" ")[0])
//            binding.tvTimeType.text = prayer.timeType
        }

        private fun convertTo12HourFormat(time: String): String {
            val inputFormat = SimpleDateFormat("HH:mm",Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val date = inputFormat.parse(time)
            return outputFormat.format(date)
        }
    }


}