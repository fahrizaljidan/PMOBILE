package com.pm.mycoin.view.fragment.debt

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pm.mycoin.R
import com.pm.mycoin.databinding.AddDialogLayoutBinding
import com.pm.mycoin.databinding.FilterDialogLayoutBinding
import com.pm.mycoin.databinding.FragmentDebtBinding
import com.pm.mycoin.db.Debt
import com.pm.mycoin.utils.DateUtil
import com.pm.mycoin.view.activity.main.MainActivity
import com.pm.mycoin.view.fragment.main.MainViewModel
import java.util.*

class DebtFragment : Fragment() {
    private lateinit var binding: FragmentDebtBinding
    private var adapter: DebtAdapter? = null
    private var viewModel: MainViewModel? = null
    private var debts: List<Debt>? = null

    private var startDate: Date? = null
    private var endDate: Date? = null

    private var isNewest = true
    private var isFiltered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDebtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }
        populateRecycler()
        getAllDebts()

        binding.debtSort.setOnClickListener {
            isNewest = !isNewest

            debts = debts?.reversed()
            adapter?.setData(debts?.toMutableList())

            if (!isNewest) binding.debtSortImage.rotationX = 180.0.toFloat()
            else binding.debtSortImage.rotationX = 0.toFloat()

            if (!isNewest) binding.debtSortText.text = getString(R.string.sort_oldest)
            else binding.debtSortText.text = getString(R.string.sort_newest)
        }

        binding.debtFilter.setOnClickListener {
            if (isFiltered) {
                binding.debtFilterText.text = getString(R.string.filter)
                getAllDebts()
            } else {
                showFilterDialog()
            }

            isFiltered = !isFiltered
        }
    }

    private fun getAllDebts() {
        viewModel?.getAllDebts(isNewest)?.observe(viewLifecycleOwner, {
            debts = it
            adapter?.setData(it?.toMutableList())
        })
    }

    private fun populateRecycler() {
        adapter = DebtAdapter(null) { it, it1 ->
            if (it1 == "delete") {
                (parentFragment?.activity as MainActivity).reduceValue("", it.total.toLong())

                viewModel?.deleteDebt(it)
                Toast.makeText(context, R.string.toast_hapus_berhasil, Toast.LENGTH_SHORT).show()
            } else {
                showAddDataDialog(it)
            }
        }

        binding.debtRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.debtRecycler.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun showAddDataDialog(debt: Debt) {
        val builder = context?.let { AlertDialog.Builder(it) }
        val dialogView = AddDialogLayoutBinding.inflate(layoutInflater)

        dialogView.apply {
            dialogTitle.setText(debt.judul)
            dialogAmount.setText(debt.total.toString())
            dialogDate.text = "Transaction date: ${debt.date?.let { DateUtil.formatDate(it) }}"
            dialogCheckboxIncome.visibility = View.GONE
        }

        var selectedDate = debt.date
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogView.dialogShowDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                dialogView.dialogDate.text =
                    "Transaction date: ${DateUtil.formatDate(calendar.time)}"
                selectedDate = calendar.time
            }, year, month, day).show()
        }

        builder?.setView(dialogView.root)
        builder?.setCancelable(true)
        builder?.setPositiveButton(R.string.dialog_simpan, null)

        val dialog = builder?.create()
        dialog?.show()
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            if (dialogView.dialogTitle.text.isNotBlank() && dialogView.dialogAmount.text.isNotBlank()
                && selectedDate != null
            ) {
                val innerDebt = Debt(
                    debt.id, dialogView.dialogTitle.text.toString(),
                    dialogView.dialogAmount.text.toString().toInt(),
                    selectedDate
                )

                viewModel?.updateDebt(innerDebt)
                dialog.dismiss()
            } else {
                Toast.makeText(context, R.string.toast_isi_kolom, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFilterDialog() {
        val dialog = context?.let { AlertDialog.Builder(it) }
        val dialogView = FilterDialogLayoutBinding.inflate(layoutInflater)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        dialogView.filterStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                startDate = calendar.time
                dialogView.filterStartDate.text = DateUtil.formatDate(calendar.time)
            }, year, month, day).show()
        }

        dialogView.filterEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)
                endDate = calendar.time
                dialogView.filterEndDate.text = DateUtil.formatDate(calendar.time)
            }, year, month, day).show()
        }

        dialog?.setView(dialogView.root)
        dialog?.setCancelable(true)
        dialog?.setPositiveButton(R.string.dialog_simpan) { _, _ ->
            if (startDate != null && endDate != null) {
                viewModel?.getFilteredDebt(startDate!!, endDate!!, isNewest)
                    ?.observe(viewLifecycleOwner, {
                        debts = it
                        adapter?.setData(it?.toMutableList())

                        binding.debtFilterText.text = getString(R.string.remove_filter)
                    })
            }
        }

        dialog?.show()
    }
}