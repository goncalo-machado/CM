package com.example.composearticle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composearticle.ui.theme.ComposeArticleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeArticleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ArticlePage(
                        imageResourceId = R.drawable.bg_compose_background,
                        titleResourceId = R.string.title,
                        firstParagraphResourceId = R.string.first_paragraph,
                        secondParagraphResourceId = R.string.second_paragraph,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ArticleImage(imageResourceId: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(imageResourceId),
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
fun ArticleText(
    titleResourceId: Int,
    firstParagraphResourceId: Int,
    secondParagraphResourceId: Int,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(titleResourceId),
            fontSize = 24.sp,
            modifier = modifier.padding(16.dp)
        )
        Text(
            text = stringResource(firstParagraphResourceId),
            textAlign = TextAlign.Justify,
            modifier = modifier.padding(16.dp)
        )
        Text(
            text = stringResource(secondParagraphResourceId),
            textAlign = TextAlign.Justify,
            modifier = modifier.padding(16.dp)
        )
    }
}

@Composable
fun ArticlePage(
    imageResourceId: Int,
    titleResourceId: Int,
    firstParagraphResourceId: Int,
    secondParagraphResourceId: Int,
    modifier: Modifier = Modifier
) {
    Column {
        ArticleImage(imageResourceId, modifier)
        ArticleText(titleResourceId, firstParagraphResourceId, secondParagraphResourceId, modifier)
    }
}

@Preview(showBackground = true)
@Composable
fun ArticlePreview() {
    ComposeArticleTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ArticlePage(
                imageResourceId = R.drawable.bg_compose_background,
                titleResourceId = R.string.title,
                firstParagraphResourceId = R.string.first_paragraph,
                secondParagraphResourceId = R.string.second_paragraph,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}