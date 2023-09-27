package com.roger.myapplication

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import com.roger.myapplication.helper.StateBirds
import com.roger.myapplication.ui.app.BirdProfile
import com.roger.myapplication.ui.theme.IOExtendedComposeTheme

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class BirdDetailActivity : ComponentActivity() {

    private val bird: StateBirds by lazy {
        intent?.getSerializableExtra(BIRD_ID,StateBirds::class.java) as StateBirds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IOExtendedComposeTheme {
                BirdProfile(bird)
            }
        }
    }

    companion object {
        private const val BIRD_ID = "bird_id"
        fun newIntent(context: Context, bird: StateBirds) =
            Intent(context, BirdDetailActivity::class.java).apply {
                putExtra(BIRD_ID, bird)
            }
    }
}