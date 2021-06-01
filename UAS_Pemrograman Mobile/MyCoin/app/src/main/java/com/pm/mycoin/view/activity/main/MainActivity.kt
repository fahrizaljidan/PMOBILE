package com.pm.mycoin.view.activity.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ajts.androidmads.library.SQLiteToExcel
import com.pm.mycoin.R
import com.pm.mycoin.databinding.ActivityMainBinding
import com.pm.mycoin.databinding.SaldoDialogLayoutBinding
import com.pm.mycoin.utils.CurencyFormatter

import com.pm.mycoin.view.fragment.main.MainFragment
import com.pm.mycoin.view.fragment.main.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var excelConverter: SQLiteToExcel

    private val directoryPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
    private val tableList = arrayListOf("record_table", "debt_table")
    private var permissionGranted = false
    private var alreadyConverted = false

    private var totalExpenses = 0L
    private var totalIncome = 0L
    private var totalDebt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.title = null

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )


        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = true
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)




        supportFragmentManager.beginTransaction().replace(R.id.fragment, MainFragment()).commit()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getTotalExpenses()?.observe(this, {
            if (it != null) {
                totalExpenses = it.toLong()
            }
        })

        viewModel.getTotalIncome()?.observe(this, {
            if (it != null) {
                totalIncome = it.toLong()
            }
        })

        viewModel.getTotalDebt()?.observe(this, {
            if (it != null) {
                totalDebt = it.toLong()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reset -> showDialog()
            R.id.action_saldo -> showBalanceDialog()

        }

        return true
    }



    private fun showBalanceDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = SaldoDialogLayoutBinding.inflate(layoutInflater)

        dialogView.apply {
            dialogMainIncome.text = CurencyFormatter.convertAndFormat(totalIncome)
            dialogMainExpenses.text = CurencyFormatter.convertAndFormat(
                totalExpenses
            )
            dialogMainDebt.text = CurencyFormatter.convertAndFormat(totalDebt)
            dialogMainSaldo.text =
                CurencyFormatter.convertAndFormat(totalIncome - (totalExpenses + totalDebt))
        }

        dialog.setView(dialogView.root)
        dialog.setTitle(R.string.dialog_title_saldo)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Close", null)
        dialog.show()
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.perhatian))
        dialog.setMessage(R.string.dialog_message)
        dialog.setCancelable(true)
        dialog.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAllRecord()
            viewModel.deleteAllDebt()

            totalIncome = 0
            totalExpenses = 0
            totalDebt = 0
        }
        dialog.setNegativeButton("Cancel") { innerDialog, _ ->
            innerDialog.dismiss()
        }

        dialog.show()
    }

    fun reduceValue(key: String, amount: Long) {
        when (key) {
            "income" -> totalIncome -= amount
            "expenses" -> totalExpenses -= amount
            else -> totalDebt -= amount
        }
    }




}
