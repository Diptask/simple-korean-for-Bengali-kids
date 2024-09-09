package com.Nistha.banglatokoreanapp


import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Nistha.banglatokoreanapp.ui.theme.NisthasKoreanAppTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.*
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import coil.request.ImageRequest
import androidx.compose.foundation.Image
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.size.Size
import android.os.Build.VERSION.SDK_INT
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NisthasKoreanAppTheme {
                TranslationList(translations)
            }
        }
    }
}

data class Translation(val bengali: String, val korean: String, val audioFile: String)

val translations = listOf(
    Translation("হ্যালো", "안녕하세요", "Hello-annyeonghaseyo.mp3"),
    Translation("শুভ সকাল", "좋은 아침이에요", "good-morning-joeun-achimieyo.mp3"),
    Translation("কেমন আছেন?", "안녕하세요", "How are you-jal-jinaesyeosseoyo.mp3"),
    Translation("শুভ অপরাহ্ণ", "좋은 오후에요", "Good Afternoon.mp3"),
    Translation("শুভ রাত্রি", "잘 자요", "good night.mp3"),
    Translation("আপনার নাম কী?", "이름이 뭐에요?", "What is your name-ireumi-mwoyeyo.mp3"),
    // Add more translations as needed
)

@Composable
fun TranslationList(translations: List<Translation>) {
    var showGif by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        translations.forEach { translation ->
            TranslationItem(LocalContext.current, translation) {
                showGif = it
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showGif) {
            GifDisplay(onGifFinished = { showGif = false })
        }  // No arguments passed
}


@Composable
fun TranslationItem(context: Context, translation: Translation, onPlayButtonClicked: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Bengali: ${translation.bengali}", fontSize = 18.sp)
            Text(text = "Korean: ${translation.korean}", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            playAudio(context, translation.audioFile)
            onPlayButtonClicked(translation.bengali == "শুভ রাত্রি")
        }) {
            Text("Play")
        }
    }
}

fun playAudio(context: Context, audioFile: String) {
    val mediaPlayer = MediaPlayer()
    val afd = context.assets.openFd(audioFile)
    mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
    mediaPlayer.prepare()
    mediaPlayer.start()
}

@Composable
fun GifDisplay(modifier: Modifier = Modifier, onGifFinished: () -> Unit) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data("file:///android_asset/good-night.gif")
            .apply {
                size(Size.ORIGINAL)
            }
            .build(),
        imageLoader = imageLoader
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.fillMaxWidth().height(300.dp) // Adjust height as needed
    )

    // Use a Timer to control the display duration of the GIF
    LaunchedEffect(Unit) {
        delay(2000) // Adjust delay to match GIF length or desired display time
        onGifFinished()
    }
}





@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NisthasKoreanAppTheme {
        TranslationList(translations)
    }
}