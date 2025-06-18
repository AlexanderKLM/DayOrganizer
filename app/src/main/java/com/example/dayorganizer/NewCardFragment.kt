package com.example.dayorganizer

import android.app.TimePickerDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.dayorganizer.databinding.NewCardFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.LocalTime
import com.google.firebase.auth.FirebaseAuth

interface OnCardSavedListener {
    fun onCardSaved(card: CardInfo)
}
class NewCardFragment (var cardInfo: CardInfo?, private val listener: OnCardSavedListener) : BottomSheetDialogFragment() {
    private lateinit var binding: NewCardFragmentBinding
    private lateinit var cardViewModel: CardViewModel
    private var timefill: LocalTime? = null
    private var datefill: LocalDate? = null
    private var priority: Int = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if (cardInfo != null) {
            binding.fragmentTitle.text = "Изменить задачу"
            binding.deleteButtonfragment.visibility = View.VISIBLE
            val editable = Editable.Factory.getInstance()
            binding.cardnamefragment.text = editable.newEditable(cardInfo!!.title)
            binding.carddescfragment.text = editable.newEditable(cardInfo!!.desc)
            if (cardInfo!!.timefill() != null) {
                timefill = cardInfo!!.timefill()!!
                updateTimeButtonText()
            }
            if (cardInfo!!.datefill() != null) {
                datefill = cardInfo!!.datefill()
                updateDateButtonText()
            }
        } else {
            binding.fragmentTitle.text = "Новая задача"
            binding.deleteButtonfragment.visibility = View.GONE
        }

        cardViewModel = ViewModelProvider(activity).get(CardViewModel::class.java)
        binding.saveButtonfragment.setOnClickListener {
            saveButton()
        }
        binding.timePickerButton.setOnClickListener {
            openTimePicker()
        }
        binding.datePickerButton.setOnClickListener {
            openDatePicker()
        }
        binding.priorityGroup.setOnCheckedChangeListener { _, checkedId ->
            priority = when (checkedId) {
                binding.priorityLow.id -> 1
                binding.priorityMedium.id -> 2
                binding.priorityHigh.id -> 3
                binding.priorityUrgent.id -> 4
                else -> 1
            }
            binding.choosepriority.text = when (priority) {
                1 -> "Выбранный приоритет: низкий"
                2 -> "Выбранный приоритет: средний"
                3 -> "Выбранный приоритет: высокий"
                4 -> "Выбранный приоритет: срочный"
                else -> "Выберите приоритет"
            }

        }
        cardInfo?.let {
            when (it.priority) {
                1 -> binding.priorityGroup.check(binding.priorityLow.id)
                2 -> binding.priorityGroup.check(binding.priorityMedium.id)
                3 -> binding.priorityGroup.check(binding.priorityHigh.id)
                4 -> binding.priorityGroup.check(binding.priorityUrgent.id)
            }
            priority = it.priority
            binding.deleteButtonfragment.setOnClickListener {
                deleteCard()
            }
        }
    }

    private fun openTimePicker() {
        if (timefill == null)
            timefill = LocalTime.now()
        val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            timefill = LocalTime.of(selectedHour, selectedMinute)
            updateTimeButtonText()
        }
        val time = timefill!!
        val dialog = TimePickerDialog(requireContext(), listener, time.hour, time.minute, true)
        dialog.setTitle("Выбрать время")
        dialog.show()

    }

    private fun openDatePicker() {
        if (datefill == null) {
            datefill = LocalDate.now()
        }
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            datefill = LocalDate.of(year, month + 1, dayOfMonth)
            updateDateButtonText()
        }

        val date = datefill!!
        val dialog = DatePickerDialog(
            requireContext(),
            listener,
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        )
        dialog.setTitle("Выбрать дату")
        dialog.show()
    }

    private fun updateTimeButtonText() {
        binding.timePickerButton.text =
            String.format("%02d:%02d", timefill!!.hour, timefill!!.minute)
    }

    private fun updateDateButtonText() {
        binding.datePickerButton.text = datefill?.format(CardInfo.dateFormatter) ?: "Выбрать дату"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NewCardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun saveButton() {
        val title = binding.cardnamefragment.text.toString().trim()
        val desc = binding.carddescfragment.text.toString().trim()
        val cardtime = if (timefill == null) null else CardInfo.timeFormatter.format(timefill)
        val carddate = datefill?.format(CardInfo.dateFormatter)

        if (title.isEmpty() || carddate == null || cardtime == null) {
            Toast.makeText(
                requireContext(),
                "Пожалуйста, заполните все поля",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (timefill == null) null else CardInfo.timeFormatter.format(timefill)

        priority = when (binding.priorityGroup.checkedRadioButtonId) {
            binding.priorityLow.id -> 1
            binding.priorityMedium.id -> 2
            binding.priorityHigh.id -> 3
            binding.priorityUrgent.id -> 4
            else -> 1
        }


        if (cardInfo == null) {
            val newCard = CardInfo(
                0, title, desc, carddate, cardtime,
                priority,
                isdone = false,
                isoverdue = false,
                isrepeating = false,
                userid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            )
            cardViewModel.insertCard(newCard, requireContext())
                .observe(viewLifecycleOwner) { savedCard ->
                    listener.onCardSaved(savedCard)
                }
        } else {
            cardInfo!!.title = title
            cardInfo!!.desc = desc
            cardInfo!!.time = cardtime
            cardInfo!!.date = carddate
            cardInfo!!.priority = priority

            cardViewModel.updateCards(cardInfo!!).also {
                listener.onCardSaved(cardInfo!!)
            }
        }
        binding.cardnamefragment.setText("")
        binding.carddescfragment.setText("")
        dismiss()
    }

    private fun deleteCard() {
        cardInfo?.let { card ->
            cardViewModel.deleteCard(card)
        }
        dismiss()
    }
}

