package com.example.apiretrofit.ui.share

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.ProjectReportAdapter
import com.example.apiretrofit.api.model.ProjectReportResponse
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class ReportActivity : AppCompatActivity() {
    private lateinit var barChart: BarChart
    private lateinit var api: ApiService
    private var projectID: Int = -1
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvReport: RecyclerView
    private lateinit var reportAdapter: ProjectReportAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvReport = findViewById(R.id.recyclerView)
        rvReport.layoutManager = LinearLayoutManager(this)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        barChart = findViewById(R.id.barChart)
        barChart.description.isEnabled = false
        barChart.setFitBars(true)

        api = ApiClient.getApiService(this)
        projectID = intent.getIntExtra("project_id", -1)
        if (projectID == -1) {
            Toast.makeText(this, "ID proyek tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchReportData(projectID)

        swipeRefresh.setOnRefreshListener {
            fetchReportData(projectID)
        }
    }

    private fun fetchReportData(projectId: Int) {
        swipeRefresh.isRefreshing = true
        api.getProjectReport(projectId).enqueue(object : Callback<List<ProjectReportResponse>> {
            override fun onResponse(
                call: Call<List<ProjectReportResponse>>,
                response: Response<List<ProjectReportResponse>>
            ) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    val reportList = response.body()
                    reportList?.let {
                        setupChart(it)

                        // Set data ke RecyclerView
                        val adapter = ProjectReportAdapter(it)
                        rvReport.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@ReportActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    Log.e("ReportActivity", "Gagal: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ProjectReportResponse>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Log.e("ReportActivity", "Error: ${t.message}", t)
                Toast.makeText(this@ReportActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupChart(dataList: List<ProjectReportResponse>) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        val estimatedTimeLefts = mutableListOf<String>()
        val colors = mutableListOf<Int>()

        dataList.forEachIndexed { index, data ->
            val progress = data.progress.toFloatOrNull() ?: 0f
            val estimatedDays = data.estimatedTimeLeft.filter { it.isDigit() }.toIntOrNull() ?: 0

            entries.add(BarEntry(index.toFloat(), progress)) // progress dalam persen
            labels.add(data.teamName)
            estimatedTimeLefts.add("$estimatedDays days left")

            // Warna merah jika waktu tersisa 0, biru kalau masih ada
            if (estimatedDays == 0 && progress.toInt() != 100) {
                colors.add(Color.RED)
            } else {
                colors.add(Color.BLUE)
            }
        }

        val dataSet = BarDataSet(entries, "Team Progress (%)")
        dataSet.colors = colors // Set warna berdasarkan kondisi
        dataSet.valueTextSize = 12f
        dataSet.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                val index = barEntry?.x?.toInt() ?: 0
                val progress = barEntry?.y?.toInt() ?: 0
                val timeLeft = estimatedTimeLefts.getOrNull(index) ?: "-"
                return "$progress% ($timeLeft)"
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.labelRotationAngle = -15f
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.axisMaximum = 100f
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.invalidate()
    }

}
