package com.fct.compose.views.compose

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fct.compose.R
import com.fct.compose.data.LaunchEntity
import com.fct.compose.data.generateMetaDataString
import com.fct.compose.data.getComposePreviewLaunchEntity
import com.fct.compose.data.hasUrlLinks
import com.fct.compose.viewmodels.LaunchDetailsViewModel
import com.google.android.material.composethemeadapter.MdcTheme

private const val TAG = "composeLaunchDetails"

@Composable
fun LaunchDetailsScreen(
    viewModel: LaunchDetailsViewModel,
    entity: LaunchEntity = getComposePreviewLaunchEntity(),
    externalLinkCallBack: (url: String?) -> Unit
) {
    val metadata = entity.generateMetaDataString(LocalContext.current)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        DetailsScreenContent(entity, metadata)
        DetailsScreenSlideShow(viewModel, entity)

        if (entity.hasUrlLinks()) {
            DetailsExternalLinks(entity, externalLinkCallBack)
        }
    }
}

@Composable
fun DetailsScreenContent(
    entity: LaunchEntity,
    metadata: String
) {
    Column {

        // title
        Text(
            text = entity.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.spacing_normal))
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

        // meta data
        Spacer(Modifier.height(3.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entity.missionPatchSmall)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.mipmap.placeholder),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(26.dp)
                    .height(26.dp)
            )

            Text(
                text = metadata,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle2,
                color = colorResource(R.color.black_80),
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        // description
        if (!entity.details.isNullOrEmpty()) {
            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_normal)))
            Text(
                text = entity.details,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun DetailsScreenSlideShow(
    viewModel: LaunchDetailsViewModel,
    entity: LaunchEntity
) {

    if (!entity.flickrImages.isNullOrEmpty()) {

        val photoUrl by viewModel.photoUrlLiveData.observeAsState()
        val photoLoadingError by viewModel.photoLoadingError.observeAsState()

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            Text(
                text = stringResource(
                    R.string.details_available_photos,
                    viewModel.currentPhotoIndex,
                    viewModel.launchEntity?.flickrImages?.size ?: viewModel.currentPhotoIndex
                ),
                color = colorResource(R.color.black_80),
                style = MaterialTheme.typography.subtitle2,
                fontSize = 13.sp,
            )

            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl ?: entity.missionPatchSmall)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.mipmap.placeholder),
                    error = painterResource(R.mipmap.placeholder),
                    contentDescription = null,
                    onError = {
                        viewModel.setPhotoLoadingError(
                            //FIXME compose wont allow stringResource to be used here since its out of context
                            "Image Loading Error: ${it.result.throwable.message}"
//                            stringResource(
//                                R.string.image_loading_error,
//                                it.result.throwable.message.toString()
//                            )
                        )
                        Log.e(TAG, it.toString(), it.result.throwable)
                    },
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .padding(top = 6.dp, start = dimensionResource(R.dimen.spacing_large), end = dimensionResource(R.dimen.spacing_large))
                        .fillMaxWidth()
                        .clickable {

                            // will trigger observable in viewModel, which will cause a recomposition
                            viewModel.triggerNextLaunchPhoto()
                        })

                // display image loading error
                if (!photoLoadingError.isNullOrBlank()) {
                    Text(
                        text = photoLoadingError.toString(),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(R.dimen.spacing_large), start = dimensionResource(R.dimen.spacing_xlarge), end = dimensionResource(R.dimen.spacing_xlarge)),
                        style = MaterialTheme.typography.body1,
                    )
                }

            }
        }
    }
}

@Composable
fun DetailsExternalLinks(
    entity: LaunchEntity,
    externalLinkCallBack: (url: String?) -> Unit
) {

    // external links
    Spacer(Modifier.height(dimensionResource(R.dimen.spacing_medium)))
    Text(
        text = stringResource(R.string.details_external_links),
        style = MaterialTheme.typography.subtitle2
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(top =dimensionResource(R.dimen.spacing_normal)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,

        ) {

        // reddit
        entity.redditUrl?.let {
            OutlinedButton(
                onClick = { externalLinkCallBack(it) },
                modifier = Modifier.height(35.dp).width(75.dp)
            ) {
                Text(text = stringResource(R.string.reddit), fontSize = 12.sp)
            }
        }

        // wiki
        entity.wikiUrl?.let {
            OutlinedButton(
                onClick = { externalLinkCallBack(entity.wikiUrl) },
                modifier = Modifier.height(35.dp).width(100.dp)
            ) {
                Text(text = stringResource(R.string.wikipedia), fontSize = 12.sp)
            }
        }

        // webcast
        entity.webCastUrl?.let {
            OutlinedButton(
                onClick = { externalLinkCallBack(entity.webCastUrl) },
                modifier = Modifier.height(35.dp).width(60.dp)
            ) {
                Text(
                    text = stringResource(R.string.webcast),
                    fontSize = 12.sp
                )
            }
        }

        // article
        entity.articleUrl?.let {
            OutlinedButton(
                onClick = { externalLinkCallBack(entity.articleUrl) },
                modifier = Modifier.height(35.dp).width(80.dp)
            ) {
                Text(text = stringResource(R.string.article), fontSize = 12.sp)
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 420, heightDp = 720)
@Composable
fun DefaultPreview() {
    MdcTheme {
        val entity = getComposePreviewLaunchEntity()
        LaunchDetailsScreen(LaunchDetailsViewModel(), entity) {}
    }
}