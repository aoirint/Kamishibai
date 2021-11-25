package com.aoirint.kamishibai.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aoirint.kamishibai.musicregistry.Music

@Composable
fun MusicList(
    musics: List<Music>,
    onClick: ((music: Music) -> Unit)? = null,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(musics) { music ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = onClick != null,
                        onClick = {
                            onClick?.invoke(music)
                        },
                    ),
            ) {
                Text(
                    text = music.title ?: "",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }
        }
    }
}
