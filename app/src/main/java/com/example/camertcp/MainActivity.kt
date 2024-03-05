package com.example.camertcp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.camertcp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initModel()

        binding.btnRequestApInfo.setOnClickListener {
            viewModel.requestAPInfo()
        }

        binding.btnChangeSn.setOnClickListener {
            viewModel.changeSN(OQCInfo.SERIAL_NUMBER)
        }

        binding.btnGetSn.setOnClickListener {
            viewModel.getSN()
        }

        binding.btnGetCameraInfo.setOnClickListener {
            viewModel.getCameraInfo()
        }

        binding.btnRequestALS.setOnClickListener {
            viewModel.requestALS()
        }

        binding.btnRequestBell.setOnClickListener {
            viewModel.requestBell()
        }
    }

    private fun initModel() = with(viewModel){
        liveResult.observe(this@MainActivity){
            binding.tvResult.append(it)
        }
    }
}

