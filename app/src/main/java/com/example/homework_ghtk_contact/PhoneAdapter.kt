package com.example.homework_ghtk_contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_ghtk_contrac.databinding.ItemPhoneBinding

class PhoneAdapter : RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder>(){
    var list : MutableList<String> = mutableListOf()
    private val selectedPhones = mutableSetOf<String>()
    inner class PhoneViewHolder(private var binding : ItemPhoneBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(phoneNumber: String) {
            binding.tvPhone.text = phoneNumber
            binding.cbSubscribe.setOnCheckedChangeListener { _, isChecked ->
                val phoneNumber = binding.tvPhone.text.toString()
                if (isChecked) {
                    selectedPhones.add(phoneNumber)
                } else {
                    selectedPhones.remove(phoneNumber)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        val binding = ItemPhoneBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PhoneViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {
        holder.bind(list[position])
    }
    fun addData(listPhone : List<String>){
        list.addAll(listPhone)
        notifyDataSetChanged()
    }

    fun getSelectedPhones(): List<String> = selectedPhones.toList()

    fun clearData() {
        list.clear()
    }
}